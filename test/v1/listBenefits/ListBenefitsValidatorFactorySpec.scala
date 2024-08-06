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

import api.controllers.validators.Validator
import support.UnitSpec
import v1.listBenefits.def1.Def1_ListBenefitsValidator
import v1.listBenefits.model.request.ListBenefitsRequestData

class ListBenefitsValidatorFactorySpec extends UnitSpec {

  private val validNino      = "AA123456B"
  private val validTaxYear   = "2020-21"
  private val invalidTaxYear = "2020-21"
  private val validBenefitId = Some("4557ecb5-fd32-48cc-81f5-e6acd1099f3c")

  private val validatorFactory = new ListBenefitsValidatorFactory

  "validator" should {
    "return the Def1 validator" when {
      "given a valid request" in {
        val result: Validator[ListBenefitsRequestData] = validatorFactory.validator(validNino, validTaxYear, validBenefitId)
        result shouldBe a[Def1_ListBenefitsValidator]
      }

      "given an invalid taxYear" in {
        val result: Validator[ListBenefitsRequestData] = validatorFactory.validator(validNino, invalidTaxYear, validBenefitId)
        result shouldBe a[Def1_ListBenefitsValidator]
      }
    }

  }

}
