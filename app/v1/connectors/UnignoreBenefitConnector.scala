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

import config.AppConfig

import javax.inject.{Inject, Singleton}
import uk.gov.hmrc.http.{HeaderCarrier, HttpClient}
import v1.connectors.DownstreamUri.{IfsUri, TaxYearSpecificIfsUri}
import v1.models.request.ignoreBenefit.IgnoreBenefitRequest
import v1.connectors.httpparsers.StandardDownstreamHttpParser._

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class UnignoreBenefitConnector @Inject() (val http: HttpClient, val appConfig: AppConfig) extends BaseDownstreamConnector {

  def unignoreBenefit(
      request: IgnoreBenefitRequest)(implicit hc: HeaderCarrier, ec: ExecutionContext, correlationId: String): Future[DownstreamOutcome[Unit]] = {

    import request._

    val downstreamUri = if (taxYear.useTaxYearSpecificApi) {
      TaxYearSpecificIfsUri[Unit](s"income-tax/${taxYear.asTysDownstream}/state-benefits/$nino/ignore/$benefitId")
    } else {
      IfsUri[Unit](s"income-tax/state-benefits/$nino/${taxYear.asMtd}/ignore/$benefitId")
    }

    delete(downstreamUri)
  }

}
