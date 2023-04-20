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

package v1.controllers.requestParsers

import api.models.domain.Nino
import api.models.errors._
import play.api.libs.json.{JsValue, Json}
import play.api.mvc.AnyContentAsJson
import support.UnitSpec
import v1.mocks.validators.MockAmendBenefitValidator
import v1.models.request.AmendBenefit.{AmendBenefitRawData, AmendBenefitRequest, AmendBenefitRequestBody}

class AmendBenefitRequestParserSpec extends UnitSpec {

  private val nino: String                   = "AA123456B"
  private val taxYear: String                = "2020-21"
  private val benefitId                      = "4557ecb5-fd32-48cc-81f5-e6acd1099f3c"
  implicit private val correlationId: String = "a1e8057e-fbbc-47a8-a8b4-78d9f015c253"

  private val validRequestJson: JsValue = Json.parse(
    """
      |{
      |  "startDate": "2020-04-06",
      |  "endDate": "2021-01-01"
      |}
    """.stripMargin
  )

  private val validRawBody = AnyContentAsJson(validRequestJson)

  private val amendBenefitRawData = AmendBenefitRawData(
    nino = nino,
    taxYear = taxYear,
    benefitId = benefitId,
    body = validRawBody
  )

  private val amendBenefitRequestBody = AmendBenefitRequestBody(
    startDate = "2020-04-06",
    endDate = Some("2021-01-01")
  )

  private val amendBenefitRequest = AmendBenefitRequest(
    nino = Nino(nino),
    taxYear = taxYear,
    benefitId = benefitId,
    body = amendBenefitRequestBody
  )

  trait Test extends MockAmendBenefitValidator {

    lazy val parser: AmendBenefitRequestParser = new AmendBenefitRequestParser(
      validator = mockAmendBenefitValidator
    )

  }

  "parse" should {
    "return a request object" when {
      "valid request data is supplied" in new Test {
        MockAmendBenefitValidator.validate(amendBenefitRawData).returns(Nil)
        parser.parseRequest(amendBenefitRawData) shouldBe Right(amendBenefitRequest)
      }
    }

    "return an ErrorWrapper" when {
      "a single validation error occurs" in new Test {
        MockAmendBenefitValidator
          .validate(amendBenefitRawData.copy(nino = "notANino"))
          .returns(List(NinoFormatError))

        parser.parseRequest(amendBenefitRawData.copy(nino = "notANino")) shouldBe
          Left(ErrorWrapper(correlationId, NinoFormatError, None))
      }

      "multiple path parameter validation errors occur" in new Test {
        MockAmendBenefitValidator
          .validate(amendBenefitRawData.copy(nino = "notANino", taxYear = "notATaxYear"))
          .returns(List(NinoFormatError, TaxYearFormatError))

        parser.parseRequest(amendBenefitRawData.copy(nino = "notANino", taxYear = "notATaxYear")) shouldBe
          Left(ErrorWrapper(correlationId, BadRequestError, Some(Seq(NinoFormatError, TaxYearFormatError))))
      }

      "multiple field value validation errors occur" in new Test {

        private val invalidValueRequestJson: JsValue = Json.parse(
          s"""
             |{
             |  "startDate": "noDate",
             |  "endDate": "noDate"
             |}
            """.stripMargin
        )

        private val invalidValueRawBody = AnyContentAsJson(invalidValueRequestJson)

        private val errors = List(
          StartDateFormatError,
          EndDateFormatError
        )

        MockAmendBenefitValidator
          .validate(amendBenefitRawData.copy(body = invalidValueRawBody))
          .returns(errors)

        parser.parseRequest(amendBenefitRawData.copy(body = invalidValueRawBody)) shouldBe
          Left(ErrorWrapper(correlationId, BadRequestError, Some(errors)))
      }
    }
  }

}
