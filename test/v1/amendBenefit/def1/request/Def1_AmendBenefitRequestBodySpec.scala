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

package v1.amendBenefit.def1.request

import play.api.libs.json.*
import shared.models.utils.JsonErrorValidators
import shared.utils.UnitSpec
import v1.amendBenefit.def1.model.request.Def1_AmendBenefitRequestBody

class Def1_AmendBenefitRequestBodySpec extends UnitSpec with JsonErrorValidators {

  val inputJson: JsValue = Json.parse(
    """
      |{
      |   "startDate": "2019-04-06",
      |   "endDate": "2020-01-01"
      |}
        """.stripMargin
  )

  "reads" when {
    "passed valid JSON" should {
      "return a valid model" in {
        inputJson.as[Def1_AmendBenefitRequestBody] shouldBe Def1_AmendBenefitRequestBody("2019-04-06", Some("2020-01-01"))
      }

      testMandatoryFields[Def1_AmendBenefitRequestBody](inputJson)("/startDate")

      testPropertyType[Def1_AmendBenefitRequestBody](inputJson)(
        path = "/startDate",
        replacement = 12344.toJson,
        expectedError = JsonError.STRING_FORMAT_EXCEPTION
      )

      testPropertyType[Def1_AmendBenefitRequestBody](inputJson)(
        path = "/endDate",
        replacement = 12344.toJson,
        expectedError = JsonError.STRING_FORMAT_EXCEPTION
      )
    }
  }

  "writes" should {
    "return a json" when {
      "a valid object is supplied" in {
        Json.toJson(Def1_AmendBenefitRequestBody("2019-04-06", Some("2020-01-01"))) shouldBe inputJson
      }
    }
  }

}
