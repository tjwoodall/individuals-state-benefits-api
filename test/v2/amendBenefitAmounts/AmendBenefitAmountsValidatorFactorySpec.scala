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

package v2.amendBenefitAmounts

import config.MockStateBenefitsAppConfig
import play.api.libs.json.Json
import shared.controllers.validators.Validator
import shared.models.utils.JsonErrorValidators
import shared.utils.UnitSpec
import v2.amendBenefitAmounts.def1.Def1_AmendBenefitAmountsValidator
import v2.amendBenefitAmounts.model.request.AmendBenefitAmountsRequestData

class AmendBenefitAmountsValidatorFactorySpec extends UnitSpec with JsonErrorValidators with MockStateBenefitsAppConfig {

  private val validNino      = "AA123456A"
  private val validTaxYear   = "2023-24"
  private val validBenefitId = "b1e8057e-fbbc-47a8-a8b4-78d9f015c253"

  private val validBody = Json.parse("""
      |{
      |  "amount": 2050.45,
      |  "taxPaid": 1095.55
      |}""".stripMargin)

  private val invalidTaxYear = "2023"

  private val validatorFactory = new AmendBenefitAmountsValidatorFactory

  "validator" should {
    "return the Def1 validator" when {
      "given a valid request" in new AppConfigTest {
        val result: Validator[AmendBenefitAmountsRequestData] = validatorFactory.validator(validNino, validTaxYear, validBenefitId, validBody)
        result shouldBe a[Def1_AmendBenefitAmountsValidator]
      }

      "given an invalid taxYear" in new AppConfigTest {
        val result: Validator[AmendBenefitAmountsRequestData] = validatorFactory.validator(validNino, invalidTaxYear, validBenefitId, validBody)
        result shouldBe a[Def1_AmendBenefitAmountsValidator]

      }
    }
  }
}
