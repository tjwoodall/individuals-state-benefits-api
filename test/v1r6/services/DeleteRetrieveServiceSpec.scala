/*
 * Copyright 2022 HM Revenue & Customs
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

package v1r6.services

import play.api.libs.json.{Format, Json}
import v1r6.connectors.DownstreamUri.IfsUri
import v1r6.controllers.EndpointLogContext
import v1r6.mocks.connectors.MockDeleteRetrieveConnector
import v1r6.models.errors._
import v1r6.models.outcomes.ResponseWrapper

import scala.concurrent.Future

class DeleteRetrieveServiceSpec extends ServiceSpec {

  val nino: String = "AA112233A"
  val taxYear: String = "2019"

  trait Test extends MockDeleteRetrieveConnector {

    case class Data(field: Option[String])

    object Data {
      implicit val reads: Format[Data] = Json.format[Data]
    }

    implicit val logContext: EndpointLogContext = EndpointLogContext("c", "ep")
    implicit val deleteIfsUri: IfsUri[Unit] = IfsUri[Unit](s"some-placeholder/savings/$nino/$taxYear")
    implicit val retrieveIfsUri: IfsUri[Data] = IfsUri[Data](s"some-placeholder/savings/$nino/$taxYear")

    val service: DeleteRetrieveService = new DeleteRetrieveService(
      connector = mockDeleteRetrieveConnector
    )
  }

  "DeleteRetrieveService" when {
    "delete" must {
      "return correct result for a success" in new Test {
        val outcome = Right(ResponseWrapper(correlationId, ()))

        MockDeleteRetrieveConnector.delete()
          .returns(Future.successful(outcome))

        await(service.delete()) shouldBe outcome
      }

      "map errors according to spec" when {

        def serviceError(ifsErrorCode: String, error: MtdError): Unit =
          s"a $ifsErrorCode error is returned from the service" in new Test {

            MockDeleteRetrieveConnector.delete()
              .returns(Future.successful(Left(ResponseWrapper(correlationId, DesErrors.single(DesErrorCode(ifsErrorCode))))))

            await(service.delete()) shouldBe Left(ErrorWrapper(correlationId, error))
          }

        val input = Seq(
          ("INVALID_NINO", NinoFormatError),
          ("INVALID_TAX_YEAR", TaxYearFormatError),
          ("NOT_FOUND", NotFoundError),
          ("SERVER_ERROR", DownstreamError),
          ("SERVICE_UNAVAILABLE", DownstreamError)
        )

        input.foreach(args => (serviceError _).tupled(args))
      }
    }

    "retrieve" must {
      "return correct result for a success" in new Test {
        val outcome = Right(ResponseWrapper(correlationId, Data(Some("value"))))

        MockDeleteRetrieveConnector.retrieve[Data]()
          .returns(Future.successful(outcome))

        await(service.retrieve[Data]()) shouldBe outcome
      }

      "return a NotFoundError for an empty response" in new Test {
        val outcome = Right(ResponseWrapper(correlationId, Data(None)))

        MockDeleteRetrieveConnector.retrieve[Data]()
          .returns(Future.successful(outcome))

        await(service.retrieve[Data]()) shouldBe Left(ErrorWrapper(correlationId, NotFoundError))
      }

      "map errors according to spec" when {

        def serviceError(ifsErrorCode: String, error: MtdError): Unit =
          s"a $ifsErrorCode error is returned from the service" in new Test {

            MockDeleteRetrieveConnector.retrieve[Data]()
              .returns(Future.successful(Left(ResponseWrapper(correlationId, DesErrors.single(DesErrorCode(ifsErrorCode))))))

            await(service.retrieve[Data]()) shouldBe Left(ErrorWrapper(correlationId, error))
          }

        val input = Seq(
          ("INVALID_NINO", NinoFormatError),
          ("INVALID_TAX_YEAR", TaxYearFormatError),
          ("NOT_FOUND", NotFoundError),
          ("SERVER_ERROR", DownstreamError),
          ("SERVICE_UNAVAILABLE", DownstreamError)
        )

        input.foreach(args => (serviceError _).tupled(args))
      }
    }
  }
}
