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

package v2.amendBenefit

import config.StateBenefitsAppConfig
import shared.controllers.validators.Validator
import play.api.libs.json.JsValue
import v2.amendBenefit.def1.Def1_AmendBenefitValidator
import v2.amendBenefit.model.request.AmendBenefitRequestData

import javax.inject.{Inject, Singleton}

@Singleton
class AmendBenefitValidatorFactory @Inject() (implicit stateBenefitsAppConfig: StateBenefitsAppConfig) {

  def validator(nino: String, taxYear: String, benefitId: String, body: JsValue): Validator[AmendBenefitRequestData] =
    taxYear match {
      case _ => new Def1_AmendBenefitValidator(nino: String, taxYear: String, benefitId: String, body: JsValue)
    }
}
