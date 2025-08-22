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

package v2.unignoreBenefit

import play.api.Configuration
import shared.connectors.{ConnectorSpec, DownstreamOutcome}
import shared.models.domain.{Nino, TaxYear}
import shared.models.outcomes.ResponseWrapper
import uk.gov.hmrc.http.StringContextOps
import v2.models.domain.BenefitId
import v2.unignoreBenefit.def1.model.request.Def1_UnignoreBenefitRequestData
import v2.unignoreBenefit.model.request.UnignoreBenefitRequestData

import scala.concurrent.Future

class UnignoreBenefitConnectorSpec extends ConnectorSpec {

  private val nino      = "AA111111A"
  private val benefitId = "123e4567-e89b-12d3-a456-426614174000"

  "UnignoreBenefitConnector" should {
    "return the expected response for a request" when {
      "a valid request is made to IF" in new IfsTest with Test {
        MockedSharedAppConfig.featureSwitchConfig returns Configuration("ifs_hip_migration_1945.enabled" -> false)
        def taxYear: TaxYear = TaxYear.fromMtd("2019-20")

        val expectedOutcome: Right[Nothing, ResponseWrapper[Unit]] = Right(ResponseWrapper(correlationId, ()))

        willDelete(
          url = url"$baseUrl/income-tax/19-20/state-benefits/$nino/ignore/$benefitId"
        ).returns(Future.successful(expectedOutcome))

        val result: DownstreamOutcome[Unit] = await(connector.unignoreBenefit(request))

        result shouldBe expectedOutcome
      }
      "a valid request is made to HIP" in new HipTest with Test {
        MockedSharedAppConfig.featureSwitchConfig returns Configuration("ifs_hip_migration_1945.enabled" -> true)

        def taxYear: TaxYear = TaxYear.fromMtd("2025-26")

        private val outcome: Right[Nothing, ResponseWrapper[Unit]] = Right(ResponseWrapper(correlationId, ()))

        willDelete(
          url = url"$baseUrl/itsd/income/ignore/state-benefits/$nino/$benefitId?taxYear=25-26",
        ).returns(Future.successful(outcome))

        val result: DownstreamOutcome[Unit] = await(connector.unignoreBenefit(request))

        result shouldBe outcome
      }
    }
  }

  trait Test {
    self: ConnectorTest =>
    
    def taxYear: TaxYear

    val request: UnignoreBenefitRequestData = Def1_UnignoreBenefitRequestData(Nino(nino), taxYear, BenefitId(benefitId))

    val connector: UnignoreBenefitConnector = new UnignoreBenefitConnector(http = mockHttpClient, appConfig = mockSharedAppConfig)

  }

}
