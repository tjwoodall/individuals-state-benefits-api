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

package v1.models.response.amendBenefit

import api.hateoas.{HateoasLinks, HateoasLinksFactory}
import api.models.hateoas.{HateoasData, Link}
import config.AppConfig

object AmendBenefitResponse extends HateoasLinks {

  implicit object AmendBenefitLinksFactory extends HateoasLinksFactory[Unit, AmendBenefitHateoasData] {

    override def links(appConfig: AppConfig, data: AmendBenefitHateoasData): Seq[Link] = {
      import data._
      Seq(
        amendBenefit(appConfig, nino, taxYear, benefitId),
        listSingleBenefit(appConfig, nino, taxYear, benefitId),
        deleteBenefit(appConfig, nino, taxYear, benefitId),
        amendBenefitAmounts(appConfig, nino, taxYear, benefitId)
      )
    }

  }

}

case class AmendBenefitHateoasData(nino: String, taxYear: String, benefitId: String) extends HateoasData
