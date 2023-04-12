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

package api.controllers.requestParsers.validators.validations

import api.models.errors.RuleEndDateBeforeStartDateError
import support.UnitSpec

import java.time.LocalDate

class DateOrderValidationSpec extends UnitSpec {
  private val earlier = "2020-10-10"
  private val later   = "2021-10-10"

  "DateOrderValidation.validate" should {
    "return an empty list for dates in a valid order" in {
      val validStartDate = LocalDate.parse(earlier)
      val validEndDate   = LocalDate.parse(later)

      val result = DateOrderValidation.validate(validStartDate, validEndDate)
      result shouldBe NoValidationErrors
    }

    "return an empty list for two identical dates" in {
      val date = LocalDate.parse(earlier)

      val result = DateOrderValidation.validate(date, date)
      result shouldBe NoValidationErrors
    }

    "return a List(RuleEndDateBeforeStartDateError) for dates in an invalid order" in {
      val invalidStartDate = LocalDate.parse(later)
      val invalidEndDate   = LocalDate.parse(earlier)

      val result = DateOrderValidation.validate(invalidStartDate, invalidEndDate)
      result shouldBe List(RuleEndDateBeforeStartDateError)
    }

  }

}
