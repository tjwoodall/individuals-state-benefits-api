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
import v1.models.request.AmendBenefit.AmendBenefitRawData

class AmendBenefitValidatorSpec extends UnitSpec {

  private val validNino    = "AA123456A"
  private val validTaxYear = "2020-21"
  private val benefitId    = "4557ecb5-fd32-48cc-81f5-e6acd1099f3c"

  private val startDate = "2020-04-06"
  private val endDate   = "2021-01-01"

  private def requestJson(startDate: String = startDate, endDate: String = endDate) = AnyContentAsJson(
    Json.parse(
      s"""
         |{
         |  "startDate": "$startDate",
         |  "endDate": "$endDate"
         |}
      """.stripMargin
    ))

  private val validRawBody = requestJson()

  class Test extends MockCurrentDateTime with MockAppConfig {

    implicit val dateTimeProvider: CurrentDateTime = mockCurrentDateTime
    val dateTimeFormatter: DateTimeFormatter       = DateTimeFormat.forPattern("yyyy-MM-dd")

    implicit val appConfig: AppConfig = mockAppConfig

    val validator = new AmendBenefitValidator()

    MockCurrentDateTime.getCurrentDate
      .returns(DateTime.parse("2022-07-11", dateTimeFormatter))
      .anyNumberOfTimes()

    MockedAppConfig.minimumPermittedTaxYear
      .returns(2021)

  }

  "AmendBenefitValidator" when {
    "running a validation" should {
      "return no errors for a valid request" in new Test {
        validator.validate(AmendBenefitRawData(validNino, validTaxYear, benefitId, validRawBody)) shouldBe Nil
      }

      "return NinoFormatError error when the supplied NINO is invalid" in new Test {
        validator.validate(AmendBenefitRawData("A12344A", validTaxYear, benefitId, validRawBody)) shouldBe
          List(NinoFormatError)
      }

      "return TaxYearFormatError error for an invalid tax year format" in new Test {
        validator.validate(AmendBenefitRawData(validNino, "20178", benefitId, validRawBody)) shouldBe
          List(TaxYearFormatError)
      }

      "return BenefitIdFormatError error for an incorrect benefitType" in new Test {
        validator.validate(AmendBenefitRawData(validNino, validTaxYear, "invalid", validRawBody)) shouldBe
          List(BenefitIdFormatError)
      }

      "return RuleTaxYearRangeInvalidError error for an invalid tax year range" in new Test {
        validator.validate(AmendBenefitRawData(validNino, "2018-20", benefitId, validRawBody)) shouldBe
          List(RuleTaxYearRangeInvalidError)
      }

      "return multiple errors for multiple invalid request parameters" in new Test {
        validator.validate(AmendBenefitRawData("notValid", "2018-20", benefitId, validRawBody)) shouldBe
          List(NinoFormatError, RuleTaxYearRangeInvalidError)
      }

      "return RuleTaxYearNotSupportedError error for an unsupported tax year" in new Test {
        validator.validate(AmendBenefitRawData(validNino, "2018-19", benefitId, validRawBody)) shouldBe
          List(RuleTaxYearNotSupportedError)
      }

      "return RuleEndDateBeforeStartDateError error for an incorrect End Date" in new Test {
        validator.validate(AmendBenefitRawData(validNino, validTaxYear, benefitId, requestJson(startDate = endDate, endDate = startDate))) shouldBe
          List(RuleEndDateBeforeStartDateError)
      }

      "return RuleIncorrectOrEmptyBodyError error for an empty request body" in new Test {
        validator.validate(AmendBenefitRawData(validNino, validTaxYear, benefitId, AnyContentAsJson(JsObject.empty))) shouldBe
          List(RuleIncorrectOrEmptyBodyError)
      }

      "return RuleIncorrectOrEmptyBodyError error for an incorrect request body" in new Test {
        val paths: Seq[String]     = List("/startDate")
        val body: AnyContentAsJson = AnyContentAsJson(Json.parse("""{ "endDate": "2020-01-01" }""".stripMargin))

        validator.validate(AmendBenefitRawData(validNino, validTaxYear, benefitId, body)) shouldBe
          List(RuleIncorrectOrEmptyBodyError.copy(paths = Some(paths)))
      }

      "return multiple errors for incorrect field formats" in new Test {
        validator.validate(AmendBenefitRawData(validNino, validTaxYear, benefitId, requestJson("notValid", "notValid"))) shouldBe
          List(StartDateFormatError, EndDateFormatError)
      }

      "return start date format error when start date is before 1900" in new Test {
        val result: Seq[MtdError] = validator.validate(
          AmendBenefitRawData(validNino, validTaxYear, benefitId, requestJson(startDate = "1809-02-01"))
        )

        result shouldBe
          List(StartDateFormatError)
      }

      "return end date format error when end date is after 2100" in new Test {
        val result: Seq[MtdError] = validator.validate(
          AmendBenefitRawData(validNino, validTaxYear, benefitId, requestJson(endDate = "2149-02-21"))
        )

        result shouldBe
          List(EndDateFormatError)
      }
    }
  }

}
