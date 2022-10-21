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

import v1.models.domain.{Nino, TaxYear}
import v1.models.outcomes.ResponseWrapper
import v1.models.request.deleteBenefitAmounts.DeleteBenefitAmountsRequest

import scala.concurrent.Future

class DeleteBenefitAmountsConnectorSpec extends ConnectorSpec {

  val nino: String        = "AA123456A"
  val benefitId: String   = "b1e8057e-fbbc-47a8-a8b4-78d9f015c253"

  trait Test {
    _: ConnectorTest =>
    def taxYear: TaxYear

    protected val connector: DeleteBenefitAmountsConnector =
      new DeleteBenefitAmountsConnector(
        http = mockHttpClient,
        appConfig = mockAppConfig
      )

    protected val request: DeleteBenefitAmountsRequest =
      DeleteBenefitAmountsRequest(
        nino = Nino(nino),
        taxYear = TaxYear(taxYear),
        benefitId = benefitId
      )

  }

  "DeleteBenefitAmountsConnector" should {
    "return a 200 result" when {
      "the downstream call is successful and not tax year specific" in new DesTest with Test {
        def taxYear          = TaxYear.fromMtd("2017-18")
        override val request = DeleteBenefitAmountsRequest(Nino(nino), taxYear, benefitId)
        val outcome          = Right(ResponseWrapper(correlationId, ()))

        val delete = willDelete(
          s"$baseUrl/income-tax/income/state-benefits/${request.nino}/${request.taxYear.asMtd}/${request.benefitId}"
        )

        delete returns Future.successful(outcome)

        await(connector.deleteOtherEmploymentIncome(request)) shouldBe outcome
      }

      "the downstream call is successful and is tax year specific" in new TysIfsTest with Test {
        def taxYear          = TaxYear.fromMtd("2023-24")
        override val request = DeleteBenefitAmountsRequest(Nino(nino), taxYear, benefitId)
        val outcome          = Right(ResponseWrapper(correlationId, ()))

        val delete = willDelete(
          s"$baseUrl/income-tax/income/state-benefits/${request.taxYear.asTysDownstream}/${request.nino}/${request.benefitId}"
        )

        delete returns Future.successful(outcome)

        await(connector.deleteOtherEmploymentIncome(request)) shouldBe outcome
      }

    }
  }

}