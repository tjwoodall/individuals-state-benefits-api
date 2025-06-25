/*
 * Copyright 2023 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package v2.amendBenefitAmounts

import shared.connectors.{ConnectorSpec, DownstreamOutcome}
import shared.models.domain.{Nino, TaxYear}
import shared.models.outcomes.ResponseWrapper
import uk.gov.hmrc.http.StringContextOps
import v2.amendBenefitAmounts.def1.model.request.{Def1_AmendBenefitAmountsRequestBody, Def1_AmendBenefitAmountsRequestData}
import v2.amendBenefitAmounts.model.request.AmendBenefitAmountsRequestData
import v2.models.domain.BenefitId

import scala.concurrent.Future

class AmendBenefitAmountsConnectorSpec extends ConnectorSpec {

  private val nino      = "AA123456A"
  private val benefitId = "123e4567-e89b-12d3-a456-426614174000"

  private val body = Def1_AmendBenefitAmountsRequestBody(999.99, Some(123.13))

  "AmendBenefitAmountsConnector" should {
    "return the expected response for a non-TYS request" when {
      "a valid request is made" in new IfsTest with Test {
        def taxYear: TaxYear = TaxYear.fromMtd("2020-21")

        val expectedOutcome: Right[Nothing, ResponseWrapper[Unit]] = Right(ResponseWrapper(correlationId, ()))

        willPut(
          url = url"$baseUrl/income-tax/income/state-benefits/$nino/2020-21/$benefitId",
          body = body
        ).returns(Future.successful(expectedOutcome))

        val result: DownstreamOutcome[Unit] = await(connector.amendBenefitAmounts(request))
        result shouldBe expectedOutcome
      }
    }

    "return the expected response for a TYS request" when {
      "a valid request is made" in new IfsTest with Test {
        def taxYear: TaxYear = TaxYear.fromMtd("2023-24")

        val expectedOutcome: Right[Nothing, ResponseWrapper[Unit]] = Right(ResponseWrapper(correlationId, ()))

        willPut(
          url = url"$baseUrl/income-tax/23-24/income/state-benefits/$nino/$benefitId",
          body = body
        ).returns(Future.successful(expectedOutcome))

        val result: DownstreamOutcome[Unit] = await(connector.amendBenefitAmounts(request))
        result shouldBe expectedOutcome
      }
    }
  }

  trait Test { _: ConnectorTest =>
    def taxYear: TaxYear

    val request: AmendBenefitAmountsRequestData = Def1_AmendBenefitAmountsRequestData(Nino(nino), taxYear, BenefitId(benefitId), body)

    val connector: AmendBenefitAmountsConnector = new AmendBenefitAmountsConnector(mockHttpClient, mockSharedAppConfig)

  }

}
