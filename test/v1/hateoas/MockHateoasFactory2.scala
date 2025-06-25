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
import org.scalamock.handlers.CallHandler
import org.scalamock.scalatest.MockFactory
import org.scalatest.TestSuite
import shared.hateoas.{HateoasData, HateoasWrapper}

trait MockHateoasFactory2 extends TestSuite with MockFactory {

  val mockHateoasFactory: HateoasFactory2 = mock[HateoasFactory2]

  object MockHateoasFactory2 {

    def wrapList[A[_, _]: Bifunctor, I1, I2, D <: HateoasData](a: A[I1, I2],
                                                               data: D): CallHandler[HateoasWrapper[A[HateoasWrapper[I1], HateoasWrapper[I2]]]] = {
      (mockHateoasFactory
        .wrapList(_: A[I1, I2], _: D)(_: Bifunctor[A], _: HateoasListLinksFactory2[A, I1, I2, D]))
        .expects(a, data, *, *)
    }

  }

}
