/*
 * Copyright 2024 HM Revenue & Customs
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

package v1.hateoas

import cats.Bifunctor
import cats.implicits.toBifunctorOps
import shared.config.SharedAppConfig
import shared.hateoas.{HateoasWrapper, Link}

import javax.inject.{Inject, Singleton}

@Singleton
class HateoasFactory2 @Inject() (appConfig: SharedAppConfig) {

  def wrapList[A[_, _]: Bifunctor, I1, I2, D](payload: A[I1, I2], data: D)(implicit
      lf: HateoasListLinksFactory2[A, I1, I2, D]): HateoasWrapper[A[HateoasWrapper[I1], HateoasWrapper[I2]]] = {
    val hateoasList = payload.bimap(
      i1 => HateoasWrapper(i1, lf.itemLinks1(appConfig, data, i1)),
      i2 => HateoasWrapper(i2, lf.itemLinks2(appConfig, data, i2))
    )

    HateoasWrapper(hateoasList, lf.links(appConfig, data))
  }

}

trait HateoasListLinksFactory2[A[_, _], I1, I2, D] {
  def itemLinks1(appConfig: SharedAppConfig, data: D, item: I1): Seq[Link]

  def itemLinks2(appConfig: SharedAppConfig, data: D, item: I2): Seq[Link]

  def links(appConfig: SharedAppConfig, data: D): Seq[Link]
}
