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
import api.models.errors.{EndDateFormatError, MtdError, StartDateFormatError}
import config.AppConfig
import v1.models.request.createBenefit.{CreateBenefitRawData, CreateBenefitRequestBody}

import java.time.LocalDate
import javax.inject.Inject

class CreateBenefitValidator @Inject() (implicit appConfig: AppConfig) extends Validator[CreateBenefitRawData] {

  private val validationSet =
    List(parameterFormatValidation, parameterRuleValidation, bodyFormatValidation, bodyParameterFormatValidation, bodyParameterValidation)

  private def parameterFormatValidation: CreateBenefitRawData => List[List[MtdError]] = (data: CreateBenefitRawData) => {
    List(
      NinoValidation.validate(data.nino),
      TaxYearValidation.validate(data.taxYear)
    )
  }

  private def parameterRuleValidation: CreateBenefitRawData => List[List[MtdError]] = { data =>
    List(
      TaxYearNotSupportedValidation.validate(data.taxYear)
    )
  }

  private def bodyFormatValidation: CreateBenefitRawData => List[List[MtdError]] = { data =>
    List(
      JsonFormatValidation.validate[CreateBenefitRequestBody](data.body.json)
    )
  }

  private def bodyParameterFormatValidation: CreateBenefitRawData => List[List[MtdError]] = { data =>
    val body = data.body.json.as[CreateBenefitRequestBody]

    List(
      BenefitTypeValidation.validate(body.benefitType),
      DateFormatValidation.validate(body.startDate, StartDateFormatError),
      body.endDate.map(DateFormatValidation.validate(_, EndDateFormatError)).getOrElse(NoValidationErrors)
    )
  }

  private def bodyParameterValidation: CreateBenefitRawData => List[List[MtdError]] = { data =>
    val body = data.body.json.as[CreateBenefitRequestBody]

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

  override def validate(data: CreateBenefitRawData): List[MtdError] = {
    run(validationSet, data).distinct
  }

}
