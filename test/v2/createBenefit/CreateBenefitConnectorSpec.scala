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

package v2.createBenefit

import shared.config.MockSharedAppConfig
import shared.connectors.{ConnectorSpec, DownstreamOutcome}
import shared.mocks.MockHttpClient
import shared.models.domain.{Nino, TaxYear}
import shared.models.outcomes.ResponseWrapper
import uk.gov.hmrc.http.StringContextOps
import v2.createBenefit.def1.model.request.{Def1_CreateBenefitRequestBody, Def1_CreateBenefitRequestData}
import v2.createBenefit.model.response.CreateBenefitResponse
import v2.models.domain.BenefitType

import scala.concurrent.Future

class CreateBenefitConnectorSpec extends ConnectorSpec {

  private val nino    = "AA111111A"
  private val taxYear = "2021-22"

  private val createBenefitRequestBody = Def1_CreateBenefitRequestBody(BenefitType.incapacityBenefit.toString, "2020-08-03", Some("2020-12-03"))

  private val request = Def1_CreateBenefitRequestData(Nino(nino), TaxYear.fromMtd(taxYear), createBenefitRequestBody)

  private val response = CreateBenefitResponse("b1e8057e-fbbc-47a8-a8b4-78d9f015c253")

  trait Test extends MockHttpClient with MockSharedAppConfig {

    val connector: CreateBenefitConnector = new CreateBenefitConnector(
      http = mockHttpClient,
      appConfig = mockSharedAppConfig
    )

  }

  "CreateBenefitConnector" when {
    "createBenefit" should {
      "return a 200 status upon HttpClient success" in new IfsTest with Test {
        private val outcome = Right(ResponseWrapper(correlationId, response))

        willPost(
            url = url"$baseUrl/income-tax/income/state-benefits/$nino/$taxYear/custom",
            body = createBenefitRequestBody
          )
          .returns(Future.successful(outcome))

        val result: DownstreamOutcome[CreateBenefitResponse] = await(connector.createBenefit(request))
        result shouldBe outcome
      }
    }
  }

}
