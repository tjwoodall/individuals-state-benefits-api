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

import api.mocks.MockCurrentDateTime
import api.models.errors._
import config.AppConfig
import mocks.MockAppConfig
import org.joda.time.DateTime
import org.joda.time.format.{DateTimeFormat, DateTimeFormatter}
import play.api.libs.json.{JsObject, Json}
import play.api.mvc.AnyContentAsJson
import support.UnitSpec
import utils.CurrentDateTime
import v1.models.request.createBenefit.CreateBenefitRawData

class CreateBenefitValidatorSpec extends UnitSpec {
  private val validNino    = "AA123456A"
  private val validTaxYear = "2020-21"

  private val startDate = "2020-08-03"
  private val endDate   = "2020-12-03"

  private def requestJson(benefitType: String = "statePension", startDate: String = startDate, endDate: String = endDate) = AnyContentAsJson(
    Json.parse(
      s"""
         |{
         |  "benefitType": "$benefitType",
         |  "startDate": "$startDate",
         |  "endDate": "$endDate"
         |}
      """.stripMargin
    ))

  private val validRawBody = requestJson(startDate = "2019-01-01", endDate = "2020-06-01")

  class Test extends MockCurrentDateTime with MockAppConfig {

    implicit val dateTimeProvider: CurrentDateTime = mockCurrentDateTime
    val dateTimeFormatter: DateTimeFormatter       = DateTimeFormat.forPattern("yyyy-MM-dd")

    implicit val appConfig: AppConfig = mockAppConfig

    val validator: CreateBenefitValidator = new CreateBenefitValidator()

    MockCurrentDateTime.getCurrentDate
      .returns(DateTime.parse("2022-07-11", dateTimeFormatter))
      .anyNumberOfTimes()

    MockedAppConfig.minimumPermittedTaxYear.returns(2021)
  }

  "CreateBenefitValidator" when {
    "running a validation" should {
      "return no errors for a valid request" in new Test {
        validator.validate(CreateBenefitRawData(validNino, validTaxYear, validRawBody)) shouldBe Nil
      }

      "return NinoFormatError error when the supplied NINO is invalid" in new Test {
        validator.validate(CreateBenefitRawData("A12344A", validTaxYear, validRawBody)) shouldBe
          List(NinoFormatError)
      }

      "return TaxYearFormatError error for an invalid tax year format" in new Test {
        validator.validate(CreateBenefitRawData(validNino, "20178", validRawBody)) shouldBe
          List(TaxYearFormatError)
      }

      "return RuleTaxYearRangeInvalidError error for an invalid tax year range" in new Test {
        validator.validate(CreateBenefitRawData(validNino, "2018-20", validRawBody)) shouldBe
          List(RuleTaxYearRangeInvalidError)
      }

      "return multiple errors for multiple invalid request parameters" in new Test {
        validator.validate(CreateBenefitRawData("notValid", "2018-20", validRawBody)) shouldBe
          List(NinoFormatError, RuleTaxYearRangeInvalidError)
      }

      "return RuleTaxYearNotSupportedError error for an unsupported tax year" in new Test {
        validator.validate(CreateBenefitRawData(validNino, "2019-20", validRawBody)) shouldBe
          List(RuleTaxYearNotSupportedError)
      }

      "return RuleIncorrectOrEmptyBodyError error for an empty request body" in new Test {
        validator.validate(CreateBenefitRawData(validNino, validTaxYear, AnyContentAsJson(JsObject.empty))) shouldBe
          List(RuleIncorrectOrEmptyBodyError)
      }

      "return RuleIncorrectOrEmptyBodyError error for an incorrect request body" in new Test {
        private val paths: Seq[String] = List("/benefitType", "/endDate", "/startDate")
        private val body: AnyContentAsJson = AnyContentAsJson(Json.parse(s"""
             |{ 
             |  "benefitType": true, 
             |  "startDate": true, 
             |  "endDate": false
             |}""".stripMargin))

        validator.validate(CreateBenefitRawData(validNino, validTaxYear, body)) shouldBe
          List(RuleIncorrectOrEmptyBodyError.copy(paths = Some(paths)))
      }

      "return BenefitTypeFormatError error for an incorrect benefitType" in new Test {
        validator.validate(CreateBenefitRawData(validNino, validTaxYear, requestJson(benefitType = "invalidBenefit"))) shouldBe
          List(BenefitTypeFormatError)
      }

      "return EndDateBeforeStartDateRuleError error for an incorrect End Date" in new Test {
        validator.validate(CreateBenefitRawData(validNino, validTaxYear, requestJson(startDate = endDate, endDate = startDate))) shouldBe
          List(RuleEndDateBeforeStartDateError)
      }

      "return EndDateFormatError error for an incorrect End Date" in new Test {
        validator.validate(CreateBenefitRawData(validNino, validTaxYear, requestJson(endDate = "20201203"))) shouldBe
          List(EndDateFormatError)
      }

      "return StartDateFormatError error for an incorrect Start Date" in new Test {
        validator.validate(CreateBenefitRawData(validNino, validTaxYear, requestJson(startDate = "20201203"))) shouldBe
          List(StartDateFormatError)
      }

      "return multiple errors for incorrect field formats" in new Test {
        validator.validate(CreateBenefitRawData(validNino, validTaxYear, requestJson("invalid", "invalid", "invalid"))) shouldBe
          List(BenefitTypeFormatError, StartDateFormatError, EndDateFormatError)
      }
    }
  }

}
