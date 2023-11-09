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

import api.connectors.{ConnectorSpec, DownstreamOutcome}
import api.mocks.MockHttpClient
import api.models.domain.{BenefitType, Nino, TaxYear}
import api.models.outcomes.ResponseWrapper
import mocks.MockAppConfig
import v1.models.request.createBenefit.{CreateBenefitRequestBody, CreateBenefitRequestData}
import v1.models.response.createBenefit.CreateBenefitResponse

import scala.concurrent.Future

class CreateBenefitConnectorSpec extends ConnectorSpec {

  private val nino    = "AA111111A"
  private val taxYear = "2021-22"

  private val createBenefitRequestBody = CreateBenefitRequestBody(BenefitType.incapacityBenefit.toString, "2020-08-03", Some("2020-12-03"))

  private val request = CreateBenefitRequestData(Nino(nino), TaxYear.fromMtd(taxYear), createBenefitRequestBody)

  private val response = CreateBenefitResponse("b1e8057e-fbbc-47a8-a8b4-78d9f015c253")

  class Test extends MockHttpClient with MockAppConfig {

    val connector: CreateBenefitConnector = new CreateBenefitConnector(
      http = mockHttpClient,
      appConfig = mockAppConfig
    )

    MockedAppConfig.ifsBaseUrl returns baseUrl
    MockedAppConfig.ifsToken returns "release6-token"
    MockedAppConfig.ifsEnvironment returns "release6-environment"
    MockedAppConfig.ifsEnvironmentHeaders returns Some(allowedIfsHeaders)
  }

  "CreateBenefitConnector" when {
    "createBenefit" should {
      "return a 200 status upon HttpClient success" in new Test {
        private val outcome = Right(ResponseWrapper(correlationId, response))

        MockedHttpClient
          .post(
            url = s"$baseUrl/income-tax/income/state-benefits/$nino/$taxYear/custom",
            config = dummyIfsHeaderCarrierConfig,
            body = createBenefitRequestBody,
            requiredHeaders = requiredRelease6Headers,
            excludedHeaders = Seq("AnotherHeader" -> "HeaderValue")
          )
          .returns(Future.successful(outcome))

        val result: DownstreamOutcome[CreateBenefitResponse] = await(connector.createBenefit(request))
        result shouldBe outcome
      }
    }
  }

}
