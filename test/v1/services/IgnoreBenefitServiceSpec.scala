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

import v1.controllers.EndpointLogContext
import v1.mocks.connectors.MockIgnoreBenefitConnector
import v1.models.domain.{Nino, TaxYear}
import v1.models.errors._
import v1.models.outcomes.ResponseWrapper
import v1.models.request.ignoreBenefit.IgnoreBenefitRequest

import scala.concurrent.Future

class IgnoreBenefitServiceSpec extends ServiceSpec {

  "IgnoreBenefitService" when {
    "ignoreBenefit" must {
      "return correct result for a success" in new Test {
        val outcome: Right[Nothing, ResponseWrapper[Unit]] = Right(ResponseWrapper(correlationId, ()))

        MockIgnoreBenefitConnector
          .ignoreBenefit(request)
          .returns(Future.successful(outcome))

        val result: Either[ErrorWrapper, ResponseWrapper[Unit]] = await(service.ignoreBenefit(request))

        result shouldBe outcome
      }

      "map errors according to spec" when {

        def serviceError(downstreamErrorCode: String, error: MtdError): Unit =
          s"a $downstreamErrorCode error is returned from the service" in new Test {

            MockIgnoreBenefitConnector
              .ignoreBenefit(request)
              .returns(Future.successful(Left(ResponseWrapper(correlationId, DownstreamErrors.single(DownstreamErrorCode(downstreamErrorCode))))))

            await(service.ignoreBenefit(request)) shouldBe Left(ErrorWrapper(correlationId, error))
          }

        val errors = List(
          ("INVALID_TAXABLE_ENTITY_ID", NinoFormatError),
          ("INVALID_TAX_YEAR", TaxYearFormatError),
          ("INVALID_BENEFIT_ID", BenefitIdFormatError),
          ("INVALID_CORRELATIONID", StandardDownstreamError),
          ("INVALID_PAYLOAD", StandardDownstreamError),
          ("IGNORE_FORBIDDEN", RuleIgnoreForbiddenError),
          ("NOT_SUPPORTED_TAX_YEAR", RuleTaxYearNotEndedError),
          ("NO_DATA_FOUND", NotFoundError),
          ("SERVER_ERROR", StandardDownstreamError),
          ("SERVICE_UNAVAILABLE", StandardDownstreamError)
        )

        val extraTysErrors = List(
          ("INVALID_CORRELATION_ID", StandardDownstreamError),
          ("TAX_YEAR_NOT_SUPPORTED", RuleTaxYearNotSupportedError)
        )

        (errors ++ extraTysErrors).foreach(args => (serviceError _).tupled(args))
      }
    }
  }

  trait Test extends MockIgnoreBenefitConnector {
    implicit val logContext: EndpointLogContext = EndpointLogContext("c", "ep")

    val nino: String = "AA111111A"
    val taxYear: String = "2019-20"
    val benefitId: String = "123e4567-e89b-12d3-a456-426614174000"

    val request: IgnoreBenefitRequest = IgnoreBenefitRequest(Nino(nino), TaxYear.fromMtd(taxYear), benefitId)

    val service: IgnoreBenefitService = new IgnoreBenefitService(
      connector = mockIgnoreBenefitConnector
    )
  }

}
