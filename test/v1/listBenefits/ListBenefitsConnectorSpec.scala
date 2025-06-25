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

package v1.listBenefits

import shared.config.MockSharedAppConfig
import shared.connectors.{ConnectorSpec, DownstreamOutcome}
import shared.mocks.MockHttpClient
import shared.models.domain.{Nino, TaxYear, Timestamp}
import shared.models.outcomes.ResponseWrapper
import uk.gov.hmrc.http.StringContextOps
import v1.listBenefits.model.request.ListBenefitsRequestData
import v1.listBenefits.model.response.{CustomerStateBenefit, HMRCStateBenefit, ListBenefitsResponse}
import v1.models.domain.BenefitId

import scala.concurrent.Future

class ListBenefitsConnectorSpec extends ConnectorSpec {

  private val nino      = "AA111111A"
  private val taxYear   = "2019-20"
  private val benefitId = "4557ecb5-fd32-48cc-81f5-e6acd1099f3c"

  private val validResponse = ListBenefitsResponse(
    stateBenefits = Some(
      Seq(
        HMRCStateBenefit(
          benefitType = "incapacityBenefit",
          dateIgnored = Some(Timestamp("2019-04-04T01:01:01.000Z")),
          benefitId = "f0d83ac0-a10a-4d57-9e41-6d033832779f",
          startDate = "2020-01-01",
          endDate = Some("2020-04-01"),
          amount = Some(2000.00),
          taxPaid = Some(2132.22),
          submittedOn = None
        )
      )
    ),
    customerAddedStateBenefits = Some(
      Seq(
        CustomerStateBenefit(
          benefitType = "incapacityBenefit",
          benefitId = "f0d83ac0-a10a-4d57-9e41-6d033832779f",
          startDate = "2020-01-01",
          endDate = Some("2020-04-01"),
          amount = Some(2000.00),
          taxPaid = Some(2132.22),
          submittedOn = Some(Timestamp("2019-04-04T01:01:01.000Z"))
        )
      )
    )
  )

  "ListBenefitsConnector" when {
    "listBenefits" when {
      "a benefitId query param is provided" must {
        "return a 200 status for a success scenario" in new IfsTest with Test {
          val request: ListBenefitsRequestData = ListBenefitsRequestData(Nino(nino), TaxYear.fromMtd("2019-20"), Some(BenefitId(benefitId)))
          val outcome: Right[Nothing, ResponseWrapper[ListBenefitsResponse[HMRCStateBenefit, CustomerStateBenefit]]] =
            Right(ResponseWrapper(correlationId, validResponse))

          willGet(url"$baseUrl/income-tax/income/state-benefits/$nino/$taxYear", parameters = Seq("benefitId" -> benefitId)) returns Future
            .successful(outcome)

          val result: DownstreamOutcome[ListBenefitsResponse[HMRCStateBenefit, CustomerStateBenefit]] = await(connector.listBenefits(request))

          result shouldBe outcome
        }
      }

      "no benefitId query param is provided" must {
        "return a 200 status for a success scenario" in new IfsTest with Test {
          val request: ListBenefitsRequestData = ListBenefitsRequestData(Nino(nino), TaxYear.fromMtd("2019-20"), None)
          val outcome: Right[Nothing, ResponseWrapper[ListBenefitsResponse[HMRCStateBenefit, CustomerStateBenefit]]] =
            Right(ResponseWrapper(correlationId, validResponse))

          willGet(url"$baseUrl/income-tax/income/state-benefits/$nino/$taxYear") returns Future.successful(outcome)

          val result: DownstreamOutcome[ListBenefitsResponse[HMRCStateBenefit, CustomerStateBenefit]] = await(connector.listBenefits(request))

          result shouldBe outcome
        }
      }
      "a benefitId is provided for a TYS tax year" must {
        "return a 200 status for a success scenario" in new IfsTest with Test {

          val request: ListBenefitsRequestData = ListBenefitsRequestData(Nino(nino), TaxYear.fromMtd("2023-24"), Some(BenefitId(benefitId)))
          val outcome: Right[Nothing, ResponseWrapper[ListBenefitsResponse[HMRCStateBenefit, CustomerStateBenefit]]] =
            Right(ResponseWrapper(correlationId, validResponse))

          willGet(url"$baseUrl/income-tax/income/state-benefits/23-24/$nino", parameters = Seq("benefitId" -> benefitId)) returns Future.successful(
            outcome)

          val result: DownstreamOutcome[ListBenefitsResponse[HMRCStateBenefit, CustomerStateBenefit]] = await(connector.listBenefits(request))

          result shouldBe outcome
        }
      }
    }
  }

  trait Test extends MockHttpClient with MockSharedAppConfig {

    val connector: ListBenefitsConnector = new ListBenefitsConnector(http = mockHttpClient, appConfig = mockSharedAppConfig)

    val ifsRequestHeaders: Seq[(String, String)] = List(
      "Environment"   -> "release6-environment",
      "Authorization" -> s"Bearer release6-token"
    )

  }

}
