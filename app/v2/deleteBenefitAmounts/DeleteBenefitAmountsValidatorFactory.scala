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

package v2.deleteBenefitAmounts

import config.StateBenefitsAppConfig
import shared.controllers.validators.Validator
import v2.deleteBenefitAmounts.def1.Def1_DeleteBenefitAmountsValidator
import v2.deleteBenefitAmounts.model.request.DeleteBenefitAmountsRequestData

import javax.inject.{Inject, Singleton}

@Singleton
class DeleteBenefitAmountsValidatorFactory @Inject() (implicit stateBenefitsAppConfig: StateBenefitsAppConfig) {

  def validator(nino: String, taxYear: String, benefitId: String): Validator[DeleteBenefitAmountsRequestData] =
    taxYear match {
      case _ => new Def1_DeleteBenefitAmountsValidator(nino: String, taxYear: String, benefitId: String)
    }
}
