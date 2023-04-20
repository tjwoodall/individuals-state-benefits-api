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
import api.models.domain.{Nino, TaxYear}
import api.models.errors._
import api.models.hateoas
import api.models.hateoas.HateoasWrapper
import api.models.hateoas.Method.{DELETE, GET, PUT}
import api.models.outcomes.ResponseWrapper
import mocks.MockAppConfig
import play.api.libs.json.{JsValue, Json}
import play.api.mvc.{AnyContentAsJson, Result}
import v1.mocks.requestParsers.MockAmendBenefitAmountsRequestParser
import v1.mocks.services.MockAmendBenefitAmountsService
import v1.models.request.AmendBenefitAmounts.{AmendBenefitAmountsRawData, AmendBenefitAmountsRequest, AmendBenefitAmountsRequestBody}
import v1.models.response.amendBenefitAmounts.AmendBenefitAmountsHateoasData

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class AmendBenefitAmountsControllerSpec
    extends ControllerBaseSpec
    with ControllerTestRunner
    with MockAppConfig
    with MockAmendBenefitAmountsService
    with MockAmendBenefitAmountsRequestParser
    with MockHateoasFactory
    with MockAuditService {

  val taxYear: String   = "2019-20"
  val benefitId: String = "b1e8057e-fbbc-47a8-a8b4-78d9f015c253"

  val requestBodyJson: JsValue = Json.parse(
    """
      |{
      |  "amount": 2050.45,
      |  "taxPaid": 1095.55
      |}
    """.stripMargin
  )

  val rawData: AmendBenefitAmountsRawData = AmendBenefitAmountsRawData(
    nino = nino,
    taxYear = taxYear,
    benefitId = benefitId,
    body = AnyContentAsJson(requestBodyJson)
  )

  val amendBenefitAmountsRequestBody: AmendBenefitAmountsRequestBody = AmendBenefitAmountsRequestBody(
    amount = 2050.45,
    taxPaid = Some(1095.55)
  )

  val requestData: AmendBenefitAmountsRequest = AmendBenefitAmountsRequest(
    nino = Nino(nino),
    taxYear = TaxYear.fromMtd(taxYear),
    benefitId = benefitId,
    body = amendBenefitAmountsRequestBody
  )

  private val testHateoasLinks = Seq(
    hateoas.Link(href = s"/individuals/state-benefits/$nino/$taxYear?benefitId=$benefitId", method = GET, rel = "self"),
    hateoas.Link(href = s"/individuals/state-benefits/$nino/$taxYear/$benefitId/amounts", method = PUT, rel = "amend-state-benefit-amounts"),
    hateoas.Link(href = s"/individuals/state-benefits/$nino/$taxYear/$benefitId/amounts", method = DELETE, rel = "delete-state-benefit-amounts")
  )

  val hateoasResponse: JsValue = Json.parse(
    s"""
       |{
       |   "links":[
       |      {
       |         "href":"/individuals/state-benefits/$nino/$taxYear?benefitId=$benefitId",
       |         "rel":"self",
       |         "method":"GET"
       |      },
       |      {
       |         "href": "/individuals/state-benefits/$nino/$taxYear/$benefitId/amounts",
       |         "method": "PUT",
       |         "rel": "amend-state-benefit-amounts"
       |      },
       |      {
       |         "href": "/individuals/state-benefits/$nino/$taxYear/$benefitId/amounts",
       |         "method": "DELETE",
       |         "rel": "delete-state-benefit-amounts"
       |      }
       |   ]
       |}
    """.stripMargin
  )

  "AmendBenefitAmountsController" should {
    "return a successful response with status 200 (OK)" when {
      "the request received is valid" in new Test {
        MockAmendBenefitAmountsRequestParser
          .parse(rawData)
          .returns(Right(requestData))

        MockAmendBenefitAmountsService
          .amendBenefitAmounts(requestData)
          .returns(Future.successful(Right(ResponseWrapper(correlationId, ()))))

        MockHateoasFactory
          .wrap((), AmendBenefitAmountsHateoasData(nino, taxYear, benefitId))
          .returns(HateoasWrapper((), testHateoasLinks))

        runOkTestWithAudit(
          expectedStatus = OK,
          maybeAuditRequestBody = Some(requestBodyJson),
          maybeExpectedResponseBody = Some(hateoasResponse),
          maybeAuditResponseBody = Some(hateoasResponse)
        )
      }
    }

    "return the error as per spec" when {
      "the parser validation fails" in new Test {

        MockAmendBenefitAmountsRequestParser
          .parse(rawData)
          .returns(Left(ErrorWrapper(correlationId, NinoFormatError)))

        runErrorTestWithAudit(NinoFormatError, Some(requestBodyJson))
      }

      "the service returns an error" in new Test {
        MockAmendBenefitAmountsRequestParser
          .parse(rawData)
          .returns(Right(requestData))

        MockAmendBenefitAmountsService
          .amendBenefitAmounts(requestData)
          .returns(Future.successful(Left(ErrorWrapper(correlationId, RuleTaxYearNotSupportedError))))

        runErrorTestWithAudit(RuleTaxYearNotSupportedError, Some(requestBodyJson))
      }
    }
  }

  trait Test extends ControllerTest with AuditEventChecking {

    val controller = new AmendBenefitAmountsController(
      authService = mockEnrolmentsAuthService,
      lookupService = mockMtdIdLookupService,
      appConfig = mockAppConfig,
      parser = mockAmendBenefitAmountsRequestParser,
      service = mockAmendBenefitAmountsService,
      hateoasFactory = mockHateoasFactory,
      auditService = mockAuditService,
      cc = cc,
      idGenerator = mockIdGenerator
    )

    protected def callController(): Future[Result] = controller.amendBenefitAmounts(nino, taxYear, benefitId)(fakePutRequest(requestBodyJson))

    def event(auditResponse: AuditResponse, requestBody: Option[JsValue]): AuditEvent[GenericAuditDetail] =
      AuditEvent(
        auditType = "AmendStateBenefitAmounts",
        transactionName = "amend-state-benefit-amounts",
        detail = GenericAuditDetail(
          userType = "Individual",
          agentReferenceNumber = None,
          pathParams = Map("nino" -> nino, "taxYear" -> taxYear, "benefitId" -> benefitId),
          queryParams = None,
          requestBody = Some(requestBodyJson),
          `X-CorrelationId` = correlationId,
          auditResponse = auditResponse
        )
      )

  }

}
