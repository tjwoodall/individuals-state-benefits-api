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

package v1.unignoreBenefit.def1

import cats.data.Validated
import cats.data.Validated.*
import cats.implicits.*
import config.StateBenefitsAppConfig
import shared.controllers.validators.Validator
import shared.controllers.validators.resolvers.{ResolveNino, ResolveTaxYearMinimum}
import shared.models.domain.TaxYear
import shared.models.errors.MtdError
import v1.controllers.resolvers.ResolveBenefitId
import v1.unignoreBenefit.def1.model.request.Def1_UnignoreBenefitRequestData
import v1.unignoreBenefit.model.request.UnignoreBenefitRequestData

import javax.inject.Singleton

@Singleton
class Def1_UnignoreBenefitValidator(nino: String, taxYear: String, benefitId: String)(implicit stateBenefitsAppConfig: StateBenefitsAppConfig)
    extends Validator[UnignoreBenefitRequestData] {

  private val resolveTaxYear: ResolveTaxYearMinimum = ResolveTaxYearMinimum(TaxYear.ending(stateBenefitsAppConfig.minimumPermittedTaxYear))

  def validate: Validated[Seq[MtdError], Def1_UnignoreBenefitRequestData] = {
    (
      ResolveNino(nino),
      resolveTaxYear(taxYear),
      ResolveBenefitId(benefitId)
    ).mapN(Def1_UnignoreBenefitRequestData.apply)
  }

}
