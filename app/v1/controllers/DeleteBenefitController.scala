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

package v1.controllers

import cats.data.EitherT
import cats.implicits._
import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent, ControllerComponents}
import play.mvc.Http.MimeTypes
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.play.audit.http.connector.AuditResult
import utils.{IdGenerator, Logging}
import v1.connectors.DownstreamUri.IfsUri
import v1.controllers.requestParsers.DeleteBenefitRequestParser
import v1.models.audit.{AuditEvent, AuditResponse, GenericAuditDetail}
import v1.models.errors._
import v1.models.request.deleteBenefit.DeleteBenefitRawData
import v1.services.{AuditService, DeleteRetrieveService, EnrolmentsAuthService, MtdIdLookupService}

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class DeleteBenefitController @Inject() (val authService: EnrolmentsAuthService,
                                         val lookupService: MtdIdLookupService,
                                         requestParser: DeleteBenefitRequestParser,
                                         service: DeleteRetrieveService,
                                         auditService: AuditService,
                                         cc: ControllerComponents,
                                         idGenerator: IdGenerator)(implicit ec: ExecutionContext)
    extends AuthorisedController(cc)
    with BaseController
    with Logging {

  implicit val endpointLogContext: EndpointLogContext =
    EndpointLogContext(
      controllerName = "DeleteBenefitController",
      endpointName = "deleteBenefit"
    )

  def deleteBenefit(nino: String, taxYear: String, benefitId: String): Action[AnyContent] =
    authorisedAction(nino).async { implicit request =>
      implicit val correlationId: String = idGenerator.getCorrelationId
      logger.info(message = s"[${endpointLogContext.controllerName}][${endpointLogContext.endpointName}] " +
        s"with correlationId : $correlationId")
      val rawData: DeleteBenefitRawData = DeleteBenefitRawData(
        nino = nino,
        taxYear = taxYear,
        benefitId = benefitId
      )
      implicit val desUri: IfsUri[Unit] = IfsUri[Unit](
        s"income-tax/income/state-benefits/$nino/$taxYear/custom/$benefitId"
      )
      val result =
        for {
          _               <- EitherT.fromEither[Future](requestParser.parseRequest(rawData))
          serviceResponse <- EitherT(service.delete(ifsErrorMap))
        } yield {
          logger.info(
            s"[${endpointLogContext.controllerName}][${endpointLogContext.endpointName}] - " +
              s"Success response received with CorrelationId: ${serviceResponse.correlationId}")

          auditSubmission(
            GenericAuditDetail(
              request.userDetails,
              Map("nino" -> nino, "taxYear" -> taxYear, "benefitId" -> benefitId),
              None,
              serviceResponse.correlationId,
              AuditResponse(httpStatus = NO_CONTENT, response = Right(None))
            )
          )
          NoContent
            .withApiHeaders(serviceResponse.correlationId)
            .as(MimeTypes.JSON)
        }
      result.leftMap { errorWrapper =>
        val resCorrelationId = errorWrapper.correlationId
        val result           = errorResult(errorWrapper).withApiHeaders(resCorrelationId)
        logger.warn(
          s"[${endpointLogContext.controllerName}][${endpointLogContext.endpointName}] - " +
            s"Error response received with CorrelationId: $resCorrelationId")

        auditSubmission(
          GenericAuditDetail(
            request.userDetails,
            Map("nino" -> nino, "taxYear" -> taxYear, "benefitId" -> benefitId),
            None,
            correlationId,
            AuditResponse(httpStatus = result.header.status, response = Left(errorWrapper.auditErrors))
          )
        )

        result
      }.merge
    }

  private def errorResult(errorWrapper: ErrorWrapper) = {
    errorWrapper.error match {
      case _
          if errorWrapper.containsAnyOf(
            BadRequestError,
            NinoFormatError,
            TaxYearFormatError,
            BenefitIdFormatError,
            RuleTaxYearNotSupportedError,
            RuleTaxYearRangeInvalidError,
            RuleDeleteForbiddenError
          ) =>
        BadRequest(Json.toJson(errorWrapper))

      case NotFoundError           => NotFound(Json.toJson(errorWrapper))
      case StandardDownstreamError => InternalServerError(Json.toJson(errorWrapper))
    }
  }

  private def ifsErrorMap: Map[String, MtdError] =
    Map(
      "INVALID_TAXABLE_ENTITY_ID" -> NinoFormatError,
      "INVALID_TAX_YEAR"          -> TaxYearFormatError,
      "INVALID_BENEFIT_ID"        -> BenefitIdFormatError,
      "INVALID_CORRELATIONID"     -> StandardDownstreamError,
      "DELETE_FORBIDDEN"          -> RuleDeleteForbiddenError,
      "NO_DATA_FOUND"             -> NotFoundError,
      "SERVER_ERROR"              -> StandardDownstreamError,
      "SERVICE_UNAVAILABLE"       -> StandardDownstreamError
    )

  private def auditSubmission(details: GenericAuditDetail)(implicit hc: HeaderCarrier, ec: ExecutionContext): Future[AuditResult] = {
    val event = AuditEvent("DeleteStateBenefit", "delete-state-benefit", details)
    auditService.auditEvent(event)
  }

}
