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

package auth

import play.api.http.Status.OK
import play.api.libs.json.{JsValue, Json}
import play.api.libs.ws.{WSRequest, WSResponse}
import shared.auth.AuthMainAgentsOnlyISpec
import shared.models.domain.TaxYear
import shared.services.DownstreamStub

class StateBenefitsAuthMainAgentsOnlyISpec extends AuthMainAgentsOnlyISpec {

  val callingApiVersion = "2.0"

  val supportingAgentsNotAllowedEndpoint = "create-benefit"

  private val taxYear = TaxYear.fromMtd("2019-20")

  val mtdUrl = s"/$nino/${taxYear.asMtd}"

  val benefitId: String = "b1e8057e-fbbc-47a8-a8b4-78d9f015c253"

  def sendMtdRequest(request: WSRequest): WSResponse = await(
    request.post(
      Json.parse(
        s"""
       |{
       |  "benefitType": "incapacityBenefit",
       |  "startDate": "2019-01-01",
       |  "endDate": "2020-06-01"
       |}
      """.stripMargin
      )))

  val downstreamUri: String = s"/income-tax/income/state-benefits/$nino/${taxYear.asMtd}/custom"

  override val downstreamSuccessStatus: Int = OK

  val maybeDownstreamResponseJson: Option[JsValue] = Some(
    Json.parse(
      s"""
       |{
       |   "benefitId": "$benefitId"
       |}
        """.stripMargin
    ))

  override val downstreamHttpMethod: DownstreamStub.HTTPMethod = DownstreamStub.POST

}
