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

package v2.listBenefits

import shared.connectors.DownstreamUri.*
import shared.connectors.{BaseDownstreamConnector, DownstreamOutcome}
import shared.config.SharedAppConfig
import play.api.http.Status
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.http.client.HttpClientV2
import v2.listBenefits.model.request.ListBenefitsRequestData
import v2.listBenefits.model.response.{CustomerStateBenefit, HMRCStateBenefit, ListBenefitsResponse}

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class ListBenefitsConnector @Inject() (val http: HttpClientV2, val appConfig: SharedAppConfig) extends BaseDownstreamConnector {

  def listBenefits(request: ListBenefitsRequestData)(implicit
      hc: HeaderCarrier,
      ec: ExecutionContext,
      correlationId: String): Future[DownstreamOutcome[ListBenefitsResponse[HMRCStateBenefit, CustomerStateBenefit]]] = {

    import shared.connectors.httpparsers.StandardDownstreamHttpParser.*
    implicit val successCode: SuccessCode = SuccessCode(Status.OK)

    import request.*

    val downstreamUri = if (taxYear.useTaxYearSpecificApi) {
      IfsUri[ListBenefitsResponse[HMRCStateBenefit, CustomerStateBenefit]](
        s"income-tax/income/state-benefits/${taxYear.asTysDownstream}/${nino.nino}")
    } else {
      IfsUri[ListBenefitsResponse[HMRCStateBenefit, CustomerStateBenefit]](s"income-tax/income/state-benefits/${nino.nino}/${taxYear.asMtd}")
    }

    val queryParams = benefitId.map(id => List("benefitId" -> id.benefitId)).getOrElse(Nil)

    get(downstreamUri, queryParams = queryParams)

  }

}
