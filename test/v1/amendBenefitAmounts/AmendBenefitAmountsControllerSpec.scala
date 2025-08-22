/*
 * Copyright 2025 HM Revenue & Customs
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

package v1.amendBenefitAmounts

import play.api.Configuration
import play.api.libs.json.{JsValue, Json}
import play.api.mvc.Result
import shared.config.MockSharedAppConfig
import shared.controllers.{ControllerBaseSpec, ControllerTestRunner}
import shared.hateoas.Method.{DELETE, GET, PUT}
import shared.hateoas.{HateoasWrapper, Link, MockHateoasFactory}
import shared.models.audit.{AuditEvent, AuditResponse, GenericAuditDetail}
import shared.models.domain.TaxYear
import shared.models.errors.*
import shared.models.outcomes.ResponseWrapper
import shared.services.MockAuditService
import v1.amendBenefitAmounts.def1.model.request.{Def1_AmendBenefitAmountsRequestBody, Def1_AmendBenefitAmountsRequestData}
import v1.amendBenefitAmounts.model.response.AmendBenefitAmountsHateoasData
import v1.models.domain.BenefitId

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class AmendBenefitAmountsControllerSpec
    extends ControllerBaseSpec
    with ControllerTestRunner
    with MockSharedAppConfig
    with MockAmendBenefitAmountsService
    with MockAmendBenefitAmountsValidatorFactory
    with MockHateoasFactory
    with MockAuditService {

  private val taxYear   = "2019-20"
  private val benefitId = "b1e8057e-fbbc-47a8-a8b4-78d9f015c253"

  private val requestBodyJson = Json.parse(
    """
      |{
      |  "amount": 2050.45,
      |  "taxPaid": 1095.55
      |}
    """.stripMargin
  )

  private val amendBenefitAmountsRequestBody = Def1_AmendBenefitAmountsRequestBody(2050.45, Some(1095.55))

  private val requestData =
    Def1_AmendBenefitAmountsRequestData(parsedNino, TaxYear.fromMtd(taxYear), BenefitId(benefitId), amendBenefitAmountsRequestBody)

  private val testHateoasLinks = List(
    Link(s"/individuals/state-benefits/$validNino/$taxYear?benefitId=$benefitId", GET, "self"),
    Link(s"/individuals/state-benefits/$validNino/$taxYear/$benefitId/amounts", PUT, "amend-state-benefit-amounts"),
    Link(s"/individuals/state-benefits/$validNino/$taxYear/$benefitId/amounts", DELETE, "delete-state-benefit-amounts")
  )

  private val hateoasResponse = Json.parse(
    s"""
       |{
       |   "links":[
       |      {
       |         "href":"/individuals/state-benefits/$validNino/$taxYear?benefitId=$benefitId",
       |         "rel":"self",
       |         "method":"GET"
       |      },
       |      {
       |         "href": "/individuals/state-benefits/$validNino/$taxYear/$benefitId/amounts",
       |         "method": "PUT",
       |         "rel": "amend-state-benefit-amounts"
       |      },
       |      {
       |         "href": "/individuals/state-benefits/$validNino/$taxYear/$benefitId/amounts",
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
        willUseValidator(returningSuccess(requestData))

        MockAmendBenefitAmountsService
          .amendBenefitAmounts(requestData)
          .returns(Future.successful(Right(ResponseWrapper(correlationId, ()))))

        MockHateoasFactory
          .wrap((), AmendBenefitAmountsHateoasData(validNino, taxYear, benefitId))
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
        willUseValidator(returning(NinoFormatError))

        runErrorTestWithAudit(NinoFormatError, Some(requestBodyJson))
      }

      "the service returns an error" in new Test {
        willUseValidator(returningSuccess(requestData))

        MockAmendBenefitAmountsService
          .amendBenefitAmounts(requestData)
          .returns(Future.successful(Left(ErrorWrapper(correlationId, RuleTaxYearNotSupportedError))))

        runErrorTestWithAudit(RuleTaxYearNotSupportedError, Some(requestBodyJson))
      }
    }
  }

  private trait Test extends ControllerTest with AuditEventChecking[GenericAuditDetail] {

    val controller: AmendBenefitAmountsController = new AmendBenefitAmountsController(
      authService = mockEnrolmentsAuthService,
      lookupService = mockMtdIdLookupService,
      validatorFactory = mockAmendBenefitAmountsValidatorFactory,
      service = mockAmendBenefitAmountsService,
      hateoasFactory = mockHateoasFactory,
      auditService = mockAuditService,
      cc = cc,
      idGenerator = mockIdGenerator
    )

    MockedSharedAppConfig.featureSwitchConfig.anyNumberOfTimes() returns Configuration(
      "supporting-agents-access-control.enabled" -> true
    )

    MockedSharedAppConfig.endpointAllowsSupportingAgents(controller.endpointName).anyNumberOfTimes() returns false

    protected def callController(): Future[Result] =
      controller.amendBenefitAmounts(validNino, taxYear, benefitId)(fakeRequest.withBody(requestBodyJson))

    def event(auditResponse: AuditResponse, maybeRequestBody: Option[JsValue]): AuditEvent[GenericAuditDetail] =
      AuditEvent(
        auditType = "AmendStateBenefitAmounts",
        transactionName = "amend-state-benefit-amounts",
        detail = GenericAuditDetail(
          versionNumber = apiVersion.name,
          userType = "Individual",
          agentReferenceNumber = None,
          params = Map("nino" -> validNino, "taxYear" -> taxYear, "benefitId" -> benefitId),
          requestBody = maybeRequestBody,
          `X-CorrelationId` = correlationId,
          auditResponse = auditResponse
        )
      )

  }

}
