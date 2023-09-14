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

import api.models.errors.{EndDateFormatError, StartDateFormatError}
import support.UnitSpec

class DateFormatValidationSpec extends UnitSpec {

  "DateFormatValidation.validate" should {
    "return an empty list for a valid date" when {
      "valid params are supplied" in {

        DateFormatValidation.validate(
          date = "2019-04-20"
        ) shouldBe NoValidationErrors
      }

      "return a DateFormatError for an invalid start date" in {
        DateFormatValidation.validate(
          date = "2019-04-40",
          isStartDate = true
        ) shouldBe List(StartDateFormatError)
      }

      "return a DateFormatError for an invalid end date" in {
        DateFormatValidation.validate(
          date = "2019-04-40"
        ) shouldBe List(EndDateFormatError)
      }
    }

    "return date validation errors" when {
      "the start date is too early" in {
        DateFormatValidation.validate(
          date = "1890-04-14",
          isStartDate = true
        ) shouldBe List(StartDateFormatError)
      }

      "the end date is too late" in {
        DateFormatValidation.validate(
          date = "2102-10-21"
        ) shouldBe List(EndDateFormatError)
      }
    }
  }

}
