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

package v1.amendBenefit

import play.api.libs.json.JsValue
import play.api.mvc.{Action, ControllerComponents}
import shared.config.SharedAppConfig
import shared.controllers.*
import shared.hateoas.HateoasFactory
import shared.routing.Version
import shared.services.{AuditService, EnrolmentsAuthService, MtdIdLookupService}
import shared.utils.IdGenerator
import v1.amendBenefit.model.response.AmendBenefitHateoasData
import v1.amendBenefit.model.response.AmendBenefitResponse.AmendBenefitLinksFactory

import javax.inject.{Inject, Singleton}
import scala.concurrent.ExecutionContext

@Singleton
class AmendBenefitController @Inject() (val authService: EnrolmentsAuthService,
                                        val lookupService: MtdIdLookupService,
                                        validatorFactory: AmendBenefitValidatorFactory,
                                        service: AmendBenefitService,
                                        auditService: AuditService,
                                        hateoasFactory: HateoasFactory,
                                        cc: ControllerComponents,
                                        val idGenerator: IdGenerator)(implicit appConfig: SharedAppConfig, ec: ExecutionContext)
    extends AuthorisedController(cc) {

  val endpointName = "amend-benefit"

  implicit val endpointLogContext: EndpointLogContext =
    EndpointLogContext(
      controllerName = "AmendBenefitController",
      endpointName = "amendBenefitAmounts"
    )

  def amendBenefit(nino: String, taxYear: String, benefitId: String): Action[JsValue] =
    authorisedAction(nino).async(parse.json) { implicit request =>
      implicit val ctx: RequestContext = RequestContext.from(idGenerator, endpointLogContext)

      val validator = validatorFactory.validator(nino, taxYear, benefitId, request.body)

      val requestHandler = RequestHandler
        .withValidator(validator)
        .withService(service.amendBenefit)
        .withAuditing(AuditHandler(
          auditService = auditService,
          auditType = "AmendStateBenefit",
          transactionName = "amend-state-benefit",
          apiVersion = Version(request),
          params = Map("nino" -> nino, "taxYear" -> taxYear, "benefitId" -> benefitId),
          requestBody = Some(request.body),
          includeResponse = true
        ))
        .withHateoasResult(hateoasFactory)(AmendBenefitHateoasData(nino, taxYear, benefitId))

      requestHandler.handleRequest()
    }

}
