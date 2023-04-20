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
import api.models.domain.Nino
import api.models.errors._
import api.models.outcomes.ResponseWrapper
import api.services.ServiceSpec
import v1.mocks.connectors.MockAmendBenefitConnector
import v1.models.request.AmendBenefit.{AmendBenefitRequest, AmendBenefitRequestBody}

import scala.concurrent.Future

class AmendBenefitServiceSpec extends ServiceSpec {

  "AmendBenefitService" when {
    "amendBenefit" must {
      "return correct result for a success" in new Test {
        val outcome: Right[Nothing, ResponseWrapper[Unit]] = Right(ResponseWrapper(correlationId, ()))

        MockAmendBenefitConnector
          .amendBenefit(requestData)
          .returns(Future.successful(outcome))

        val result: AmendBenefitServiceOutcome = await(service.amendBenefit(requestData))
        result shouldBe outcome
      }
    }

    "map errors according to spec" when {
      def serviceError(downstreamErrorCode: String, error: MtdError): Unit =
        s"a $downstreamErrorCode error is returned from the service" in new Test {

          MockAmendBenefitConnector
            .amendBenefit(requestData)
            .returns(Future.successful(Left(ResponseWrapper(correlationId, DownstreamErrors.single(DownstreamErrorCode(downstreamErrorCode))))))

          val result: AmendBenefitServiceOutcome = await(service.amendBenefit(requestData))
          result shouldBe Left(ErrorWrapper(correlationId, error))
        }

      val errors = List(
        ("INVALID_TAXABLE_ENTITY_ID", NinoFormatError),
        ("INVALID_TAX_YEAR", TaxYearFormatError),
        ("INVALID_BENEFIT_ID", BenefitIdFormatError),
        ("INVALID_CORRELATIONID", StandardDownstreamError),
        ("INVALID_PAYLOAD", StandardDownstreamError),
        ("UPDATE_FORBIDDEN", RuleUpdateForbiddenError),
        ("NO_DATA_FOUND", NotFoundError),
        ("INVALID_START_DATE", RuleStartDateAfterTaxYearEndError),
        ("INVALID_CESSATION_DATE", RuleEndDateBeforeTaxYearStartError),
        ("SERVER_ERROR", StandardDownstreamError),
        ("SERVICE_UNAVAILABLE", StandardDownstreamError)
      )

      errors.foreach(args => (serviceError _).tupled(args))
    }
  }

  private trait Test extends MockAmendBenefitConnector {
    implicit val logContext: EndpointLogContext = EndpointLogContext("c", "ep")

    val nino      = "AA123456A"
    val taxYear   = "2021-22"
    val benefitId = "123e4567-e89b-12d3-a456-426614174000"

    val amendBenefitRequestBody: AmendBenefitRequestBody = AmendBenefitRequestBody("2020-08-03", Some("2020-12-03"))

    val requestData: AmendBenefitRequest = AmendBenefitRequest(Nino(nino), taxYear, benefitId, amendBenefitRequestBody)

    val service: AmendBenefitService = new AmendBenefitService(
      connector = mockAmendBenefitConnector
    )

  }

}
