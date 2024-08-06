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
import support.UnitSpec
import v1.ignoreBenefit.def1.Def1_IgnoreBenefitValidator
import v1.ignoreBenefit.model.request.IgnoreBenefitRequestData

class IgnoreBenefitValidatorFactorySpec extends UnitSpec {

  private val validNino      = "AA123456B"
  private val validTaxYear   = "2021-22"
  private val invalidTaxYear   = "2021"
  private val validBenefitId = "b1e8057e-fbbc-47a8-a8b4-78d9f015c253"

  private val validatorFactory = new IgnoreBenefitValidatorFactory

  "validator" should {
    "return the Def1 validator" when {
      "given a valid request" in {
        val result: Validator[IgnoreBenefitRequestData] = validatorFactory.validator(validNino, validTaxYear, validBenefitId)
        result shouldBe a[Def1_IgnoreBenefitValidator]
      }

      "given an invalid taxYear" in {
        val result: Validator[IgnoreBenefitRequestData] = validatorFactory.validator(validNino, invalidTaxYear, validBenefitId)
        result shouldBe a[Def1_IgnoreBenefitValidator]
      }
    }

  }

}
