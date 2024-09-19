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

package v1

import shared.config.AppConfig
import shared.hateoas.Link
import shared.hateoas.Method._

trait HateoasLinks {

  private object RelType {
    val CREATE_STATE_BENEFIT         = "create-state-benefit"
    val AMEND_STATE_BENEFIT          = "amend-state-benefit"
    val DELETE_STATE_BENEFIT         = "delete-state-benefit"
    val AMEND_STATE_BENEFIT_AMOUNTS  = "amend-state-benefit-amounts"
    val DELETE_STATE_BENEFIT_AMOUNTS = "delete-state-benefit-amounts"
    val IGNORE_STATE_BENEFIT         = "ignore-state-benefit"
    val UNIGNORE_STATE_BENEFIT       = "unignore-state-benefit"

    val SELF = "self"
  }

  // Uris
  private def baseUri(appConfig: AppConfig, nino: String, taxYear: String) =
    s"/${appConfig.apiGatewayContext}/$nino/$taxYear"

  private def baseUriWithBenefitIdParam(appConfig: AppConfig, nino: String, taxYear: String, id: String) =
    s"""/${appConfig.apiGatewayContext}/$nino/$taxYear?benefitId=$id"""

  private def uriWithId(appConfig: AppConfig, nino: String, taxYear: String, benefitId: String) =
    s"/${appConfig.apiGatewayContext}/$nino/$taxYear/$benefitId"

  private def uriWithAmounts(appConfig: AppConfig, nino: String, taxYear: String, benefitId: String) =
    s"/${appConfig.apiGatewayContext}/$nino/$taxYear/$benefitId/amounts"

  // Links
  def createBenefit(appConfig: AppConfig, nino: String, taxYear: String): Link =
    Link(
      href = baseUri(appConfig, nino, taxYear),
      method = POST,
      rel = RelType.CREATE_STATE_BENEFIT
    )

  def amendBenefit(appConfig: AppConfig, nino: String, taxYear: String, benefitId: String): Link =
    Link(
      href = uriWithId(appConfig, nino, taxYear, benefitId),
      method = PUT,
      rel = RelType.AMEND_STATE_BENEFIT
    )

  def deleteBenefit(appConfig: AppConfig, nino: String, taxYear: String, benefitId: String): Link =
    Link(
      href = uriWithId(appConfig, nino, taxYear, benefitId),
      method = DELETE,
      rel = RelType.DELETE_STATE_BENEFIT
    )

  def listBenefits(appConfig: AppConfig, nino: String, taxYear: String): Link =
    Link(
      href = baseUri(appConfig, nino, taxYear),
      method = GET,
      rel = RelType.SELF
    )

  def listSingleBenefit(appConfig: AppConfig, nino: String, taxYear: String, benefitId: String): Link =
    Link(
      href = baseUriWithBenefitIdParam(appConfig, nino, taxYear, benefitId),
      method = GET,
      rel = RelType.SELF
    )

  def amendBenefitAmounts(appConfig: AppConfig, nino: String, taxYear: String, benefitId: String): Link =
    Link(
      href = uriWithAmounts(appConfig, nino, taxYear, benefitId),
      method = PUT,
      rel = RelType.AMEND_STATE_BENEFIT_AMOUNTS
    )

  def deleteBenefitAmounts(appConfig: AppConfig, nino: String, taxYear: String, benefitId: String): Link =
    Link(
      href = uriWithAmounts(appConfig, nino, taxYear, benefitId),
      method = DELETE,
      rel = RelType.DELETE_STATE_BENEFIT_AMOUNTS
    )

  def ignoreBenefit(appConfig: AppConfig, nino: String, taxYear: String, benefitId: String): Link =
    Link(
      href = s"${uriWithId(appConfig, nino, taxYear, benefitId)}/ignore",
      method = POST,
      rel = RelType.IGNORE_STATE_BENEFIT
    )

  def unignoreBenefit(appConfig: AppConfig, nino: String, taxYear: String, benefitId: String): Link =
    Link(
      href = s"${uriWithId(appConfig, nino, taxYear, benefitId)}/unignore",
      method = POST,
      rel = RelType.UNIGNORE_STATE_BENEFIT
    )

}
