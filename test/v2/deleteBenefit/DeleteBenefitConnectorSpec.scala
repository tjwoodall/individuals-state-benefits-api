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

package v2.deleteBenefit

import shared.connectors.{ConnectorSpec, DownstreamOutcome}
import shared.models.domain.{Nino, TaxYear}
import shared.models.outcomes.ResponseWrapper
import v2.deleteBenefit.def1.model.request.Def1_DeleteBenefitRequestData
import v2.models.domain.BenefitId

import scala.concurrent.Future

class DeleteBenefitConnectorSpec extends ConnectorSpec {

  private val nino      = "AA111111A"
  private val taxYear   = "2019-20"
  private val benefitId = "b1e8057e-fbbc-47a8-a8b4-78d9f015c253"

  "DeleteBenefitConnector" when {
    "a valid request is supplied" must {
      "return a 204 status for a success scenario" in new IfsTest with Test {

        val outcome: Right[Nothing, ResponseWrapper[Unit]] = Right(ResponseWrapper(correlationId, ()))

        willDelete(
            url = s"$baseUrl/income-tax/income/state-benefits/$nino/$taxYear/custom/$benefitId"
          )
          .returns(Future.successful(outcome))

        val result: DownstreamOutcome[Unit] = await(connector.deleteBenefit(request))
        result shouldBe outcome
      }
    }
  }

  trait Test {
    _: ConnectorTest =>

    protected val request: Def1_DeleteBenefitRequestData = Def1_DeleteBenefitRequestData(Nino(nino), TaxYear.fromMtd(taxYear), BenefitId(benefitId))

    val connector: DeleteBenefitConnector = new DeleteBenefitConnector(http = mockHttpClient, appConfig = mockSharedAppConfig)

  }

}
