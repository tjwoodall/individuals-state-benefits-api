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

import api.controllers.validators.Validator
import api.controllers.validators.resolvers._
import api.models.errors.{MtdError, StartDateFormatError}
import cats.data.Validated
import cats.data.Validated.{Invalid, Valid}
import cats.implicits.catsSyntaxTuple4Semigroupal
import play.api.libs.json.JsValue
import v1.controllers.validators.resolvers.ResolveBenefitId
import v1.models.request.amendBenefit.{AmendBenefitRequestBody, AmendBenefitRequestData}

import java.time.LocalDate
import javax.inject.Singleton
import scala.annotation.nowarn

@Singleton
class AmendBenefitValidatorFactory {

  private val minYear = 1900
  private val maxYear = 2100

  @nowarn("cat=lint-byname-implicit")
  private val resolveJson = new ResolveNonEmptyJsonObject[AmendBenefitRequestBody]()

  private val resolveTaxYear = DetailedResolveTaxYear(maybeMinimumTaxYear = Some(minimumPermittedTaxYear.year))

  def validator(nino: String, taxYear: String, benefitId: String, body: JsValue): Validator[AmendBenefitRequestData] =
    new Validator[AmendBenefitRequestData] {

      def validate: Validated[Seq[MtdError], AmendBenefitRequestData] =
        (
          ResolveNino(nino),
          resolveTaxYear(taxYear),
          ResolveBenefitId(benefitId),
          resolveJson(body)
        ).mapN(AmendBenefitRequestData) andThen validateBusinessRules

      private def validateBusinessRules(parsed: AmendBenefitRequestData): Validated[Seq[MtdError], AmendBenefitRequestData] = {
        import parsed.body._

        val validatedDates = endDate match {
          case Some(endDate) => ResolveDateRange.withLimits(minYear, maxYear)(startDate -> endDate)
          case None =>
            ResolveIsoDate(startDate, Some(StartDateFormatError), None)
              .andThen(date =>
                if (date.isBefore(LocalDate.ofYearDay(minYear, 1)))
                  Invalid(List(StartDateFormatError))
                else
                  Valid(date))
        }

        validatedDates.map(_ => parsed)
      }

    }

}
