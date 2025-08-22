/*
 * Copyright 2025 HM Revenue & Customs
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

package v1.amendBenefitAmounts.def1

import cats.data.Validated
import cats.data.Validated.Valid
import cats.implicits.catsSyntaxTuple4Semigroupal
import config.StateBenefitsAppConfig
import play.api.libs.json.JsValue
import shared.controllers.validators.Validator
import shared.controllers.validators.resolvers.{ResolveNino, ResolveNonEmptyJsonObject, ResolveParsedNumber, ResolveTaxYearMinimum}
import shared.models.domain.TaxYear
import shared.models.errors.MtdError
import v1.amendBenefitAmounts.def1.model.request.{Def1_AmendBenefitAmountsRequestBody, Def1_AmendBenefitAmountsRequestData}
import v1.amendBenefitAmounts.model.request.AmendBenefitAmountsRequestData
import v1.controllers.resolvers.ResolveBenefitId

import javax.inject.Singleton

@Singleton
class Def1_AmendBenefitAmountsValidator(nino: String, taxYear: String, benefitId: String, body: JsValue)(implicit
    stateBenefitsAppConfig: StateBenefitsAppConfig)
    extends Validator[AmendBenefitAmountsRequestData] {

  private val resolveJson = new ResolveNonEmptyJsonObject[Def1_AmendBenefitAmountsRequestBody]()

  private val resolveTaxYear: ResolveTaxYearMinimum = ResolveTaxYearMinimum(TaxYear.ending(stateBenefitsAppConfig.minimumPermittedTaxYear))

  private val resolveAmountNumber = ResolveParsedNumber()
  private val resolveTaxPaid      = ResolveParsedNumber(min = -99999999999.99)

  def validate: Validated[Seq[MtdError], Def1_AmendBenefitAmountsRequestData] =
    (
      ResolveNino(nino),
      resolveTaxYear(taxYear),
      ResolveBenefitId(benefitId),
      resolveJson(body)
    ).mapN(Def1_AmendBenefitAmountsRequestData.apply).andThen(validateBusinessRules)

  private def validateBusinessRules(parsed: Def1_AmendBenefitAmountsRequestData): Validated[Seq[MtdError], Def1_AmendBenefitAmountsRequestData] = {
    import parsed.body.*

    combine(
      resolveAmountNumber(amount, path = "/amount"),
      taxPaid.map(resolveTaxPaid(_, path = "/taxPaid")).getOrElse(Valid(()))
    ).map(_ => parsed)
  }

}
