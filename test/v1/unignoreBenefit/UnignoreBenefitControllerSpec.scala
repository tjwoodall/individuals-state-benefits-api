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

package v1.unignoreBenefit

import play.api.Configuration
import play.api.libs.json.{JsValue, Json}
import play.api.mvc.Result
import shared.controllers.{ControllerBaseSpec, ControllerTestRunner}
import shared.hateoas.Method.{GET, POST}
import shared.hateoas.{HateoasWrapper, Link, MockHateoasFactory}
import shared.models.audit.{AuditEvent, AuditResponse, GenericAuditDetail}
import shared.models.domain.TaxYear
import shared.models.errors.*
import shared.models.outcomes.ResponseWrapper
import shared.services.MockAuditService
import v1.models.domain.BenefitId
import v1.unignoreBenefit.def1.model.request.Def1_UnignoreBenefitRequestData
import v1.unignoreBenefit.model.response.UnignoreBenefitHateoasData

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class UnignoreBenefitControllerSpec
    extends ControllerBaseSpec
    with ControllerTestRunner
    with MockUnignoreBenefitService
    with MockUnignoreBenefitValidatorFactory
    with MockAuditService
    with MockHateoasFactory {

  private val taxYear     = "2019-20"
  private val benefitId   = "b1e8057e-fbbc-47a8-a8b4-78d9f015c253"
  private val requestData = Def1_UnignoreBenefitRequestData(parsedNino, TaxYear.fromMtd(taxYear), BenefitId(benefitId))

  "UnignoreBenefitController" should {
    "return a successful response with status 200 (OK)" when {
      "happy path" in new Test {
        willUseValidator(returningSuccess(requestData))

        MockUnignoreBenefitService
          .unignoreBenefit(requestData)
          .returns(Future.successful(Right(ResponseWrapper(correlationId, ()))))

        MockHateoasFactory
          .wrap((), UnignoreBenefitHateoasData(validNino, taxYear, benefitId))
          .returns(HateoasWrapper((), testHateoasLinks))

        runOkTestWithAudit(
          expectedStatus = OK,
          maybeAuditRequestBody = None,
          maybeExpectedResponseBody = Some(hateoasResponse),
          maybeAuditResponseBody = Some(hateoasResponse)
        )
      }
    }

    "return the error as per spec" when {
      "the parser validation fails" in new Test {
        willUseValidator(returning(NinoFormatError))

        runErrorTestWithAudit(NinoFormatError, None)
      }

      "the service returns an error" in new Test {
        willUseValidator(returningSuccess(requestData))

        MockUnignoreBenefitService
          .unignoreBenefit(requestData)
          .returns(Future.successful(Left(ErrorWrapper(correlationId, RuleTaxYearNotSupportedError))))

        runErrorTestWithAudit(RuleTaxYearNotSupportedError, maybeAuditRequestBody = None)
      }
    }
  }

  private trait Test extends ControllerTest with AuditEventChecking[GenericAuditDetail] {

    val controller: UnignoreBenefitController = new UnignoreBenefitController(
      authService = mockEnrolmentsAuthService,
      lookupService = mockMtdIdLookupService,
      validatorFactory = mockUnignoreBenefitValidatorFactory,
      service = mockUnignoreBenefitService,
      auditService = mockAuditService,
      hateoasFactory = mockHateoasFactory,
      cc = cc,
      idGenerator = mockIdGenerator
    )

    MockedSharedAppConfig.featureSwitchConfig.anyNumberOfTimes() returns Configuration(
      "supporting-agents-access-control.enabled" -> true
    )

    MockedSharedAppConfig.endpointAllowsSupportingAgents(controller.endpointName).anyNumberOfTimes() returns false

    protected def callController(): Future[Result] = controller.unignoreBenefit(validNino, taxYear, benefitId)(fakeRequest)

    protected def event(auditResponse: AuditResponse, requestBody: Option[JsValue]): AuditEvent[GenericAuditDetail] =
      AuditEvent(
        auditType = "UnignoreStateBenefit",
        transactionName = "unignore-state-benefit",
        detail = GenericAuditDetail(
          userType = "Individual",
          agentReferenceNumber = None,
          params = Map("nino" -> validNino, "taxYear" -> taxYear, "benefitId" -> benefitId),
          requestBody = None,
          `X-CorrelationId` = correlationId,
          versionNumber = apiVersion.name,
          auditResponse = auditResponse
        )
      )

    val testHateoasLinks: Seq[Link] = List(
      Link(s"/individuals/state-benefits/$validNino/$taxYear?benefitId=$benefitId", GET, "self"),
      Link(s"/individuals/state-benefits/$validNino/$taxYear/$benefitId/ignore", POST, "ignore-state-benefit")
    )

    val hateoasResponse: JsValue = Json.parse(
      s"""
         |{
         |   "links":[
         |      {
         |         "href":"/individuals/state-benefits/$validNino/$taxYear?benefitId=$benefitId",
         |         "rel":"self",
         |         "method":"GET"
         |      },
         |      {
         |         "href":"/individuals/state-benefits/$validNino/$taxYear/$benefitId/ignore",
         |         "rel":"ignore-state-benefit",
         |         "method":"POST"
         |      }
         |   ]
         |}
    """.stripMargin
    )

  }

}
