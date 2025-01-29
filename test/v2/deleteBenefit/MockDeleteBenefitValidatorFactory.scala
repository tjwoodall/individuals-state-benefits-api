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

package v2.deleteBenefit

import shared.controllers.validators.Validator
import shared.models.errors.MtdError
import cats.data.Validated
import cats.data.Validated.{Invalid, Valid}
import org.scalamock.handlers.CallHandler
import org.scalamock.scalatest.MockFactory
import v2.deleteBenefit.model.request.DeleteBenefitRequestData

trait MockDeleteBenefitValidatorFactory extends MockFactory {

  val mockDeleteBenefitValidatorFactory: DeleteBenefitValidatorFactory =
    mock[DeleteBenefitValidatorFactory]

  object MockedDeleteBenefitValidatorFactory {

    def validator(): CallHandler[Validator[DeleteBenefitRequestData]] =
      (mockDeleteBenefitValidatorFactory.validator(_: String, _: String, _: String)).expects(*, *, *)

  }

  def willUseValidator(use: Validator[DeleteBenefitRequestData]): CallHandler[Validator[DeleteBenefitRequestData]] = {
    MockedDeleteBenefitValidatorFactory
      .validator()
      .anyNumberOfTimes()
      .returns(use)
  }

  def returningSuccess(result: DeleteBenefitRequestData): Validator[DeleteBenefitRequestData] =
    new Validator[DeleteBenefitRequestData] {
      def validate: Validated[Seq[MtdError], DeleteBenefitRequestData] = Valid(result)
    }

  def returning(result: MtdError*): Validator[DeleteBenefitRequestData] = returningErrors(result)

  def returningErrors(result: Seq[MtdError]): Validator[DeleteBenefitRequestData] =
    new Validator[DeleteBenefitRequestData] {
      def validate: Validated[Seq[MtdError], DeleteBenefitRequestData] = Invalid(result)
    }

}
