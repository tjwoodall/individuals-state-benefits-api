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
import api.services.{EnrolmentsAuthService, MtdIdLookupService}
import play.api.mvc.{Action, AnyContent, ControllerComponents}
import utils.IdGenerator
import v1.controllers.validators.ListBenefitsValidatorFactory
import v1.models.response.listBenefits.{CustomerStateBenefit, HMRCStateBenefit, ListBenefitsHateoasData, ListBenefitsResponse}
import v1.services.ListBenefitsService

import javax.inject.{Inject, Singleton}
import scala.concurrent.ExecutionContext

@Singleton
class ListBenefitsController @Inject()(val authService: EnrolmentsAuthService,
                                       val lookupService: MtdIdLookupService,
                                       validatorFactory: ListBenefitsValidatorFactory,
                                       service: ListBenefitsService,
                                       hateoasFactory: HateoasFactory,
                                       cc: ControllerComponents,
                                       val idGenerator: IdGenerator)(implicit ec: ExecutionContext)
  extends AuthorisedController(cc) {

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
        .withResultCreator(ResultCreator.hateoasListWrapping2(hateoasFactory)((_, response) =>
          ListBenefitsHateoasData(nino, taxYear, benefitId.isDefined, hmrcBenefitIds(response))))

      requestHandler.handleRequest()
    }

  private def hmrcBenefitIds(response: ListBenefitsResponse[HMRCStateBenefit, CustomerStateBenefit]): Seq[String] =
    response.stateBenefits.getOrElse(Nil).map(_.benefitId)

}
