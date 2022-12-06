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

import config.AppConfig
import play.api.http.Status
import uk.gov.hmrc.http.{HeaderCarrier, HttpClient}
import v1.connectors.DownstreamUri.{IfsUri, TaxYearSpecificIfsUri}
import v1.models.request.listBenefits.ListBenefitsRequest
import v1.models.response.listBenefits.{CustomerStateBenefit, HMRCStateBenefit, ListBenefitsResponse}

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class ListBenefitsConnector @Inject() (val http: HttpClient, val appConfig: AppConfig) extends BaseDownstreamConnector {

  def listBenefits(request: ListBenefitsRequest)(implicit
      hc: HeaderCarrier,
      ec: ExecutionContext,
      correlationId: String): Future[DownstreamOutcome[ListBenefitsResponse[HMRCStateBenefit, CustomerStateBenefit]]] = {

    import v1.connectors.httpparsers.StandardDownstreamHttpParser._
    implicit val successCode: SuccessCode = SuccessCode(Status.OK)

    import request._

    if (taxYear.useTaxYearSpecificApi) {
      get(
        TaxYearSpecificIfsUri[ListBenefitsResponse[HMRCStateBenefit, CustomerStateBenefit]](
          s"income-tax/income/state-benefits/${taxYear.asTysDownstream}/${nino}"),
        queryParams = request.benefitId.map("benefitId" -> _).toSeq
      )
    } else {
      get(
        IfsUri[ListBenefitsResponse[HMRCStateBenefit, CustomerStateBenefit]](s"income-tax/income/state-benefits/$nino/${taxYear.asMtd}"),
        queryParams = request.benefitId.map("benefitId" -> _).toSeq
      )
    }

  }

}
