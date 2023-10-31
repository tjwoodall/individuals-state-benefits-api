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

package v1.controllers.validators.resolvers

import api.models.errors.BenefitIdFormatError
import cats.data.Validated.{Invalid, Valid}
import support.UnitSpec
import v1.controllers.validators.resolvers
import v1.models.domain.BenefitId

class ResolveBenefitIdSpec extends UnitSpec {

  "ResolveBenefitId" should {
    "return no errors" when {
      "given a valid benefit ID" in {
        val validBenefitId = "b1e8057e-fbbc-47a8-a8b4-78d9f015c253"
        val result         = ResolveBenefitId(validBenefitId)
        result shouldBe Valid(BenefitId(validBenefitId))
      }
    }

    "return an error" when {
      "given an invalid benefit ID" in {
        val invalidBenefitId = ""
        val result           = resolvers.ResolveBenefitId(invalidBenefitId)
        result shouldBe Invalid(List(BenefitIdFormatError))
      }
    }
  }

}
