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

package v1.services

import api.controllers.EndpointLogContext
import api.models.domain.{BenefitType, Nino}
import api.models.errors._
import api.models.outcomes.ResponseWrapper
import api.services.ServiceSpec
import v1.mocks.connectors.MockCreateBenefitConnector
import v1.models.request.createBenefit.{CreateBenefitRequest, CreateBenefitRequestBody}
import v1.models.response.createBenefit.AddBenefitResponse

import scala.concurrent.Future

class CreateBenefitServiceSpec extends ServiceSpec {

  "AddBenefitService" when {
    "addBenefit" must {
      "return correct result for a success" in new Test {
        val outcome: Right[Nothing, ResponseWrapper[AddBenefitResponse]] = Right(ResponseWrapper(correlationId, response))

        MockAddBenefitConnector
          .addBenefit(request)
          .returns(Future.successful(outcome))

        val result: CreateBenefitServiceOutcome = await(service.addBenefit(request))
        result shouldBe outcome
      }

      "map errors according to spec" when {
        def serviceError(downstreamErrorCode: String, error: MtdError): Unit =
          s"a $downstreamErrorCode error is returned from the service" in new Test {

            MockAddBenefitConnector
              .addBenefit(request)
              .returns(Future.successful(Left(ResponseWrapper(correlationId, DownstreamErrors.single(DownstreamErrorCode(downstreamErrorCode))))))

            val result: CreateBenefitServiceOutcome = await(service.addBenefit(request))
            result shouldBe Left(ErrorWrapper(correlationId, error))
          }

        val errors = List(
          "INVALID_TAXABLE_ENTITY_ID"   -> NinoFormatError,
          "INVALID_TAX_YEAR"            -> TaxYearFormatError,
          "INVALID_CORRELATIONID"       -> StandardDownstreamError,
          "INVALID_PAYLOAD"             -> StandardDownstreamError,
          "BENEFIT_TYPE_ALREADY_EXISTS" -> RuleBenefitTypeExists,
          "NOT_SUPPORTED_TAX_YEAR"      -> RuleTaxYearNotEndedError,
          "INVALID_START_DATE"          -> RuleStartDateAfterTaxYearEndError,
          "INVALID_CESSATION_DATE"      -> RuleEndDateBeforeTaxYearStartError,
          "SERVER_ERROR"                -> StandardDownstreamError,
          "SERVICE_UNAVAILABLE"         -> StandardDownstreamError
        )

        errors.foreach(args => (serviceError _).tupled(args))
      }
    }
  }

  private trait Test extends MockCreateBenefitConnector {
    implicit val logContext: EndpointLogContext = EndpointLogContext("c", "ep")

    val nino    = "AA112233A"
    val taxYear = "2021-22"

    val addBenefitRequestBody: CreateBenefitRequestBody =
      CreateBenefitRequestBody(BenefitType.incapacityBenefit.toString, "2020-08-03", Some("2020-12-03"))

    val request: CreateBenefitRequest = CreateBenefitRequest(Nino(nino), taxYear, addBenefitRequestBody)

    val response: AddBenefitResponse = AddBenefitResponse("b1e8057e-fbbc-47a8-a8b4-78d9f015c253")

    val service: CreateBenefitService = new CreateBenefitService(
      connector = mockAddBenefitConnector
    )

  }

}
