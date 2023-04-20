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

package api.models.audit

import api.models.errors.TaxYearFormatError
import play.api.http.Status.{BAD_REQUEST, OK}
import play.api.libs.json.{JsValue, Json}
import support.UnitSpec

class GenericAuditDetailSpec extends UnitSpec {

  val nino                                 = "XX751130C"
  val taxYear                              = "2020-21"
  val benefitId                            = "a1e8057e-fbbc-47a8-a8b4-78d9f015c253"
  val userType                             = "Agent"
  val agentReferenceNumber: Option[String] = Some("012345678")
  val correlationId                        = "a1e8057e-fbbc-47a8-a8b4-78d9f015c253"

  val auditDetailJsonSuccess: JsValue = Json.parse(
    s"""
       |{
       |   "userType":"$userType",
       |   "agentReferenceNumber":"${agentReferenceNumber.get}",
       |   "nino":"$nino",
       |   "taxYear":"$taxYear",
       |   "request":{
       |      "benefitType":"incapacityBenefit",
       |      "startDate":"2020-08-03",
       |      "endDate":"2020-12-03"
       |   },
       |   "X-CorrelationId":"$correlationId",
       |   "response":{
       |      "httpStatus":$OK,
       |      "body":{
       |         "benefitId":"$benefitId",
       |         "links":[
       |            {
       |               "href":"/individuals/state-benefits/$nino/$taxYear",
       |               "rel":"self",
       |               "method":"GET"
       |            },
       |            {
       |               "href":"/individuals/state-benefits/$nino/$taxYear/$benefitId",
       |               "rel":"amend-state-benefit",
       |               "method":"PUT"
       |            },
       |            {
       |               "href":"/individuals/state-benefits/$nino/$taxYear/$benefitId",
       |               "rel":"delete-state-benefit",
       |               "method":"DELETE"
       |            }
       |         ]
       |      }
       |   }
       |}
    """.stripMargin
  )

  val auditDetailModelSuccess: GenericAuditDetail = GenericAuditDetail(
    userType = userType,
    agentReferenceNumber = agentReferenceNumber,
    pathParams = Map("nino" -> nino, "taxYear" -> taxYear),
    queryParams = None,
    requestBody = Some(
      Json.parse(
        """
          |{
          |  "benefitType": "incapacityBenefit",
          |  "startDate": "2020-08-03",
          |  "endDate": "2020-12-03"
          |}
        """.stripMargin
      )),
    `X-CorrelationId` = correlationId,
    auditResponse = AuditResponse(
      OK,
      Right(Some(Json.parse(s"""
           |{
           |  "benefitId": "$benefitId",
           |  "links": [
           |    {
           |      "href": "/individuals/state-benefits/$nino/$taxYear",
           |      "rel": "self",
           |      "method": "GET"
           |    },
           |    {
           |      "href": "/individuals/state-benefits/$nino/$taxYear/$benefitId",
           |      "rel": "amend-state-benefit",
           |      "method": "PUT"
           |    },
           |    {
           |      "href": "/individuals/state-benefits/$nino/$taxYear/$benefitId",
           |      "rel": "delete-state-benefit",
           |      "method": "DELETE"
           |    }
           |  ]
           |}
        """.stripMargin)))
    )
  )

  val invalidTaxYearAuditDetailJson: JsValue = Json.parse(
    s"""
       |{
       |    "userType": "$userType",
       |    "agentReferenceNumber": "${agentReferenceNumber.get}",
       |    "nino": "$nino",
       |    "taxYear" : "2020-2021",
       |   "request":{
       |      "benefitType":"incapacityBenefit",
       |      "startDate":"2020-08-03",
       |      "endDate":"2020-12-03"
       |   },
       |    "X-CorrelationId":"$correlationId",
       |    "response": {
       |      "httpStatus":$BAD_REQUEST,
       |      "errors": [
       |        {
       |          "errorCode":"FORMAT_TAX_YEAR"
       |        }
       |      ]
       |    }
       |}
    """.stripMargin
  )

  val invalidTaxYearAuditDetailModel: GenericAuditDetail = GenericAuditDetail(
    userType = userType,
    agentReferenceNumber = agentReferenceNumber,
    pathParams = Map("nino" -> nino, "taxYear" -> "2020-2021"),
    queryParams = None,
    requestBody = Some(
      Json.parse(
        """
          |{
          |  "benefitType": "incapacityBenefit",
          |  "startDate": "2020-08-03",
          |  "endDate": "2020-12-03"
          |}
      """.stripMargin
      )),
    `X-CorrelationId` = correlationId,
    auditResponse = AuditResponse(BAD_REQUEST, Left(Seq(AuditError(TaxYearFormatError.code))))
  )

  "GenericAuditDetail" when {
    "written to JSON (success)" should {
      "produce the expected JsObject" in {
        Json.toJson(auditDetailModelSuccess) shouldBe auditDetailJsonSuccess
      }
    }

    "written to JSON (error)" should {
      "produce the expected JsObject" in {
        Json.toJson(invalidTaxYearAuditDetailModel) shouldBe invalidTaxYearAuditDetailJson
      }
    }
  }

}
