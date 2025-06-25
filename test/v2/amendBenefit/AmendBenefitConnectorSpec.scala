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

package v2.amendBenefit

import shared.config.MockSharedAppConfig
import shared.connectors.{ConnectorSpec, DownstreamOutcome}
import shared.mocks.MockHttpClient
import shared.models.domain.{Nino, TaxYear}
import shared.models.outcomes.ResponseWrapper
import uk.gov.hmrc.http.StringContextOps
import v2.amendBenefit.def1.model.request.{Def1_AmendBenefitRequestBody, Def1_AmendBenefitRequestData}
import v2.models.domain.BenefitId

import scala.concurrent.Future

class AmendBenefitConnectorSpec extends ConnectorSpec {

  private val nino      = "AA123456A"
  private val taxYear   = "2021-22"
  private val benefitId = "123e4567-e89b-12d3-a456-426614174000"

  private val amendBenefitRequestBody = Def1_AmendBenefitRequestBody("2020-08-03", Some("2020-12-03"))

  private val request = Def1_AmendBenefitRequestData(Nino(nino), TaxYear.fromMtd(taxYear), BenefitId(benefitId), amendBenefitRequestBody)

  trait Test extends MockHttpClient with MockSharedAppConfig {

    val connector: AmendBenefitConnector = new AmendBenefitConnector(
      http = mockHttpClient,
      appConfig = mockSharedAppConfig
    )
  }

  "AmendBenefitConnector" when {
    "amendBenefit" must {
      "return a 201 status for a success scenario" in new IfsTest with Test {
        val outcome: Right[Nothing, ResponseWrapper[Unit]] = Right(ResponseWrapper(correlationId, ()))

        willPut(
            url = url"$baseUrl/income-tax/income/state-benefits/$nino/$taxYear/custom/$benefitId",
            body = request.body
          )
          .returns(Future.successful(outcome))

        val result: DownstreamOutcome[Unit] = await(connector.amendBenefit(request))

        result shouldBe outcome
      }
    }
  }

}
