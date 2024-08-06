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

package v1.amendBenefitAmounts.def1

import api.controllers.validators.Validator
import api.controllers.validators.resolvers.{DetailedResolveTaxYear, ResolveNino, ResolveNonEmptyJsonObject, ResolveParsedNumber}
import api.models.errors.MtdError
import cats.data.Validated
import cats.data.Validated.Valid
import cats.implicits.catsSyntaxTuple4Semigroupal
import play.api.libs.json.JsValue
import v1.amendBenefitAmounts.def1.model.request.{Def1_AmendBenefitAmountsRequestBody, Def1_AmendBenefitAmountsRequestData}
import v1.amendBenefitAmounts.model.request.AmendBenefitAmountsRequestData
import v1.controllers.validators.minimumPermittedTaxYear
import v1.controllers.validators.resolvers.ResolveBenefitId

import javax.inject.Singleton
import scala.annotation.nowarn

@Singleton
class Def1_AmendBenefitAmountsValidator(nino: String, taxYear: String, benefitId: String, body: JsValue)
    extends Validator[AmendBenefitAmountsRequestData] {

  @nowarn("cat=lint-byname-implicit")
  private val resolveJson = new ResolveNonEmptyJsonObject[Def1_AmendBenefitAmountsRequestBody]()

  private val resolveTaxYear = DetailedResolveTaxYear(maybeMinimumTaxYear = Some(minimumPermittedTaxYear.year))

  private val resolveAmountNumber = ResolveParsedNumber()
  private val resolveTaxPaid      = ResolveParsedNumber(min = -99999999999.99)

  def validate: Validated[Seq[MtdError], Def1_AmendBenefitAmountsRequestData] =
    (
      ResolveNino(nino),
      resolveTaxYear(taxYear),
      ResolveBenefitId(benefitId),
      resolveJson(body)
    ).mapN(Def1_AmendBenefitAmountsRequestData) andThen validateBusinessRules

  private def validateBusinessRules(parsed: Def1_AmendBenefitAmountsRequestData): Validated[Seq[MtdError], Def1_AmendBenefitAmountsRequestData] = {
    import parsed.body._

    combine(
      resolveAmountNumber(amount, path = Some("/amount")),
      taxPaid.map(resolveTaxPaid(_, path = Some("/taxPaid"))).getOrElse(Valid(()))
    ).map(_ => parsed)
  }

}
