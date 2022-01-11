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

package v1r6.models.response.listBenefits

import cats._
import config.AppConfig
import play.api.libs.json._
import utils.JsonUtils
import v1r6.hateoas.{HateoasLinks, HateoasListLinksFactory2}
import v1r6.models.hateoas.{HateoasData, Link}

case class ListBenefitsResponse[H, C](stateBenefits: Option[Seq[H]],
                                      customerAddedStateBenefits: Option[Seq[C]])

object ListBenefitsResponse extends HateoasLinks with JsonUtils {

  implicit object ListBenefitsLinksFactory extends HateoasListLinksFactory2[ListBenefitsResponse,
    HMRCStateBenefit, CustomerStateBenefit, ListBenefitsHateoasData] {

    private class Links(appConfig: AppConfig, data: ListBenefitsHateoasData, stateBenefit: StateBenefit) {

      import data._

      lazy val retrieveLink: Link = retrieveSingleBenefit(appConfig, nino, taxYear, stateBenefit.benefitId)
      lazy val updateAmountsLink: Link = updateBenefitAmounts(appConfig, nino, taxYear, stateBenefit.benefitId)
      lazy val deleteAmountsLink: Link = deleteBenefitAmounts(appConfig, nino, taxYear, stateBenefit.benefitId)
      lazy val deleteLink: Link = deleteBenefit(appConfig, nino, taxYear, stateBenefit.benefitId)
      lazy val updateLink: Link = updateBenefit(appConfig, nino, taxYear, stateBenefit.benefitId)
      lazy val ignoreLink: Link = ignoreBenefit(appConfig, nino, taxYear, stateBenefit.benefitId)
      lazy val unignoreLink: Link = unignoreBenefit(appConfig, nino, taxYear, stateBenefit.benefitId)

      lazy val commonLinks = Seq(retrieveLink, updateAmountsLink)
    }

    override def itemLinks1(appConfig: AppConfig, data: ListBenefitsHateoasData, stateBenefit: HMRCStateBenefit): Seq[Link] = {
      val links = new Links(appConfig, data, stateBenefit)

      if (!data.queryIsFiltered) {
        Seq(links.retrieveLink)
      } else {
        links.commonLinks :+
          (if (stateBenefit.dateIgnored.isEmpty) links.ignoreLink else links.unignoreLink)
      }
    }

    override def itemLinks2(appConfig: AppConfig, data: ListBenefitsHateoasData, stateBenefit: CustomerStateBenefit): Seq[Link] = {
      val links = new Links(appConfig, data, stateBenefit)

      if (!data.queryIsFiltered) {
        Seq(links.retrieveLink)
      } else {
        links.commonLinks ++
          (if (stateBenefit.hasAmounts) Seq(links.deleteAmountsLink) else Nil) ++
          (if (data.hmrcBenefitIds.contains(stateBenefit.benefitId)) Nil else Seq(links.deleteLink, links.updateLink))
      }
    }

    override def links(appConfig: AppConfig, data: ListBenefitsHateoasData): Seq[Link] = {
      import data._

      Seq(
        addBenefit(appConfig, nino, taxYear),
        listBenefits(appConfig, nino, taxYear)
      )
    }
  }

  implicit object ResponseBifunctor extends Bifunctor[ListBenefitsResponse] {
    override def bimap[A, B, C, D](fab: ListBenefitsResponse[A, B])(f: A => C, g: B => D): ListBenefitsResponse[C, D] =
      ListBenefitsResponse(
        fab.stateBenefits.map(x => x.map(f)), fab.customerAddedStateBenefits.map(y => y.map(g)))
  }

  implicit def writes[A: Writes, B: Writes]: OWrites[ListBenefitsResponse[A, B]] = Json.writes[ListBenefitsResponse[A, B]]

  private def readJson[T](implicit rds: Reads[Seq[T]]): Reads[Seq[T]] = (json: JsValue) => {
    json
      .validate[JsValue]
      .flatMap(
        readJson => {
          Json.toJson(readJson.as[JsObject].fields.flatMap {
            case (field, arr: JsArray) =>
              arr.value.map {
                element =>
                  element.as[JsObject] + ("benefitType" -> Json.toJson(field))
              }
            case (field, obj: JsObject) =>
              Seq(obj.as[JsObject] + ("benefitType" -> Json.toJson(field)))
            case (_, _) => Seq.empty
          }).validate[Seq[T]]
        })
  }

  implicit def reads[A: Reads, B: Reads]: Reads[ListBenefitsResponse[A, B]] =
    for {
      stateBenefits <- (__ \ "stateBenefits").readNullable(readJson[A]).mapEmptySeqToNone
      customerAddedStateBenefits <- (__ \ "customerAddedStateBenefits").readNullable(readJson[B]).mapEmptySeqToNone
    } yield ListBenefitsResponse(stateBenefits, customerAddedStateBenefits)

}

case class ListBenefitsHateoasData(nino: String, taxYear: String, queryIsFiltered: Boolean, hmrcBenefitIds: Seq[String]) extends HateoasData
