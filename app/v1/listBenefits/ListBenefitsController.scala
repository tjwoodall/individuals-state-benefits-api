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

package v1.listBenefits

import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent, ControllerComponents}
import shared.config.SharedAppConfig
import shared.controllers.*
import shared.services.{EnrolmentsAuthService, MtdIdLookupService}
import shared.utils.IdGenerator
import v1.hateoas.HateoasFactory2
import v1.listBenefits.model.response.ListBenefitsHateoasData

import javax.inject.{Inject, Singleton}
import scala.concurrent.ExecutionContext

@Singleton
class ListBenefitsController @Inject() (val authService: EnrolmentsAuthService,
                                        val lookupService: MtdIdLookupService,
                                        validatorFactory: ListBenefitsValidatorFactory,
                                        service: ListBenefitsService,
                                        hateoasFactory: HateoasFactory2,
                                        cc: ControllerComponents,
                                        val idGenerator: IdGenerator)(implicit appConfig: SharedAppConfig, ec: ExecutionContext)
    extends AuthorisedController(cc) {

  val endpointName = "list-benefits"

  implicit val endpointLogContext: EndpointLogContext =
    EndpointLogContext(
      controllerName = "ListBenefitsController",
      endpointName = "ListBenefitsAmounts"
    )

  def listBenefits(nino: String, taxYear: String, benefitId: Option[String]): Action[AnyContent] =
    authorisedAction(nino).async { implicit request =>
      implicit val ctx: RequestContext = RequestContext.from(idGenerator, endpointLogContext)

      val validator = validatorFactory.validator(nino, taxYear, benefitId)

      val requestHandler = RequestHandler
        .withValidator(validator)
        .withService(service.listBenefits)
        .withResultCreator { (_, response) =>
          val hmrcBenefitIds = response.stateBenefits.getOrElse(Nil).map(_.benefitId)
          val wrapped        = hateoasFactory.wrapList(response, ListBenefitsHateoasData(nino, taxYear, benefitId.isDefined, hmrcBenefitIds))

          ResultWrapper(OK, Some(Json.toJson(wrapped)))
        }

      requestHandler.handleRequest()
    }

}
