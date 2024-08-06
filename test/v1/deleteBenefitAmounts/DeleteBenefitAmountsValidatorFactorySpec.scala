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

package v1.deleteBenefitAmounts

import api.controllers.validators.Validator
import support.UnitSpec
import v1.deleteBenefitAmounts.def1.Def1_DeleteBenefitAmountsValidator
import v1.deleteBenefitAmounts.model.request.DeleteBenefitAmountsRequestData

class DeleteBenefitAmountsValidatorFactorySpec extends UnitSpec {

  private val validNino      = "AA123456A"
  private val validTaxYear   = "2023-24"
  private val invalidTaxYear = "2023"
  private val validBenefitId = "b1e8057e-fbbc-47a8-a8b4-78d9f015c253"

  private val validatorFactory = new DeleteBenefitAmountsValidatorFactory

  "validator" should {
    "return the Def1 validator" when {
      "given a valid request" in {
        val result: Validator[DeleteBenefitAmountsRequestData] = validatorFactory.validator(validNino, validTaxYear, validBenefitId)
        result shouldBe a[Def1_DeleteBenefitAmountsValidator]
      }

      "given an invalid taxYear" in {
        val result: Validator[DeleteBenefitAmountsRequestData] = validatorFactory.validator(validNino, invalidTaxYear, validBenefitId)
        result shouldBe a[Def1_DeleteBenefitAmountsValidator]
      }
    }

  }

}
