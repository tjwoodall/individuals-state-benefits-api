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

package v1.ignoreBenefit

import play.api.http.Status.CREATED
import shared.config.{ConfigFeatureSwitches, SharedAppConfig}
import shared.connectors.DownstreamUri._
import shared.connectors.httpparsers.StandardDownstreamHttpParser._
import shared.connectors.{BaseDownstreamConnector, DownstreamOutcome, DownstreamUri}
import shared.models.domain.EmptyJsonBody
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.http.client.HttpClientV2
import v1.ignoreBenefit.model.request.IgnoreBenefitRequestData

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class IgnoreBenefitConnector @Inject() (val http: HttpClientV2, val appConfig: SharedAppConfig) extends BaseDownstreamConnector {

  def ignoreBenefit(
      request: IgnoreBenefitRequestData)(implicit hc: HeaderCarrier, ec: ExecutionContext, correlationId: String): Future[DownstreamOutcome[Unit]] = {

    implicit val successCode: SuccessCode = SuccessCode(CREATED)

    import request._
    val downstreamUri: DownstreamUri[Unit] =
      if (ConfigFeatureSwitches().isEnabled("ifs_hip_migration_1944")) {
        HipUri[Unit](s"itsd/income/ignore/state-benefits/$nino/$benefitId?taxYear=${taxYear.asTysDownstream}")
      } else {
        IfsUri[Unit](s"income-tax/${taxYear.asTysDownstream}/income/state-benefits/$nino/ignore/$benefitId")
      }
    put(EmptyJsonBody, downstreamUri)

  }

}
