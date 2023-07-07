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

package v1.controllers.requestParsers.validators

import api.controllers.requestParsers.validators.Validator
import api.controllers.requestParsers.validators.validations.DecimalValueValidation.BIG_DECIMAL_MINIMUM_INCLUSIVE
import api.controllers.requestParsers.validators.validations._
import api.models.errors.MtdError
import config.AppConfig
import v1.models.request.AmendBenefitAmounts.{AmendBenefitAmountsRawData, AmendBenefitAmountsRequestBody}

import javax.inject.Inject

class AmendBenefitAmountsValidator @Inject() (implicit appConfig: AppConfig) extends Validator[AmendBenefitAmountsRawData] {

  private val validationSet = List(parameterFormatValidation, parameterRuleValidation, bodyFormatValidator, bodyValueValidator)

  override def validate(data: AmendBenefitAmountsRawData): List[MtdError] = {
    run(validationSet, data).distinct
  }

  private def parameterFormatValidation: AmendBenefitAmountsRawData => List[List[MtdError]] = (data: AmendBenefitAmountsRawData) => {
    List(
      NinoValidation.validate(data.nino),
      TaxYearValidation.validate(data.taxYear),
      BenefitIdValidation.validate(data.benefitId)
    )
  }

  private def parameterRuleValidation: AmendBenefitAmountsRawData => List[List[MtdError]] = { data =>
    List(
      TaxYearNotSupportedValidation.validate(data.taxYear)
    )
  }

  private def bodyFormatValidator: AmendBenefitAmountsRawData => List[List[MtdError]] = { data =>
    List(
      JsonFormatValidation.validate[AmendBenefitAmountsRequestBody](data.body.json)
    )
  }

  private def bodyValueValidator: AmendBenefitAmountsRawData => List[List[MtdError]] = (data: AmendBenefitAmountsRawData) => {
    val requestBodyData: AmendBenefitAmountsRequestBody = data.body.json.as[AmendBenefitAmountsRequestBody]

    List(
      DecimalValueValidation.validate(
        amount = requestBodyData.amount,
        path = "/amount"
      ),
      DecimalValueValidation.validateOptional(
        amount = requestBodyData.taxPaid,
        path = "/taxPaid",
        minValue = -99999999999.99,
        message = BIG_DECIMAL_MINIMUM_INCLUSIVE
      )
    )
  }

}
