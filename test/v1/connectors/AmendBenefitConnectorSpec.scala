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

package v1.connectors

import api.connectors.ConnectorSpec
import api.mocks.MockHttpClient
import api.models.domain.{Nino, TaxYear}
import api.models.outcomes.ResponseWrapper
import mocks.MockAppConfig
import uk.gov.hmrc.http.HeaderCarrier
import v1.models.domain.BenefitId
import v1.models.request.amendBenefit.{AmendBenefitRequestData, AmendBenefitRequestBody}

import scala.concurrent.Future

class AmendBenefitConnectorSpec extends ConnectorSpec {

  val nino: String      = "AA123456A"
  val taxYear: String   = "2021-22"
  val benefitId: String = "123e4567-e89b-12d3-a456-426614174000"

  val amendBenefitRequestBody: AmendBenefitRequestBody = AmendBenefitRequestBody(
    startDate = "2020-08-03",
    endDate = Some("2020-12-03")
  )

  val request: AmendBenefitRequestData = AmendBenefitRequestData(
    nino = Nino(nino),
    taxYear = TaxYear.fromMtd(taxYear),
    benefitId = BenefitId(benefitId),
    body = amendBenefitRequestBody
  )

  class Test extends MockHttpClient with MockAppConfig {

    val connector: AmendBenefitConnector = new AmendBenefitConnector(
      http = mockHttpClient,
      appConfig = mockAppConfig
    )

    val ifsRequestHeaders: Seq[(String, String)] = Seq(
      "Environment"   -> "ifs-environment",
      "Authorization" -> s"Bearer ifs-token"
    )

    MockedAppConfig.ifsBaseUrl returns baseUrl
    MockedAppConfig.ifsToken returns "release6-token"
    MockedAppConfig.ifsEnvironment returns "release6-environment"
    MockedAppConfig.ifsEnvironmentHeaders returns Some(allowedIfsHeaders)
  }

  "AmendBenefitConnector" when {
    "amendBenefit" must {
      "return a 201 status for a success scenario" in new Test {
        val outcome = Right(ResponseWrapper(correlationId, ()))

        implicit val hc: HeaderCarrier = HeaderCarrier(otherHeaders = otherHeaders ++ Seq("Content-Type" -> "application/json"))
        val requiredRelease6HeadersPut: Seq[(String, String)] = requiredRelease6Headers ++ Seq("Content-Type" -> "application/json")

        MockedHttpClient
          .put(
            url = s"$baseUrl/income-tax/income/state-benefits/$nino/$taxYear/custom/$benefitId",
            config = dummyIfsHeaderCarrierConfig,
            body = request.body,
            requiredHeaders = requiredRelease6HeadersPut,
            excludedHeaders = Seq("AnotherHeader" -> "HeaderValue")
          )
          .returns(Future.successful(outcome))

        await(connector.amendBenefit(request)) shouldBe outcome
      }
    }
  }

}
