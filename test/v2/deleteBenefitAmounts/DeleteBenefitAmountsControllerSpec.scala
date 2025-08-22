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

package v2.deleteBenefitAmounts

import play.api.Configuration
import play.api.libs.json.JsValue
import play.api.mvc.Result
import shared.controllers.{ControllerBaseSpec, ControllerTestRunner}
import shared.models.audit.{AuditEvent, AuditResponse, GenericAuditDetail}
import shared.models.domain.TaxYear
import shared.models.errors.*
import shared.models.outcomes.ResponseWrapper
import shared.services.MockAuditService
import v2.deleteBenefitAmounts.def1.model.request.Def1_DeleteBenefitAmountsRequestData
import v2.models.domain.BenefitId

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class DeleteBenefitAmountsControllerSpec
    extends ControllerBaseSpec
    with ControllerTestRunner
    with MockAuditService
    with MockDeleteBenefitAmountsService
    with MockDeleteBenefitAmountsValidatorFactory {

  private val taxYear   = "2019-20"
  private val benefitId = "b1e8057e-fbbc-47a8-a8b4-78d9f015c253"

  private val requestData = Def1_DeleteBenefitAmountsRequestData(parsedNino, TaxYear.fromMtd(taxYear), BenefitId(benefitId))

  "DeleteBenefitAmountsController" should {
    "return a successful response with status 204 (No Content)" when {
      "happy path" in new Test {
        willUseValidator(returningSuccess(requestData))

        MockDeleteBenefitAmountsService
          .deleteBenefitAmounts(requestData)
          .returns(Future.successful(Right(ResponseWrapper(correlationId, ()))))

        runOkTestWithAudit(expectedStatus = NO_CONTENT)
      }
    }

    "return the error as per spec" when {
      "the parser validation fails" in new Test {
        willUseValidator(returning(NinoFormatError))

        runErrorTestWithAudit(NinoFormatError)
      }

      "service returns an error" in new Test {
        willUseValidator(returningSuccess(requestData))

        MockDeleteBenefitAmountsService
          .deleteBenefitAmounts(requestData)
          .returns(Future.successful(Left(ErrorWrapper(correlationId, RuleTaxYearNotSupportedError))))

        runErrorTestWithAudit(RuleTaxYearNotSupportedError)
      }
    }
  }

  private trait Test extends ControllerTest with AuditEventChecking[GenericAuditDetail] {

    val controller: DeleteBenefitAmountsController = new DeleteBenefitAmountsController(
      authService = mockEnrolmentsAuthService,
      lookupService = mockMtdIdLookupService,
      validatorFactory = mockDeleteBenefitAmountsValidatorFactory,
      service = mockDeleteBenefitAmountsService,
      auditService = mockAuditService,
      cc = cc,
      idGenerator = mockIdGenerator
    )

    MockedSharedAppConfig.featureSwitchConfig.anyNumberOfTimes() returns Configuration(
      "supporting-agents-access-control.enabled" -> true
    )

    MockedSharedAppConfig.endpointAllowsSupportingAgents(controller.endpointName).anyNumberOfTimes() returns false

    protected def callController(): Future[Result] = controller.deleteBenefitAmounts(validNino, taxYear, benefitId)(fakeRequest)

    def event(auditResponse: AuditResponse, maybeRequestBody: Option[JsValue]): AuditEvent[GenericAuditDetail] =
      AuditEvent(
        auditType = "DeleteStateBenefitAmounts",
        transactionName = "delete-state-benefit-amounts",
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
