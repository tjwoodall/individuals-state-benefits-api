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

package v1.unignoreBenefit.model.response

import shared.hateoas.{HateoasData, HateoasLinksFactory, Link}
import shared.config.SharedAppConfig
import v1.HateoasLinks

object UnignoreBenefitResponse extends HateoasLinks {

  implicit object UnignoreBenefitLinksFactory extends HateoasLinksFactory[Unit, UnignoreBenefitHateoasData] {

    override def links(appConfig: SharedAppConfig, data: UnignoreBenefitHateoasData): Seq[Link] = {
      import data.*
      Seq(
        listSingleBenefit(appConfig, nino, taxYear, benefitId),
        ignoreBenefit(appConfig, nino, taxYear, benefitId)
      )
    }

  }

}

case class UnignoreBenefitHateoasData(nino: String, taxYear: String, benefitId: String) extends HateoasData
