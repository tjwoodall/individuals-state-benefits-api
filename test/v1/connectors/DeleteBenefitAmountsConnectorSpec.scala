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
import api.models.domain.{Nino, TaxYear}
import api.models.outcomes.ResponseWrapper
import v1.models.domain.BenefitId
import v1.models.request.deleteBenefitAmounts.DeleteBenefitAmountsRequestData

import scala.concurrent.Future

class DeleteBenefitAmountsConnectorSpec extends ConnectorSpec {

  private val nino      = "AA123456A"
  private val benefitId = "b1e8057e-fbbc-47a8-a8b4-78d9f015c253"

  "DeleteBenefitAmountsConnector" should {
    "return a 200 result on delete" when {
      "the downstream call is successful and not tax year specific" in new DesTest with Test {
        def taxYear: TaxYear                               = TaxYear.fromMtd("2017-18")
        val outcome: Right[Nothing, ResponseWrapper[Unit]] = Right(ResponseWrapper(correlationId, ()))

        willDelete(s"$baseUrl/income-tax/income/state-benefits/$nino/${request.taxYear.asMtd}/${request.benefitId}") returns Future.successful(
          outcome)

        val result: DownstreamOutcome[Unit] = await(connector.deleteBenefitAmounts(request))

        result shouldBe outcome
      }

      "the downstream call is successful and is tax year specific" in new TysIfsTest with Test {
        def taxYear: TaxYear                               = TaxYear.fromMtd("2023-24")
        val outcome: Right[Nothing, ResponseWrapper[Unit]] = Right(ResponseWrapper(correlationId, ()))

        willDelete(s"$baseUrl/income-tax/income/state-benefits/${request.taxYear.asTysDownstream}/$nino/${request.benefitId}") returns Future
          .successful(outcome)

        val result: DownstreamOutcome[Unit] = await(connector.deleteBenefitAmounts(request))

        result shouldBe outcome
      }
    }
  }

  trait Test {
    _: ConnectorTest =>
    def taxYear: TaxYear

    protected val connector: DeleteBenefitAmountsConnector = new DeleteBenefitAmountsConnector(http = mockHttpClient, appConfig = mockAppConfig)

    protected val request: DeleteBenefitAmountsRequestData = DeleteBenefitAmountsRequestData(Nino(nino), taxYear, BenefitId(benefitId))

  }

}
