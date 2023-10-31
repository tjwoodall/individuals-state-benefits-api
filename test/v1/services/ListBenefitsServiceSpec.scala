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
import api.models.domain.{Nino, TaxYear, Timestamp}
import api.models.errors._
import api.models.outcomes.ResponseWrapper
import api.services.ServiceSpec
import v1.connectors.MockListBenefitsConnector
import v1.models.domain.BenefitId
import v1.models.request.listBenefits.ListBenefitsRequest
import v1.models.response.listBenefits.{CustomerStateBenefit, HMRCStateBenefit, ListBenefitsResponse}

import scala.concurrent.Future

class ListBenefitsServiceSpec extends ServiceSpec {

  private val nino      = "AA112233A"
  private val taxYear   = "2019-20"
  private val benefitId = Some("4557ecb5-fd32-48cc-81f5-e6acd1099f3c")

  private val requestData = ListBenefitsRequest(Nino(nino), TaxYear.fromMtd(taxYear), benefitId.map(BenefitId))

  private val validResponse = ListBenefitsResponse(
    stateBenefits = Some(
      Seq(
        HMRCStateBenefit(
          benefitType = "incapacityBenefit",
          dateIgnored = Some(Timestamp("2019-04-04T01:01:01.000Z")),
          benefitId = "f0d83ac0-a10a-4d57-9e41-6d033832779f",
          startDate = "2020-01-01",
          endDate = Some("2020-04-01"),
          amount = Some(2000.00),
          taxPaid = Some(2132.22),
          submittedOn = None
        )
      )
    ),
    customerAddedStateBenefits = Some(
      Seq(
        CustomerStateBenefit(
          benefitType = "incapacityBenefit",
          benefitId = "f0d83ac0-a10a-4d57-9e41-6d033832779f",
          startDate = "2020-01-01",
          endDate = Some("2020-04-01"),
          amount = Some(2000.00),
          taxPaid = Some(2132.22),
          submittedOn = Some(Timestamp("2019-04-04T01:01:01.000Z"))
        )
      )
    )
  )

  trait Test extends MockListBenefitsConnector {
    implicit val logContext: EndpointLogContext = EndpointLogContext("c", "ep")

    val service: ListBenefitsService = new ListBenefitsService(connector = mockListBenefitsConnector)
  }

  "ListBenefitsService" when {
    "listBenefits" must {
      "return correct result for a success" in new Test {
        val outcome = Right(ResponseWrapper(correlationId, validResponse))

        MockListBenefitsConnector
          .listBenefits(requestData)
          .returns(Future.successful(outcome))

        await(service.listBenefits(requestData)) shouldBe outcome
      }

      "map errors according to spec" when {

        def serviceError(desErrorCode: String, error: MtdError): Unit =
          s"a $desErrorCode error is returned from the service" in new Test {

            MockListBenefitsConnector
              .listBenefits(requestData)
              .returns(Future.successful(Left(ResponseWrapper(correlationId, DownstreamErrors.single(DownstreamErrorCode(desErrorCode))))))

            await(service.listBenefits(requestData)) shouldBe Left(ErrorWrapper(correlationId, error))
          }

        val errors = Seq(
          ("INVALID_TAXABLE_ENTITY_ID", NinoFormatError),
          ("INVALID_TAX_YEAR", TaxYearFormatError),
          ("INVALID_BENEFIT_ID", BenefitIdFormatError),
          ("INVALID_VIEW", StandardDownstreamError),
          ("INVALID_CORRELATIONID", StandardDownstreamError),
          ("NO_DATA_FOUND", NotFoundError),
          ("TAX_YEAR_NOT_SUPPORTED", RuleTaxYearNotSupportedError),
          ("SERVER_ERROR", StandardDownstreamError),
          ("SERVICE_UNAVAILABLE", StandardDownstreamError)
        )

        val extraTysErrors = Seq(
          ("INVALID_CORRELATION_ID", StandardDownstreamError),
          ("NOT_FOUND", NotFoundError)
        )

        (errors ++ extraTysErrors).foreach(args => (serviceError _).tupled(args))
      }
    }
  }

}
