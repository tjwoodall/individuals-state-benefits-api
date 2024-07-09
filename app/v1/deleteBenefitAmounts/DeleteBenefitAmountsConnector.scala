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

package v1.deleteBenefitAmounts

import api.connectors.DownstreamUri.{DesUri, IfsUri, TaxYearSpecificIfsUri}
import api.connectors.{BaseDownstreamConnector, DownstreamOutcome}
import api.models.domain.{Nino, TaxYear}
import config.AppConfig
import play.api.http.Status.NO_CONTENT
import uk.gov.hmrc.http.{HeaderCarrier, HttpClient}
import v1.deleteBenefitAmounts.def1.model.request.Def1_DeleteBenefitAmountsRequestData
import v1.deleteBenefitAmounts.model.request.DeleteBenefitAmountsRequestData
import v1.models.domain.BenefitId

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class DeleteBenefitAmountsConnector @Inject() (val http: HttpClient, val appConfig: AppConfig) extends BaseDownstreamConnector {

  private def completeRequest(nino: Nino, taxYear: TaxYear, benefitId: BenefitId)(implicit
                                                                     hc: HeaderCarrier,
                                                                     ec: ExecutionContext,
                                                                     correlationId: String): Future[DownstreamOutcome[Unit]] = {

    import api.connectors.httpparsers.StandardDownstreamHttpParser._

    implicit val successCode: SuccessCode = SuccessCode(NO_CONTENT)

    val downstreamUri = {
      if (taxYear.useTaxYearSpecificApi) {
        TaxYearSpecificIfsUri[Unit](s"income-tax/income/state-benefits/${taxYear.asTysDownstream}/${nino}/${benefitId}")
      } else if (featureSwitches.isDesIf_MigrationEnabled) {
        IfsUri[Unit](s"income-tax/income/state-benefits/${nino}/${taxYear.asMtd}/${benefitId}")
      } else {
        DesUri[Unit](s"income-tax/income/state-benefits/${nino}/${taxYear.asMtd}/${benefitId}")
      }
    }

    delete(uri = downstreamUri)
  }

  def deleteBenefitAmounts(request: DeleteBenefitAmountsRequestData)(implicit
                                                       hc: HeaderCarrier,
                                                       ec: ExecutionContext,
                                                       correlationId: String): Future[DownstreamOutcome[Unit]] =
    request match {
      case def1: Def1_DeleteBenefitAmountsRequestData =>
        import def1._
        completeRequest(nino, taxYear,benefitId)
      case _ =>
        Future.failed(new IllegalArgumentException("Request type is not known"))
    }
}
