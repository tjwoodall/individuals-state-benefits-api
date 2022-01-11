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

package v1r6.services

import cats.data.EitherT
import cats.implicits._
import uk.gov.hmrc.http.HeaderCarrier
import utils.Logging
import v1r6.connectors.ListBenefitsConnector
import v1r6.controllers.EndpointLogContext
import v1r6.models.errors._
import v1r6.models.outcomes.ResponseWrapper
import v1r6.models.request.listBenefits.ListBenefitsRequest
import v1r6.models.response.listBenefits.{CustomerStateBenefit, HMRCStateBenefit, ListBenefitsResponse}
import v1r6.support.DesResponseMappingSupport

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class ListBenefitsService @Inject()(connector: ListBenefitsConnector) extends DesResponseMappingSupport with Logging {

  def listBenefits(request: ListBenefitsRequest)
                  (implicit hc: HeaderCarrier, ec: ExecutionContext, logContext: EndpointLogContext,
                   correlationId: String):
  Future[Either[ErrorWrapper, ResponseWrapper[ListBenefitsResponse[HMRCStateBenefit, CustomerStateBenefit]]]] = {

    val result = for {
      desResponseWrapper <- EitherT(connector.listBenefits(request)).leftMap(mapDesErrors(mappingDesToMtdError))
    } yield desResponseWrapper.map(des => des)

    result.value
  }

  private def mappingDesToMtdError: Map[String, MtdError] = Map(
    "INVALID_TAXABLE_ENTITY_ID" -> NinoFormatError,
    "INVALID_TAX_YEAR" -> TaxYearFormatError,
    "INVALID_BENEFIT_ID" -> BenefitIdFormatError,
    "INVALID_VIEW" -> DownstreamError,
    "INVALID_CORRELATIONID" -> DownstreamError,
    "NO_DATA_FOUND" -> NotFoundError,
    "TAX_YEAR_NOT_SUPPORTED" -> RuleTaxYearNotSupportedError,
    "SERVER_ERROR" -> DownstreamError,
    "SERVICE_UNAVAILABLE" -> DownstreamError
  )
}

