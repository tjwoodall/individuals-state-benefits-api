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

import shared.controllers.validators.Validator
import play.api.libs.json.Json
import shared.utils.UnitSpec
import v2.amendBenefit.def1.Def1_AmendBenefitValidator
import v2.amendBenefit.model.request.AmendBenefitRequestData
import config.MockStateBenefitsAppConfig

class AmendBenefitValidatorFactorySpec extends UnitSpec with MockStateBenefitsAppConfig {

  private val validNino      = "AA123456A"
  private val validTaxYear   = "2022-23"
  private val invalidTaxYear = "2023"
  private val startDate      = "2020-04-06"
  private val endDate        = "2021-01-01"
  private val validBenefitId = "b1e8057e-fbbc-47a8-a8b4-78d9f015c253"

  private def validBody(startDate: String = startDate, endDate: String = endDate) = Json.parse(
    s"""
       |{
       |  "startDate": "$startDate",
       |  "endDate": "$endDate"
       |}
      """.stripMargin
  )

  private val validatorFactory = new AmendBenefitValidatorFactory

  "validator" should {
    "return the Def1 validator" when {
      "given a valid request" in new AppConfigTest {
        val result: Validator[AmendBenefitRequestData] = validatorFactory.validator(validNino, validTaxYear, validBenefitId, validBody())
        result shouldBe a[Def1_AmendBenefitValidator]

      }

      "given an invalid taxYear" in new AppConfigTest {
        val result: Validator[AmendBenefitRequestData] = validatorFactory.validator(validNino, invalidTaxYear, validBenefitId, validBody())
        result shouldBe a[Def1_AmendBenefitValidator]

      }
    }
  }
}
