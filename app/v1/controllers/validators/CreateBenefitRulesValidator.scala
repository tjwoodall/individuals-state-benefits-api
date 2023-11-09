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

package v1.controllers.validators

import api.controllers.validators.RulesValidator
import api.controllers.validators.resolvers.{ResolveDateRange, ResolveIsoDate}
import api.models.domain.BenefitType._
import api.models.errors.{BenefitTypeFormatError, MtdError, StartDateFormatError}
import cats.data.Validated
import cats.data.Validated.Invalid
import v1.models.request.createBenefit.CreateBenefitRequestData

import java.time.LocalDate

object CreateBenefitRulesValidator extends RulesValidator[CreateBenefitRequestData] {

  private val minYear = 1900
  private val maxYear = 2100

  private lazy val minDate = LocalDate.ofYearDay(minYear, 1)
  private lazy val maxDate = LocalDate.ofYearDay(maxYear, 1)

  private val availableBenefitTypes = List(
    statePension,
    statePensionLumpSum,
    employmentSupportAllowance,
    jobSeekersAllowance,
    bereavementAllowance,
    otherStateBenefits,
    incapacityBenefit
  ).map(_.toString)

  def validateBusinessRules(parsed: CreateBenefitRequestData): Validated[Seq[MtdError], CreateBenefitRequestData] =
    combine(
      validateBenefitType(parsed.body.benefitType),
      validateDates(parsed.body.startDate, parsed.body.endDate)
    ).onSuccess(parsed)

  private def validateBenefitType(benefitType: String): Validated[Seq[MtdError], Unit] =
    if (availableBenefitTypes.contains(benefitType)) valid else Invalid(List(BenefitTypeFormatError))

  private def validateDates(startDate: String, endDate: Option[String]): Validated[Seq[MtdError], Unit] =
    endDate match {
      case Some(endDate) => ResolveDateRange.withLimits(minYear, maxYear)(startDate -> endDate).toUnit
      case None =>
        ResolveIsoDate(startDate, Some(StartDateFormatError), None)
          .andThen(date =>
            if (date.isBefore(minDate) || !date.isBefore(maxDate))
              Invalid(List(StartDateFormatError))
            else
              valid)
    }

}
