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

package v2.endpoints

import common.errors.{BenefitIdFormatError, RuleIgnoreForbiddenError, RuleOutsideAmendmentWindow}
import play.api.http.HeaderNames.ACCEPT
import play.api.http.Status._
import play.api.libs.json.{JsObject, Json}
import play.api.libs.ws.{WSRequest, WSResponse}
import play.api.test.Helpers.AUTHORIZATION
import shared.models.errors._
import shared.services.{AuditStub, AuthStub, DownstreamStub, MtdIdLookupStub}
import shared.support.IntegrationBaseSpec

class IgnoreBenefitControllerHipISpec extends IntegrationBaseSpec {

  override def servicesConfig: Map[String, Any] =
    Map("feature-switch.ifs_hip_migration_1944.enabled" -> true) ++ super.servicesConfig

  def downstreamQueryParams: Map[String, String] = Map("taxYear" -> "25-26")

  "Calling the 'ignore benefit' endpoint" should {
    "return a 204 status code" when {
      "any valid request is made" in new Test {

        override def setupStubs(): Unit = {
          AuditStub.audit()
          AuthStub.authorised()
          MtdIdLookupStub.ninoFound(nino)
          DownstreamStub
            .when(DownstreamStub.PUT, downstreamUri, downstreamQueryParams)
            .thenReturn(CREATED, JsObject.empty)
        }

        val response: WSResponse = await(request().post(JsObject.empty))
        response.status shouldBe NO_CONTENT
      }
    }

    "return error according to spec" when {
      "validation error" when {
        def validationErrorTest(requestNino: String,
                                requestTaxYear: String,
                                requestBenefitId: String,
                                expectedStatus: Int,
                                expectedBody: MtdError,
                                scenario: Option[String]): Unit = {
          s"validation fails with ${expectedBody.code} error ${scenario.getOrElse("")}" in new Test {

            override val nino: String      = requestNino
            override val taxYear: String   = requestTaxYear
            override val benefitId: String = requestBenefitId

            val response: WSResponse = await(request().post(JsObject.empty))
            response.status shouldBe expectedStatus
            response.json shouldBe Json.toJson(expectedBody)
          }
        }

        val input = List(
          ("AA1123A", "2025-26", "4557ecb5-fd32-48cc-81f5-e6acd1099f3c", BAD_REQUEST, NinoFormatError, None),
          ("AA123456A", "20199", "78d9f015-a8b4-47a8-8bbc-c253a1e8057e", BAD_REQUEST, TaxYearFormatError, None),
          ("AA123456A", "2025-26", "ABCDE12345FG", BAD_REQUEST, BenefitIdFormatError, None),
          ("AA123456A", "2018-19", "78d9f015-a8b4-47a8-8bbc-c253a1e8057e", BAD_REQUEST, RuleTaxYearNotSupportedError, None),
          ("AA123456A", "2025-27", "4557ecb5-fd32-48cc-81f5-e6acd1099f3c", BAD_REQUEST, RuleTaxYearRangeInvalidError, None)
        )

        input.foreach(args => (validationErrorTest _).tupled(args))
      }

      "downstream service error" when {
        def serviceErrorTest(downstreamStatus: Int, downstreamCode: String, expectedStatus: Int, expectedBody: MtdError): Unit = {
          s"downstream returns an $downstreamCode error and status $downstreamStatus" in new Test {

            override def setupStubs(): Unit = {
              DownstreamStub.onError(DownstreamStub.PUT, downstreamUri, downstreamQueryParams, downstreamStatus, errorBody(downstreamCode))
            }

            val response: WSResponse = await(request().post(JsObject.empty))
            response.status shouldBe expectedStatus
            response.json shouldBe Json.toJson(expectedBody)
          }
        }

        val errors = List(
          (BAD_REQUEST, "1215", BAD_REQUEST, NinoFormatError),
          (BAD_REQUEST, "1117", BAD_REQUEST, TaxYearFormatError),
          (BAD_REQUEST, "1231", BAD_REQUEST, BenefitIdFormatError),
          (BAD_REQUEST, "UNMATCHED_STUB_ERROR", BAD_REQUEST, RuleIncorrectGovTestScenarioError),
          (BAD_REQUEST, "1216", INTERNAL_SERVER_ERROR, InternalError),
          (UNPROCESSABLE_ENTITY, "1232", BAD_REQUEST, RuleIgnoreForbiddenError),
          (NOT_IMPLEMENTED, "5000", BAD_REQUEST, RuleTaxYearNotSupportedError),
          (NOT_FOUND, "5010", NOT_FOUND, NotFoundError),
          (UNPROCESSABLE_ENTITY, "4200", BAD_REQUEST, RuleOutsideAmendmentWindow),
          (UNPROCESSABLE_ENTITY, "1115", BAD_REQUEST, RuleTaxYearNotEndedError)
        )

        (errors).foreach(args => (serviceErrorTest _).tupled(args))
      }
    }
  }

  private trait Test {

    val nino: String      = "AA123456A"
    val benefitId: String = "b1e8057e-fbbc-47a8-a8b4-78d9f015c253"

    def taxYear: String = "2025-26"

    def downstreamUri: String = s"/itsd/income/ignore/state-benefits/$nino/$benefitId"

    def mtdUri: String = s"/$nino/$taxYear/$benefitId/ignore"

    def setupStubs(): Unit = {}

    def request(): WSRequest = {
      AuthStub.resetAll()
      AuditStub.audit()
      AuthStub.authorised()
      MtdIdLookupStub.ninoFound(nino)
      setupStubs()
      buildRequest(mtdUri)
        .withHttpHeaders(
          (ACCEPT, "application/vnd.hmrc.2.0+json"),
          (AUTHORIZATION, "Bearer 123") // some bearer token
        )
    }

    def errorBody(code: String): String =
      s"""
         |{
         |  "origin": "HIP",
         |  "response": [
         |    {
         |      "errorCode": "$code",
         |      "errorDescription": "downstream message"
         |    }
         |  ]
         |}
            """.stripMargin

  }

}
