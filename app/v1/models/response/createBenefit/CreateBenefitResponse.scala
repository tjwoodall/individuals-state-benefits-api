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

package v1.models.response.createBenefit

import api.hateoas.{HateoasData, HateoasLinks, HateoasLinksFactory, Link}
import config.AppConfig
import play.api.libs.json.{Json, OFormat}

case class CreateBenefitResponse(benefitId: String)

object CreateBenefitResponse extends HateoasLinks {

  implicit val format: OFormat[CreateBenefitResponse] = Json.format[CreateBenefitResponse]

  implicit object CreateBenefitLinksFactory extends HateoasLinksFactory[CreateBenefitResponse, CreateBenefitHateoasData] {

    override def links(appConfig: AppConfig, data: CreateBenefitHateoasData): Seq[Link] = {
      import data._
      Seq(
        listSingleBenefit(appConfig, nino, taxYear, benefitId),
        amendBenefit(appConfig, nino, taxYear, benefitId),
        deleteBenefit(appConfig, nino, taxYear, benefitId)
      )
    }

  }

}

case class CreateBenefitHateoasData(nino: String, taxYear: String, benefitId: String) extends HateoasData
