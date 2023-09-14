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
import api.controllers.requestParsers.validators.validations._
import api.models.errors.{MtdError}
import config.AppConfig
import v1.models.request.AmendBenefit.{AmendBenefitRawData, AmendBenefitRequestBody}

import java.time.LocalDate
import javax.inject.Inject

class AmendBenefitValidator @Inject() (implicit appConfig: AppConfig) extends Validator[AmendBenefitRawData] {

  private val validationSet =
    List(parameterFormatValidation, parameterRuleValidation, bodyFormatValidator, bodyParameterFormatValidation, bodyValueValidator)

  override def validate(data: AmendBenefitRawData): List[MtdError] = {
    run(validationSet, data).distinct
  }

  private def parameterFormatValidation: AmendBenefitRawData => List[List[MtdError]] = (data: AmendBenefitRawData) => {
    List(
      NinoValidation.validate(data.nino),
      TaxYearValidation.validate(data.taxYear),
      BenefitIdValidation.validate(data.benefitId)
    )
  }

  private def parameterRuleValidation: AmendBenefitRawData => List[List[MtdError]] = { data =>
    List(
      TaxYearNotSupportedValidation.validate(data.taxYear)
    )
  }

  private def bodyFormatValidator: AmendBenefitRawData => List[List[MtdError]] = { data =>
    List(
      JsonFormatValidation.validate[AmendBenefitRequestBody](data.body.json)
    )
  }

  private def bodyParameterFormatValidation: AmendBenefitRawData => List[List[MtdError]] = { data =>
    val body = data.body.json.as[AmendBenefitRequestBody]

    List(
      DateFormatValidation.validate(body.startDate, isStartDate = true),
      body.endDate.map(DateFormatValidation.validate(_)).getOrElse(NoValidationErrors)
    )
  }

  private def bodyValueValidator: AmendBenefitRawData => List[List[MtdError]] = (data: AmendBenefitRawData) => {
    val body: AmendBenefitRequestBody = data.body.json.as[AmendBenefitRequestBody]

    val dateOrderValidationErrors = body.endDate match {
      case Some(end) =>
        val parsedStart = LocalDate.parse(body.startDate)
        val parsedEnd   = LocalDate.parse(end)
        DateOrderValidation.validate(parsedStart, parsedEnd)
      case _ => NoValidationErrors
    }

    List(
      dateOrderValidationErrors
    )
  }

}
