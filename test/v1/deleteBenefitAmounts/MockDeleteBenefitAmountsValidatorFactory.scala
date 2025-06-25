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

package v1.deleteBenefitAmounts

import shared.controllers.validators.Validator
import shared.models.errors.MtdError
import cats.data.Validated
import cats.data.Validated.{Invalid, Valid}
import org.scalamock.handlers.CallHandler
import org.scalamock.scalatest.MockFactory
import org.scalatest.TestSuite
import v1.deleteBenefitAmounts.model.request.DeleteBenefitAmountsRequestData

trait MockDeleteBenefitAmountsValidatorFactory extends TestSuite with MockFactory {

  val mockDeleteBenefitAmountsValidatorFactory: DeleteBenefitAmountsValidatorFactory =
    mock[DeleteBenefitAmountsValidatorFactory]

  object MockedDeleteBenefitAmountsValidatorFactory {

    def validator(): CallHandler[Validator[DeleteBenefitAmountsRequestData]] =
      (mockDeleteBenefitAmountsValidatorFactory.validator(_: String, _: String, _: String)).expects(*, *, *)

  }

  def willUseValidator(use: Validator[DeleteBenefitAmountsRequestData]): CallHandler[Validator[DeleteBenefitAmountsRequestData]] = {
    MockedDeleteBenefitAmountsValidatorFactory
      .validator()
      .anyNumberOfTimes()
      .returns(use)
  }

  def returningSuccess(result: DeleteBenefitAmountsRequestData): Validator[DeleteBenefitAmountsRequestData] =
    new Validator[DeleteBenefitAmountsRequestData] {
      def validate: Validated[Seq[MtdError], DeleteBenefitAmountsRequestData] = Valid(result)
    }

  def returning(result: MtdError*): Validator[DeleteBenefitAmountsRequestData] = returningErrors(result)

  def returningErrors(result: Seq[MtdError]): Validator[DeleteBenefitAmountsRequestData] =
    new Validator[DeleteBenefitAmountsRequestData] {
      def validate: Validated[Seq[MtdError], DeleteBenefitAmountsRequestData] = Invalid(result)
    }

}
