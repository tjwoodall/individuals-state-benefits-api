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

import api.models.errors.{EndDateFormatError, MtdError, StartDateFormatError}

import java.time.LocalDate
import scala.util.{Failure, Success, Try}

object DateFormatValidation {

  private val minYear = 1900
  private val maxYear = 2100

  def validate(date: String, isStartDate: Boolean = false): List[MtdError] = Try {
    LocalDate.parse(date, dateFormat)
  } match {
    case Success(localDate) => validateStartAndEndDate(localDate, isStartDate)
    case Failure(_)         => if (isStartDate) List(StartDateFormatError) else List(EndDateFormatError)
  }

  private def validateStartAndEndDate(date: LocalDate, isStartDate: Boolean): List[MtdError] = {
    if (isStartDate && date.getYear <= minYear) {
      List(StartDateFormatError)
    } else if (!isStartDate && date.getYear >= maxYear) {
      List(EndDateFormatError)
    } else {
      Nil
    }
  }

}
