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
import play.api.libs.json.JsValue
import play.api.mvc.{Action, AnyContentAsJson, ControllerComponents}
import routing.{Version1, Version}
import utils.{IdGenerator, Logging}
import v1.controllers.requestParsers.AmendBenefitAmountsRequestParser
import v1.models.request.AmendBenefitAmounts.AmendBenefitAmountsRawData
import v1.models.response.amendBenefitAmounts.AmendBenefitAmountsHateoasData
import v1.models.response.amendBenefitAmounts.AmendBenefitAmountsResponse.AmendBenefitAmountsLinksFactory
import v1.services.AmendBenefitAmountsService

import javax.inject.{Inject, Singleton}
import scala.concurrent.ExecutionContext

@Singleton
class AmendBenefitAmountsController @Inject() (val authService: EnrolmentsAuthService,
                                               val lookupService: MtdIdLookupService,
                                               appConfig: AppConfig,
                                               parser: AmendBenefitAmountsRequestParser,
                                               service: AmendBenefitAmountsService,
                                               auditService: AuditService,
                                               hateoasFactory: HateoasFactory,
                                               cc: ControllerComponents,
                                               val idGenerator: IdGenerator)(implicit ec: ExecutionContext)
    extends AuthorisedController(cc)
    with Logging {

  implicit val endpointLogContext: EndpointLogContext =
    EndpointLogContext(
      controllerName = "AmendBenefitAmountsController",
      endpointName = "amendBenefitAmounts"
    )

  def amendBenefitAmounts(nino: String, taxYear: String, benefitId: String): Action[JsValue] =
    authorisedAction(nino).async(parse.json) { implicit request =>
      implicit val ctx: RequestContext = RequestContext.from(idGenerator, endpointLogContext)

      val rawData: AmendBenefitAmountsRawData = AmendBenefitAmountsRawData(
        nino = nino,
        taxYear = taxYear,
        benefitId = benefitId,
        body = AnyContentAsJson(request.body)
      )

      val requestHandler = RequestHandlerOld
        .withParser(parser)
        .withService(service.amendBenefitAmounts)
        .withAuditing(AuditHandlerOld(
          auditService = auditService,
          auditType = "AmendStateBenefitAmounts",
          transactionName = "amend-state-benefit-amounts",
          version = Version.from(request, orElse = Version1),
          pathParams = Map("nino" -> nino, "taxYear" -> taxYear, "benefitId" -> benefitId),
          requestBody = Some(request.body),
          includeResponse = true
        ))
        .withHateoasResult(hateoasFactory)(AmendBenefitAmountsHateoasData(nino, taxYear, benefitId))

      requestHandler.handleRequest(rawData)
    }

}
