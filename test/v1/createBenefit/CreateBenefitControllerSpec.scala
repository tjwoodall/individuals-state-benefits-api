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

package v1.createBenefit

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
import v1.createBenefit.def1.model.request.{Def1_CreateBenefitRequestBody, Def1_CreateBenefitRequestData}
import v1.createBenefit.model.response.{CreateBenefitHateoasData, CreateBenefitResponse}
import v1.models.domain.BenefitType

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class CreateBenefitControllerSpec
    extends ControllerBaseSpec
    with ControllerTestRunner
    with MockSharedAppConfig
    with MockCreateBenefitService
    with MockAuditService
    with MockCreateBenefitValidatorFactory
    with MockHateoasFactory {

  private val taxYear   = "2019-20"
  private val benefitId = "b1e8057e-fbbc-47a8-a8b4-78d9f015c253"
  private val startDate = "2020-08-03"
  private val endDate   = "2020-12-03"

  val requestBodyJson: JsValue = Json.parse(
    s"""
       |{
       |  "benefitType": "incapacityBenefit",
       |  "startDate": "$startDate",
       |  "endDate" : "$endDate"
       |}
    """.stripMargin
  )

  val createStateBenefitRequestBody: Def1_CreateBenefitRequestBody = Def1_CreateBenefitRequestBody(
    startDate = "2019-01-01",
    endDate = Some("2020-06-01"),
    benefitType = BenefitType.incapacityBenefit.toString
  )

  val requestData: Def1_CreateBenefitRequestData = Def1_CreateBenefitRequestData(
    nino = parsedNino,
    taxYear = TaxYear.fromMtd(taxYear),
    body = createStateBenefitRequestBody
  )

  val responseData: CreateBenefitResponse = CreateBenefitResponse(benefitId)

  private val testHateoasLinks = List(
    Link(s"/individuals/state-benefits/$validNino/$taxYear?benefitId=$benefitId", GET, rel = "self"),
    Link(s"/individuals/state-benefits/$validNino/$taxYear/$benefitId", PUT, rel = "amend-state-benefit"),
    Link(s"/individuals/state-benefits/$validNino/$taxYear/$benefitId", DELETE, rel = "delete-state-benefit")
  )

  val responseJson: JsValue = Json.parse(
    s"""
       |{
       |   "benefitId": "$benefitId",
       |   "links": [
       |         {
       |         "href": "/individuals/state-benefits/$validNino/$taxYear?benefitId=$benefitId",
       |         "rel": "self",
       |         "method": "GET"
       |      },
       |      {
       |         "href": "/individuals/state-benefits/$validNino/$taxYear/$benefitId",
       |         "rel": "amend-state-benefit",
       |         "method": "PUT"
       |      },
       |      {
       |         "href": "/individuals/state-benefits/$validNino/$taxYear/$benefitId",
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
        willUseValidator(returningSuccess(requestData))

        MockCreateStateBenefitService
          .createStateBenefit(requestData)
          .returns(Future.successful(Right(ResponseWrapper(correlationId, responseData))))

        MockHateoasFactory
          .wrap(responseData, CreateBenefitHateoasData(validNino, taxYear, benefitId))
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
        willUseValidator(returning(NinoFormatError))

        runErrorTestWithAudit(NinoFormatError, Some(requestBodyJson))
      }

      "the service returns an error" in new Test {
        willUseValidator(returningSuccess(requestData))

        MockCreateStateBenefitService
          .createStateBenefit(requestData)
          .returns(Future.successful(Left(ErrorWrapper(correlationId, RuleTaxYearNotSupportedError))))

        runErrorTestWithAudit(RuleTaxYearNotSupportedError, maybeAuditRequestBody = Some(requestBodyJson))
      }
    }
  }

  private trait Test extends ControllerTest with AuditEventChecking[GenericAuditDetail] {

    val controller: CreateBenefitController = new CreateBenefitController(
      authService = mockEnrolmentsAuthService,
      lookupService = mockMtdIdLookupService,
      validatorFactory = mockCreateBenefitValidatorFactory,
      service = mockCreateStateBenefitService,
      auditService = mockAuditService,
      hateoasFactory = mockHateoasFactory,
      cc = cc,
      idGenerator = mockIdGenerator
    )

    MockedSharedAppConfig.featureSwitchConfig.anyNumberOfTimes() returns Configuration(
      "supporting-agents-access-control.enabled" -> true
    )

    MockedSharedAppConfig.endpointAllowsSupportingAgents(controller.endpointName).anyNumberOfTimes() returns false

    protected def callController(): Future[Result] = controller.createStateBenefit(validNino, taxYear)(fakePostRequest(requestBodyJson))

    def event(auditResponse: AuditResponse, requestBody: Option[JsValue]): AuditEvent[GenericAuditDetail] =
      AuditEvent(
        auditType = "CreateStateBenefit",
        transactionName = "create-state-benefit",
        detail = GenericAuditDetail(
          userType = "Individual",
          agentReferenceNumber = None,
          params = Map("nino" -> validNino, "taxYear" -> taxYear),
          requestBody = Some(requestBodyJson),
          `X-CorrelationId` = correlationId,
          versionNumber = apiVersion.name,
          auditResponse = auditResponse
        )
      )

  }

}
