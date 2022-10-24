/*
 * Copyright 2022 HM Revenue & Customs
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

import mocks.MockAppConfig
import v1.models.domain.Nino
import v1.mocks.MockHttpClient
import v1.models.outcomes.ResponseWrapper
import v1.models.request.EmptyBody
import v1.models.request.ignoreBenefit.IgnoreBenefitRequest

import scala.concurrent.Future

class IgnoreBenefitConnectorSpec extends ConnectorSpec {

  val nino: String                  = "AA111111A"
  val taxYear: String               = "2019-20"
  val benefitId: String             = "123e4567-e89b-12d3-a456-426614174000"
  val request: IgnoreBenefitRequest = IgnoreBenefitRequest(Nino(nino), taxYear, benefitId)

  class Test extends MockHttpClient with MockAppConfig {

    val connector: IgnoreBenefitConnector = new IgnoreBenefitConnector(
      http = mockHttpClient,
      appConfig = mockAppConfig
    )

    MockedAppConfig.ifsBaseUrl returns baseUrl
    MockedAppConfig.ifsToken returns "release6-token"
    MockedAppConfig.ifsEnvironment returns "release6-environment"
    MockedAppConfig.ifsEnvironmentHeaders returns Some(allowedIfsHeaders)
  }

  "IgnoreBenefitConnector" when {
    "happy path" should {
      "return a successful response" in new Test {
        private val outcome = Right(ResponseWrapper(correlationId, ()))

        MockedHttpClient
          .put(
            url = s"$baseUrl/income-tax/income/state-benefits/$nino/$taxYear/ignore/$benefitId",
            config = dummyIfsHeaderCarrierConfig,
            body = EmptyBody,
            requiredHeaders = requiredRelease6Headers,
            excludedHeaders = Seq("AnotherHeader" -> "HeaderValue")
          )
          .returns(Future.successful(outcome))

        await(connector.ignoreBenefit(request)) shouldBe outcome
      }
    }
  }

}
