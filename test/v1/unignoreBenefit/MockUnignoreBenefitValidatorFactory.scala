/*
 * Copyright 2024 HM Revenue & Customs
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

package v1.unignoreBenefit

import api.controllers.validators.Validator
import api.models.errors.MtdError
import cats.data.Validated
import cats.data.Validated.{Invalid, Valid}
import org.scalamock.handlers.CallHandler
import org.scalamock.scalatest.MockFactory
import v1.unignoreBenefit.model.request.Def1_UnignoreBenefitRequestData

trait MockUnignoreBenefitValidatorFactory extends MockFactory {

  val mockUnignoreBenefitValidatorFactory: UnignoreBenefitValidatorFactory =
    mock[UnignoreBenefitValidatorFactory]

  object MockedIgnoreBenefitValidatorFactory {

    def validator(): CallHandler[Validator[Def1_UnignoreBenefitRequestData]] =
      (mockUnignoreBenefitValidatorFactory.validator(_: String, _: String, _: String)).expects(*, *, *)

  }

  def willUseValidator(use: Validator[Def1_UnignoreBenefitRequestData]): CallHandler[Validator[Def1_UnignoreBenefitRequestData]] = {
    MockedIgnoreBenefitValidatorFactory
      .validator()
      .anyNumberOfTimes()
      .returns(use)
  }

  def returningSuccess(result: Def1_UnignoreBenefitRequestData): Validator[Def1_UnignoreBenefitRequestData] =
    new Validator[Def1_UnignoreBenefitRequestData] {
      def validate: Validated[Seq[MtdError], Def1_UnignoreBenefitRequestData] = Valid(result)
    }

  def returning(result: MtdError*): Validator[Def1_UnignoreBenefitRequestData] = returningErrors(result)

  def returningErrors(result: Seq[MtdError]): Validator[Def1_UnignoreBenefitRequestData] =
    new Validator[Def1_UnignoreBenefitRequestData] {
      def validate: Validated[Seq[MtdError], Def1_UnignoreBenefitRequestData] = Invalid(result)
    }

}
