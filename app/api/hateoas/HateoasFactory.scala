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

package api.hateoas

import cats._
import cats.implicits._
import config.AppConfig

import javax.inject.Inject

class HateoasFactory @Inject() (appConfig: AppConfig) {

  def wrap[A, D <: HateoasData](payload: A, data: D)(implicit lf: HateoasLinksFactory[A, D]): HateoasWrapper[A] = {
    val links = lf.links(appConfig, data)

    HateoasWrapper(payload, links)
  }

  def wrapList[A[_]: Functor, I, D](payload: A[I], data: D)(implicit lf: HateoasListLinksFactory[A, I, D]): HateoasWrapper[A[HateoasWrapper[I]]] = {
    val hateoasList = payload.map(i => HateoasWrapper(i, lf.itemLinks(appConfig, data, i)))

    HateoasWrapper(hateoasList, lf.links(appConfig, data))
  }

  def wrapList[A[_, _]: Bifunctor, I1, I2, D](payload: A[I1, I2], data: D)(implicit
      lf: HateoasListLinksFactory2[A, I1, I2, D]): HateoasWrapper[A[HateoasWrapper[I1], HateoasWrapper[I2]]] = {
    val hateoasList = payload.bimap(
      i1 => HateoasWrapper(i1, lf.itemLinks1(appConfig, data, i1)),
      i2 => HateoasWrapper(i2, lf.itemLinks2(appConfig, data, i2))
    )

    HateoasWrapper(hateoasList, lf.links(appConfig, data))
  }

}

trait HateoasLinksFactory[A, D] {
  def links(appConfig: AppConfig, data: D): Seq[Link]
}

trait HateoasListLinksFactory[A[_], I, D] extends HateoasLinksFactory[A[_], D] {
  def itemLinks(appConfig: AppConfig, data: D, item: I): Seq[Link]
}

trait HateoasListLinksFactory2[A[_, _], I1, I2, D] extends HateoasLinksFactory[A[_, _], D] {
  def itemLinks1(appConfig: AppConfig, data: D, item: I1): Seq[Link]

  def itemLinks2(appConfig: AppConfig, data: D, item: I2): Seq[Link]
}
