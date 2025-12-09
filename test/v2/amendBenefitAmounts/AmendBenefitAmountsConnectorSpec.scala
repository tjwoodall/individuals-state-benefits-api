/*
 * Copyright 2025 HM Revenue & Customs
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

import org.scalamock.handlers.CallHandler
import play.api.Configuration
import shared.connectors.{ConnectorSpec, DownstreamOutcome}
import shared.models.domain.{Nino, TaxYear}
import shared.models.outcomes.ResponseWrapper
import uk.gov.hmrc.http.StringContextOps
import v2.amendBenefitAmounts.def1.model.request.{Def1_AmendBenefitAmountsRequestBody, Def1_AmendBenefitAmountsRequestData}
import v2.amendBenefitAmounts.model.request.AmendBenefitAmountsRequestData
import v2.models.domain.BenefitId

import java.net.URL
import scala.concurrent.Future

class AmendBenefitAmountsConnectorSpec extends ConnectorSpec {

  private val nino      = "AA123456A"
  private val benefitId = "123e4567-e89b-12d3-a456-426614174000"

  private val body = Def1_AmendBenefitAmountsRequestBody(999.99, Some(123.13))

  private val preTysTaxYear = TaxYear.fromMtd("2020-21")
  private val tysTaxYear = TaxYear.fromMtd("2023-24")

  "AmendBenefitAmountsConnector" should {
    "return the expected response for a non-TYS request" when {
      "a valid request is made" in new IfsTest with Test {
        def taxYear: TaxYear = preTysTaxYear

        val expectedOutcome: Right[Nothing, ResponseWrapper[Unit]] = Right(ResponseWrapper(correlationId, ()))

        willPut(
          url = url"$baseUrl/income-tax/income/state-benefits/$nino/2020-21/$benefitId",
          body = body
        ).returns(Future.successful(expectedOutcome))

        val result: DownstreamOutcome[Unit] = await(connector.amendBenefitAmounts(request))
        result shouldBe expectedOutcome
      }
    }

    "return the expected response for a TYS request and feature switch is disabled (IFS enabled)" when {
      "a valid request is made" in new IfsTest with Test {
        def taxYear: TaxYear = tysTaxYear

        val expectedOutcome: Right[Nothing, ResponseWrapper[Unit]] = Right(ResponseWrapper(correlationId, ()))

        stubTysHttpResponse(isHipEnabled = false, outcome = expectedOutcome)

        val result: DownstreamOutcome[Unit] = await(connector.amendBenefitAmounts(request))
        result shouldBe expectedOutcome
      }
    }

    "return the expected response for a TYS request and feature switch is enabled (HIP enabled)" when {
      "a valid request is made" in new HipTest with Test {
        def taxYear: TaxYear = tysTaxYear

        val expectedOutcome: Right[Nothing, ResponseWrapper[Unit]] = Right(ResponseWrapper(correlationId, ()))

        stubTysHttpResponse(isHipEnabled = true, outcome = expectedOutcome)

        val result: DownstreamOutcome[Unit] = await(connector.amendBenefitAmounts(request))
        result shouldBe expectedOutcome
      }
    }
  }

  trait Test { self: ConnectorTest =>
    def taxYear: TaxYear

    val request: AmendBenefitAmountsRequestData = Def1_AmendBenefitAmountsRequestData(Nino(nino), taxYear, BenefitId(benefitId), body)

    val connector: AmendBenefitAmountsConnector = new AmendBenefitAmountsConnector(mockHttpClient, mockSharedAppConfig)

    protected def stubTysHttpResponse(
                                       isHipEnabled: Boolean,
                                       outcome: DownstreamOutcome[Any]): CallHandler[Future[DownstreamOutcome[Any]]]#Derived = {

      val url: URL = if (isHipEnabled) {
        url"$baseUrl/itsa/income-tax/v1/${taxYear.asTysDownstream}/income/state-benefits/$nino/$benefitId"
      } else {
        url"$baseUrl/income-tax/${taxYear.asTysDownstream}/income/state-benefits/$nino/$benefitId"
      }

      MockedSharedAppConfig.featureSwitchConfig returns Configuration("ifs_hip_migration_1937.enabled" -> isHipEnabled)

      willPut(url = url, body = body).returns(Future.successful(outcome))
    }

  }

}
