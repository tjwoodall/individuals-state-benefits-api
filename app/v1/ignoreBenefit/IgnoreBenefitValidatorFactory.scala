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

import api.controllers.validators.Validator
import v1.ignoreBenefit.def1.Def1_IgnoreBenefitValidator
import v1.ignoreBenefit.model.request.IgnoreBenefitRequestData

import javax.inject.Singleton

@Singleton
class IgnoreBenefitValidatorFactory {
  def validator(nino: String, taxYear: String, benefitId: String): Validator[IgnoreBenefitRequestData] =
    taxYear match {
      case _ => new Def1_IgnoreBenefitValidator(nino: String, taxYear: String, benefitId: String)
    }
}
