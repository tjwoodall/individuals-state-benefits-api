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

package v1.services

import cats.data.EitherT

import javax.inject.{Inject, Singleton}
import uk.gov.hmrc.http.HeaderCarrier
import utils.Logging
import v1.controllers.EndpointLogContext
import v1.models.errors.{BenefitIdFormatError, DownstreamError, ErrorWrapper, MtdError, NinoFormatError, NotFoundError, TaxYearFormatError}
import v1.models.outcomes.ResponseWrapper
import v1.models.request.deleteBenefitAmounts.DeleteBenefitAmountsRequest

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class DeleteBenefitAmountsService @Inject() (connector: DeleteBenefitAmountsConnector)
  extends DownstreamResponseMappingSupport
    with Logging {

  def delete(request: DeleteBenefitAmountsRequest)(implicit
                                                          hc: HeaderCarrier,
                                                          ec: ExecutionContext,
                                                          logContext: EndpointLogContext,
                                                          correlationId: String): Future[Either[ErrorWrapper, ResponseWrapper[Unit]]] = {

    val result = EitherT(connector.deleteOtherEmploymentIncome(request)).leftMap(mapDownstreamErrors(errorMap))

    result.value
  }

  private def errorMap: Map[String, MtdError] = {
    val errorMap = Map(
      "INVALID_TAXABLE_ENTITY_ID" -> NinoFormatError,
      "INVALID_TAX_YEAR"          -> TaxYearFormatError,
      "INVALID_BENEFIT_ID"        -> BenefitIdFormatError
      "INVALID_CORRELATIONID"     -> DownstreamError,
      "NO_DATA_FOUND"             -> NotFoundError,
      "SERVER_ERROR"              -> DownstreamError,
      "SERVICE_UNAVAILABLE"       -> DownstreamError
    )

    val extraTysErrors = Map(
      "TAX_YEAR_NOT_SUPPORTED" -> RuleTaxYearNotSupportedError
    )
    errorMap ++ extraTysErrors
  }

}