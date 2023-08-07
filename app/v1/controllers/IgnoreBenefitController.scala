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

import api.controllers._
import api.hateoas.HateoasFactory
import api.services.{AuditService, EnrolmentsAuthService, MtdIdLookupService}
import config.AppConfig
import play.api.mvc.{Action, AnyContent, ControllerComponents}
import routing.{Version, Version1}
import utils.IdGenerator
import v1.controllers.requestParsers.IgnoreBenefitRequestParser
import v1.models.request.ignoreBenefit.IgnoreBenefitRawData
import v1.models.response.ignoreBenefit.IgnoreBenefitHateoasData
import v1.models.response.ignoreBenefit.IgnoreBenefitResponse._
import v1.services.IgnoreBenefitService

import javax.inject.{Inject, Singleton}
import scala.concurrent.ExecutionContext

@Singleton
class IgnoreBenefitController @Inject() (val authService: EnrolmentsAuthService,
                                         val lookupService: MtdIdLookupService,
                                         appConfig: AppConfig,
                                         parser: IgnoreBenefitRequestParser,
                                         service: IgnoreBenefitService,
                                         auditService: AuditService,
                                         hateoasFactory: HateoasFactory,
                                         cc: ControllerComponents,
                                         idGenerator: IdGenerator)(implicit ec: ExecutionContext)
    extends AuthorisedController(cc) {

  implicit val endpointLogContext: EndpointLogContext =
    EndpointLogContext(
      controllerName = "IgnoreBenefitController",
      endpointName = "ignoreBenefit"
    )

  def ignoreBenefit(nino: String, taxYear: String, benefitId: String): Action[AnyContent] =
    authorisedAction(nino).async { implicit request =>
      implicit val ctx: RequestContext = RequestContext.from(idGenerator, endpointLogContext)

      val rawData: IgnoreBenefitRawData = IgnoreBenefitRawData(nino, taxYear, benefitId)

      val requestHandler = RequestHandler
        .withParser(parser)
        .withService(service.ignoreBenefit)
        .withAuditing(AuditHandler(
          auditService = auditService,
          auditType = "IgnoreStateBenefit",
          transactionName = "ignore-state-benefit",
          version = Version.from(request, orElse = Version1),
          pathParams = Map("nino" -> nino, "taxYear" -> taxYear, "benefitId" -> benefitId),
          requestBody = None,
          includeResponse = true
        ))
        .withHateoasResult(hateoasFactory)(IgnoreBenefitHateoasData(nino, taxYear, benefitId))

      requestHandler.handleRequest(rawData)
    }

}
