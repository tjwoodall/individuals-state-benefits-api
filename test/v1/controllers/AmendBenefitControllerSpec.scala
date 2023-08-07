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
import api.mocks.hateoas.MockHateoasFactory
import api.mocks.services.MockAuditService
import api.models.audit.{AuditEvent, AuditResponse, GenericAuditDetail}
import api.models.domain.Nino
import api.models.errors._
import api.models.hateoas
import api.models.hateoas.HateoasWrapper
import api.models.hateoas.Method.{DELETE, GET, PUT}
import api.models.outcomes.ResponseWrapper
import mocks.MockAppConfig
import play.api.Configuration
import play.api.libs.json.{JsValue, Json}
import play.api.mvc.{AnyContentAsJson, Result}
import routing.Version1
import v1.mocks.requestParsers.MockAmendBenefitRequestParser
import v1.mocks.services.MockAmendBenefitService
import v1.models.request.AmendBenefit.{AmendBenefitRawData, AmendBenefitRequest, AmendBenefitRequestBody}
import v1.models.response.amendBenefit.AmendBenefitHateoasData

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class AmendBenefitControllerSpec
    extends ControllerBaseSpec
    with ControllerTestRunner
    with MockAppConfig
    with MockAmendBenefitService
    with MockAmendBenefitRequestParser
    with MockAuditService
    with MockHateoasFactory {

  private val taxYear: String = "2020-21"
  private val benefitId       = "4557ecb5-fd32-48cc-81f5-e6acd1099f3c"

  val requestBodyJson: JsValue = Json.parse(
    """
      |{
      |   "startDate": "2020-04-06",
      |   "endDate": "2021-01-01"
      |}
    """.stripMargin
  )

  val rawData: AmendBenefitRawData = AmendBenefitRawData(
    nino = nino,
    taxYear = taxYear,
    benefitId = benefitId,
    body = AnyContentAsJson(requestBodyJson)
  )

  val requestBody: AmendBenefitRequestBody = AmendBenefitRequestBody(
    startDate = "2020-04-06",
    endDate = Some("2021-01-01")
  )

  val requestData: AmendBenefitRequest = AmendBenefitRequest(
    nino = Nino(nino),
    taxYear = taxYear,
    benefitId = benefitId,
    body = requestBody
  )

  private val testHateoasLinks = Seq(
    hateoas.Link(href = s"/individuals/state-benefits/$nino/$taxYear/$benefitId", method = PUT, rel = "amend-state-benefit"),
    hateoas.Link(href = s"/individuals/state-benefits/$nino/$taxYear?benefitId=$benefitId", method = GET, rel = "self"),
    hateoas.Link(href = s"/individuals/state-benefits/$nino/$taxYear/$benefitId", method = DELETE, rel = "delete-state-benefit"),
    hateoas.Link(href = s"/individuals/state-benefits/$nino/$taxYear/$benefitId/amounts", method = PUT, rel = "amend-state-benefit-amounts")
  )

  val responseJson: JsValue = Json.parse(
    s"""
       |{
       |  "links": [
       |    {
       |      "href": "/individuals/state-benefits/$nino/$taxYear/$benefitId",
       |      "method": "PUT",
       |      "rel": "amend-state-benefit"
       |    },
       |    {
       |      "href": "/individuals/state-benefits/$nino/$taxYear?benefitId=$benefitId",
       |      "method": "GET",
       |      "rel": "self"
       |    },
       |    {
       |      "href": "/individuals/state-benefits/$nino/$taxYear/$benefitId",
       |      "method": "DELETE",
       |      "rel": "delete-state-benefit"
       |    },
       |    {
       |      "href": "/individuals/state-benefits/$nino/$taxYear/$benefitId/amounts",
       |      "method": "PUT",
       |      "rel": "amend-state-benefit-amounts"
       |    }
       |  ]
       |}
    """.stripMargin
  )

  "AmendBenefitController" should {
    "return a successful response with status 200 (OK)" when {
      "the request received is valid" in new Test {
        MockAmendBenefitRequestParser
          .parse(rawData)
          .returns(Right(requestData))

        MockAmendBenefitService
          .amendBenefit(requestData)
          .returns(Future.successful(Right(ResponseWrapper(correlationId, ()))))

        MockHateoasFactory
          .wrap((), AmendBenefitHateoasData(nino, taxYear, benefitId))
          .returns(HateoasWrapper((), testHateoasLinks))

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
        MockAmendBenefitRequestParser
          .parse(rawData)
          .returns(Left(ErrorWrapper(correlationId, NinoFormatError)))

        runErrorTestWithAudit(NinoFormatError, Some(requestBodyJson))
      }

      "service returns an error" in new Test {
        MockAmendBenefitRequestParser
          .parse(rawData)
          .returns(Right(requestData))

        MockAmendBenefitService
          .amendBenefit(requestData)
          .returns(Future.successful(Left(ErrorWrapper(correlationId, RuleTaxYearNotSupportedError))))

        runErrorTestWithAudit(RuleTaxYearNotSupportedError, maybeAuditRequestBody = Some(requestBodyJson))
      }
    }
  }

  trait Test extends ControllerTest with AuditEventChecking {

    val controller = new AmendBenefitController(
      authService = mockEnrolmentsAuthService,
      lookupService = mockMtdIdLookupService,
      appConfig = mockAppConfig,
      parser = mockAmendBenefitRequestParser,
      service = mockAmendBenefitService,
      hateoasFactory = mockHateoasFactory,
      auditService = mockAuditService,
      cc = cc,
      idGenerator = mockIdGenerator
    )

    MockedAppConfig.featureSwitches.returns(Configuration("allowTemporalValidationSuspension.enabled" -> true)).anyNumberOfTimes()

    protected def callController(): Future[Result] = controller.amendBenefit(nino, taxYear, benefitId)(fakePutRequest(requestBodyJson))

    def event(auditResponse: AuditResponse, requestBody: Option[JsValue]): AuditEvent[GenericAuditDetail] =
      AuditEvent(
        auditType = "AmendStateBenefit",
        transactionName = "amend-state-benefit",
        detail = GenericAuditDetail(
          userType = "Individual",
          agentReferenceNumber = None,
          pathParams = Map("nino" -> nino, "taxYear" -> taxYear, "benefitId" -> benefitId),
          queryParams = None,
          requestBody = Some(requestBodyJson),
          `X-CorrelationId` = correlationId,
          versionNumber = Version1.name,
          auditResponse = auditResponse
        )
      )

  }

}
