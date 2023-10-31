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
import api.models.domain.{Nino, TaxYear}
import api.models.errors._
import api.models.outcomes.ResponseWrapper
import api.services.ServiceSpec
import v1.connectors.MockAmendBenefitAmountsConnector
import v1.models.domain.BenefitId
import v1.models.request.amendBenefitAmounts.{AmendBenefitAmountsRequestData, AmendBenefitAmountsRequestBody}

import scala.concurrent.Future

class AmendBenefitAmountsServiceSpec extends ServiceSpec {

  "AmendBenefitAmountsService" when {
    "AmendBenefitAmounts" must {
      "return correct result for a success" in new Test {
        val expectedOutcome: Right[Nothing, ResponseWrapper[Unit]] = Right(ResponseWrapper(correlationId, ()))

        MockAmendBenefitAmountsConnector
          .amendBenefitAmounts(requestData)
          .returns(Future.successful(expectedOutcome))

        val result: Either[ErrorWrapper, ResponseWrapper[Unit]] = await(service.amendBenefitAmounts(requestData))
        result shouldBe expectedOutcome
      }
    }

    "map errors according to spec" when {
      def serviceError(downstreamErrorCode: String, error: MtdError): Unit =
        s"a $downstreamErrorCode error is returned from the service" in new Test {

          MockAmendBenefitAmountsConnector
            .amendBenefitAmounts(requestData)
            .returns(Future.successful(Left(ResponseWrapper(correlationId, DownstreamErrors.single(DownstreamErrorCode(downstreamErrorCode))))))

          await(service.amendBenefitAmounts(requestData)) shouldBe Left(ErrorWrapper(correlationId, error))
        }

      val errors = List(
        ("INCOME_SOURCE_NOT_FOUND", NotFoundError),
        ("INVALID_TAXABLE_ENTITY_ID", NinoFormatError),
        ("INVALID_TAX_YEAR", TaxYearFormatError),
        ("INVALID_BENEFIT_ID", BenefitIdFormatError),
        ("INVALID_CORRELATIONID", StandardDownstreamError),
        ("INVALID_PAYLOAD", StandardDownstreamError),
        ("INVALID_REQUEST_BEFORE_TAX_YEAR", RuleTaxYearNotEndedError),
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

  trait Test extends MockAmendBenefitAmountsConnector {
    implicit val logContext: EndpointLogContext = EndpointLogContext("c", "ep")

    private val nino      = "AA123456A"
    private val taxYear   = "2021-22"
    private val benefitId = "123e4567-e89b-12d3-a456-426614174000"

    val body: AmendBenefitAmountsRequestBody = AmendBenefitAmountsRequestBody(
      amount = 999.99,
      taxPaid = Some(123.13)
    )

    val requestData: AmendBenefitAmountsRequestData = AmendBenefitAmountsRequestData(
      nino = Nino(nino),
      taxYear = TaxYear.fromMtd(taxYear),
      benefitId = BenefitId(benefitId),
      body = body
    )

    val service: AmendBenefitAmountsService = new AmendBenefitAmountsService(
      connector = mockAmendBenefitAmountsConnector
    )

  }

}
