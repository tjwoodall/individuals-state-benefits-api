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
import api.hateoas.HateoasLinks
import api.mocks.hateoas.MockHateoasFactory
import api.mocks.services.MockAuditService
import api.models.domain.{Nino, TaxYear}
import api.models.errors._
import api.models.outcomes.ResponseWrapper
import play.api.libs.json.JsObject
import play.api.mvc.Result
import v1.controllers.validators.MockListBenefitsValidatorFactory
import v1.fixtures.ListBenefitsFixture._
import v1.models.domain.BenefitId
import v1.models.request.listBenefits.ListBenefitsRequestData
import v1.models.response.listBenefits.{CustomerStateBenefit, HMRCStateBenefit, ListBenefitsHateoasData}
import v1.services.MockListBenefitsService

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class ListBenefitsControllerSpec
    extends ControllerBaseSpec
    with ControllerTestRunner
    with MockListBenefitsService
    with MockListBenefitsValidatorFactory
    with MockHateoasFactory
    with MockAuditService
    with HateoasLinks {

  private val requestData = ListBenefitsRequestData(Nino(nino), TaxYear.fromMtd(taxYear), Some(BenefitId(benefitId)))

  "ListBenefitsController" should {
    "return OK with full HATEOAS" when {
      "happy path" in new Test {
        willUseValidator(returningSuccess(requestData))

        MockListBenefitsService
          .listBenefits(requestData)
          .returns(Future.successful(Right(ResponseWrapper(correlationId, responseData))))

        MockHateoasFactory
          .wrapList(responseData, ListBenefitsHateoasData(nino, taxYear, queryIsFiltered = true, hmrcBenefitIds = List(benefitId)))
          .returns(hateoasResponse)

        runOkTest(
          expectedStatus = OK,
          maybeExpectedResponseBody = Some(responseBody)
        )
      }
    }

    "return OK with no delete amount HATEOAS" when {
      "state benefits has no amount properties" in new Test {
        willUseValidator(returningSuccess(requestData))

        MockListBenefitsService
          .listBenefits(requestData)
          .returns(Future.successful(Right(ResponseWrapper(correlationId, responseDataWithNoAmounts))))

        MockHateoasFactory
          .wrapList(responseDataWithNoAmounts, ListBenefitsHateoasData(nino, taxYear, queryIsFiltered = true, hmrcBenefitIds = List(benefitId)))
          .returns(hateoasResponseWithOutAmounts)

        runOkTest(
          expectedStatus = OK,
          maybeExpectedResponseBody = Some(responseBodyWithNoAmounts)
        )
      }
    }

    "return OK with only HMRC state benefit HATEOAS" when {
      "only HMRC state benefits returned" in new Test {
        willUseValidator(returningSuccess(requestData))

        MockListBenefitsService
          .listBenefits(requestData)
          .returns(Future.successful(Right(ResponseWrapper(correlationId, responseData.copy(customerAddedStateBenefits = None)))))

        MockHateoasFactory
          .wrapList(
            responseData.copy(customerAddedStateBenefits = Option.empty[Seq[CustomerStateBenefit]]),
            ListBenefitsHateoasData(nino, taxYear, queryIsFiltered = true, hmrcBenefitIds = List(benefitId))
          )
          .returns(hmrcOnlyHateoasResponse)

        runOkTest(
          expectedStatus = OK,
          maybeExpectedResponseBody = Some(responseBody.as[JsObject] - "customerAddedStateBenefits")
        )
      }
    }

    "return OK with only CUSTOM state benefit HATEOAS" when {
      "only CUSTOM state benefits returned" in new Test {
        willUseValidator(returningSuccess(requestData))

        MockListBenefitsService
          .listBenefits(requestData)
          .returns(Future.successful(Right(ResponseWrapper(correlationId, responseData.copy(stateBenefits = None)))))

        MockHateoasFactory
          .wrapList(
            responseData.copy(stateBenefits = Option.empty[Seq[HMRCStateBenefit]]),
            ListBenefitsHateoasData(nino, taxYear, queryIsFiltered = true, hmrcBenefitIds = Nil))
          .returns(customOnlyHateoasResponse)

        runOkTest(
          expectedStatus = OK,
          maybeExpectedResponseBody = Some(responseBody.as[JsObject] - "stateBenefits")
        )
      }
    }

    "return OK with single state benefit with HATEOAS" when {
      "benefitId is passed for single retrieval" in new Test {
        willUseValidator(returningSuccess(requestData))

        MockListBenefitsService
          .listBenefits(requestData)
          .returns(Future.successful(Right(ResponseWrapper(correlationId, responseData.copy(stateBenefits = None)))))

        MockHateoasFactory
          .wrapList(
            responseData.copy(stateBenefits = Option.empty[Seq[HMRCStateBenefit]]),
            ListBenefitsHateoasData(nino, taxYear, queryIsFiltered = true, hmrcBenefitIds = Nil))
          .returns(singleCustomOnlyHateoasResponse)

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

    private val controller = new ListBenefitsController(
      authService = mockEnrolmentsAuthService,
      lookupService = mockMtdIdLookupService,
      validatorFactory = mockListBenefitsValidatorFactory,
      service = mockListBenefitsService,
      hateoasFactory = mockHateoasFactory,
      cc = cc,
      idGenerator = mockIdGenerator
    )

    protected def callController(): Future[Result] = controller.listBenefits(nino, taxYear, Some(benefitId))(fakeGetRequest)

  }

}
