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

package v2.listBenefits

import play.api.Configuration
import play.api.libs.json.JsObject
import play.api.mvc.Result
import shared.controllers.{ControllerBaseSpec, ControllerTestRunner}
import shared.models.domain.{Nino, TaxYear}
import shared.models.errors._
import shared.models.outcomes.ResponseWrapper
import shared.services.MockAuditService
import v2.fixtures.ListBenefitsFixture._
import v2.listBenefits.model.request.ListBenefitsRequestData
import v2.models.domain.BenefitId

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class ListBenefitsControllerSpec
    extends ControllerBaseSpec
    with ControllerTestRunner
    with MockListBenefitsService
    with MockListBenefitsValidatorFactory
    with MockAuditService {

  private val requestData = ListBenefitsRequestData(Nino(nino), TaxYear.fromMtd(taxYear), Some(BenefitId(benefitId)))

  "ListBenefitsController" should {
    "return OK" when {
      "happy path" in new Test {
        willUseValidator(returningSuccess(requestData))

        MockListBenefitsService
          .listBenefits(requestData)
          .returns(Future.successful(Right(ResponseWrapper(correlationId, responseData))))

        runOkTest(
          expectedStatus = OK,
          maybeExpectedResponseBody = Some(responseBody)
        )
      }
    }

    "return OK with no delete amount" when {
      "state benefits has no amount properties" in new Test {
        willUseValidator(returningSuccess(requestData))

        MockListBenefitsService
          .listBenefits(requestData)
          .returns(Future.successful(Right(ResponseWrapper(correlationId, responseDataWithNoAmounts))))

        runOkTest(
          expectedStatus = OK,
          maybeExpectedResponseBody = Some(responseBodyWithNoAmounts)
        )
      }
    }

    "return OK with only HMRC state benefit" when {
      "only HMRC state benefits returned" in new Test {
        willUseValidator(returningSuccess(requestData))

        MockListBenefitsService
          .listBenefits(requestData)
          .returns(Future.successful(Right(ResponseWrapper(correlationId, responseData.copy(customerAddedStateBenefits = None)))))

        runOkTest(
          expectedStatus = OK,
          maybeExpectedResponseBody = Some(responseBody.as[JsObject] - "customerAddedStateBenefits")
        )
      }
    }

    "return OK with only CUSTOM state benefit" when {
      "only CUSTOM state benefits returned" in new Test {
        willUseValidator(returningSuccess(requestData))

        MockListBenefitsService
          .listBenefits(requestData)
          .returns(Future.successful(Right(ResponseWrapper(correlationId, responseData.copy(stateBenefits = None)))))

        runOkTest(
          expectedStatus = OK,
          maybeExpectedResponseBody = Some(responseBody.as[JsObject] - "stateBenefits")
        )
      }
    }

    "return OK with single state benefit" when {
      "benefitId is passed for single retrieval" in new Test {
        willUseValidator(returningSuccess(requestData))

        MockListBenefitsService
          .listBenefits(requestData)
          .returns(Future.successful(Right(ResponseWrapper(correlationId, responseData.copy(stateBenefits = None)))))

        runOkTest(
          expectedStatus = OK,
          maybeExpectedResponseBody = Some(singleRetrieveWithAmounts)
        )
      }
    }

    "return the error as per spec" when {
      "the parser validation fails" in new Test {
        willUseValidator(returning(NinoFormatError))

        runErrorTest(NinoFormatError)
      }

      "the service returns an error" in new Test {
        willUseValidator(returningSuccess(requestData))

        MockListBenefitsService
          .listBenefits(requestData)
          .returns(Future.successful(Left(ErrorWrapper(correlationId, RuleTaxYearNotSupportedError))))

        runErrorTest(RuleTaxYearNotSupportedError)
      }
    }
  }

  private trait Test extends ControllerTest {

    val controller = new ListBenefitsController(
      authService = mockEnrolmentsAuthService,
      lookupService = mockMtdIdLookupService,
      validatorFactory = mockListBenefitsValidatorFactory,
      service = mockListBenefitsService,
      cc = cc,
      idGenerator = mockIdGenerator
    )

    MockedSharedAppConfig.featureSwitchConfig.anyNumberOfTimes() returns Configuration(
      "supporting-agents-access-control.enabled" -> true
    )

    MockedSharedAppConfig.endpointAllowsSupportingAgents(controller.endpointName).anyNumberOfTimes() returns false
    protected def callController(): Future[Result] = controller.listBenefits(nino, taxYear, Some(benefitId))(fakeGetRequest)

  }

}
