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

package v1.controllers

import api.controllers.{ControllerBaseSpec, ControllerTestRunner}
import api.hateoas.Method.{DELETE, GET, PUT}
import api.hateoas.{HateoasWrapper, Link}
import api.mocks.hateoas.MockHateoasFactory
import api.mocks.services.MockAuditService
import api.models.audit.{AuditEvent, AuditResponse, GenericAuditDetailOld}
import api.models.domain.{BenefitType, Nino}
import api.models.errors._
import api.models.outcomes.ResponseWrapper
import mocks.MockAppConfig
import play.api.Configuration
import play.api.libs.json.{JsValue, Json}
import play.api.mvc.{AnyContentAsJson, Result}
import routing.Version1
import v1.mocks.requestParsers.MockCreateBenefitRequestParser
import v1.mocks.services.MockCreateBenefitService
import v1.models.request.createBenefit.{CreateBenefitRawData, CreateBenefitRequest, CreateBenefitRequestBody}
import v1.models.response.createBenefit.{CreateBenefitHateoasData, CreateBenefitResponse}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class CreateBenefitControllerSpec
    extends ControllerBaseSpec
    with ControllerTestRunner
    with MockAppConfig
    with MockCreateBenefitService
    with MockAuditService
    with MockCreateBenefitRequestParser
    with MockHateoasFactory {

  val taxYear: String   = "2019-20"
  val benefitId: String = "b1e8057e-fbbc-47a8-a8b4-78d9f015c253"
  val startDate         = "2020-08-03"
  val endDate           = "2020-12-03"

  val requestBodyJson: JsValue = Json.parse(
    s"""
       |{
       |  "benefitType": "incapacityBenefit",
       |  "startDate": "$startDate",
       |  "endDate" : "$endDate"
       |}
    """.stripMargin
  )

  val rawData: CreateBenefitRawData = CreateBenefitRawData(
    nino = nino,
    taxYear = taxYear,
    body = AnyContentAsJson(requestBodyJson)
  )

  val createStateBenefitRequestBody: CreateBenefitRequestBody = CreateBenefitRequestBody(
    startDate = "2019-01-01",
    endDate = Some("2020-06-01"),
    benefitType = BenefitType.incapacityBenefit.toString
  )

  val requestData: CreateBenefitRequest = CreateBenefitRequest(
    nino = Nino(nino),
    taxYear = taxYear,
    body = createStateBenefitRequestBody
  )

  val responseData: CreateBenefitResponse = CreateBenefitResponse(benefitId)

  private val testHateoasLinks = Seq(
    Link(href = s"/individuals/state-benefits/$nino/$taxYear?benefitId=$benefitId", method = GET, rel = "self"),
    api.hateoas.Link(href = s"/individuals/state-benefits/$nino/$taxYear/$benefitId", method = PUT, rel = "amend-state-benefit"),
    api.hateoas.Link(href = s"/individuals/state-benefits/$nino/$taxYear/$benefitId", method = DELETE, rel = "delete-state-benefit")
  )

  val responseJson: JsValue = Json.parse(
    s"""
       |{
       |   "benefitId": "$benefitId",
       |   "links": [
       |         {
       |         "href": "/individuals/state-benefits/$nino/$taxYear?benefitId=$benefitId",
       |         "rel": "self",
       |         "method": "GET"
       |      },
       |      {
       |         "href": "/individuals/state-benefits/$nino/$taxYear/$benefitId",
       |         "rel": "amend-state-benefit",
       |         "method": "PUT"
       |      },
       |      {
       |         "href": "/individuals/state-benefits/$nino/$taxYear/$benefitId",
       |         "rel": "delete-state-benefit",
       |         "method": "DELETE"
       |      }
       |      ]
       |}
    """.stripMargin
  )

  "CreateBenefitController" should {
    "return a successful response with status 200 (OK)" when {
      "the request received is valid" in new Test {
        MockCreateBenefitRequestParser
          .parse(rawData)
          .returns(Right(requestData))

        MockCreateStateBenefitService
          .createStateBenefit(requestData)
          .returns(Future.successful(Right(ResponseWrapper(correlationId, responseData))))

        MockHateoasFactory
          .wrap(responseData, CreateBenefitHateoasData(nino, taxYear, benefitId))
          .returns(HateoasWrapper(responseData, testHateoasLinks))

        runOkTestWithAudit(
          expectedStatus = OK,
          maybeAuditRequestBody = Some(requestBodyJson),
          maybeExpectedResponseBody = Some(responseJson),
          maybeAuditResponseBody = Some(responseJson)
        )
      }
    }

    "return the error as per spec" when {
      "the parser validation fails" in new Test {
        MockCreateBenefitRequestParser
          .parse(rawData)
          .returns(Left(ErrorWrapper(correlationId, NinoFormatError)))

        runErrorTestWithAudit(NinoFormatError, Some(requestBodyJson))
      }

      "the service returns an error" in new Test {
        MockCreateBenefitRequestParser
          .parse(rawData)
          .returns(Right(requestData))

        MockCreateStateBenefitService
          .createStateBenefit(requestData)
          .returns(Future.successful(Left(ErrorWrapper(correlationId, RuleTaxYearNotSupportedError))))

        runErrorTestWithAudit(RuleTaxYearNotSupportedError, maybeAuditRequestBody = Some(requestBodyJson))
      }
    }
  }

  trait Test extends ControllerTest with AuditEventChecking {

    val controller = new CreateBenefitController(
      authService = mockEnrolmentsAuthService,
      lookupService = mockMtdIdLookupService,
      appConfig = mockAppConfig,
      parser = mockCreateBenefitRequestParser,
      service = mockCreateStateBenefitService,
      auditService = mockAuditService,
      hateoasFactory = mockHateoasFactory,
      cc = cc,
      idGenerator = mockIdGenerator
    )

    MockedAppConfig.featureSwitches.returns(Configuration("allowTemporalValidationSuspension.enabled" -> true)).anyNumberOfTimes()

    protected def callController(): Future[Result] = controller.createStateBenefit(nino, taxYear)(fakePostRequest(requestBodyJson))

    def event(auditResponse: AuditResponse, requestBody: Option[JsValue]): AuditEvent[GenericAuditDetailOld] =
      AuditEvent(
        auditType = "CreateStateBenefit",
        transactionName = "create-state-benefit",
        detail = GenericAuditDetailOld(
          userType = "Individual",
          agentReferenceNumber = None,
          pathParams = Map("nino" -> nino, "taxYear" -> taxYear),
          queryParams = None,
          requestBody = Some(requestBodyJson),
          `X-CorrelationId` = correlationId,
          versionNumber = Version1.name,
          auditResponse = auditResponse
        )
      )

  }

}
