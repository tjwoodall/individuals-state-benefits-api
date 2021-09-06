/*
 * Copyright 2021 HM Revenue & Customs
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

package v1r6.hateoas

import cats._
import config.AppConfig
import mocks.MockAppConfig
import support.UnitSpec
import v1r6.models.hateoas.Method.GET
import v1r6.models.hateoas.{HateoasData, HateoasWrapper, Link}

class HateoasFactorySpec extends UnitSpec with MockAppConfig {
  val hateoasFactory = new HateoasFactory(mockAppConfig)

  case class Data1(id: String) extends HateoasData

  case class Data2(id: String) extends HateoasData

  class Test {
    MockAppConfig.apiGatewayContext.returns("context").anyNumberOfTimes
  }

  "wrap" should {
    case class Response(foo: String)
    val response: Response = Response("X")

    implicit object LinksFactory1 extends HateoasLinksFactory[Response, Data1] {
      override def links(appConfig: AppConfig, data: Data1): Seq[Link] = Seq(Link(s"${appConfig.apiGatewayContext}/${data.id}", GET, "rel1"))
    }

    implicit object LinksFactory2 extends HateoasLinksFactory[Response, Data2] {
      override def links(appConfig: AppConfig, data: Data2): Seq[Link] = Seq(Link(s"${appConfig.apiGatewayContext}/${data.id}", GET, "rel2"))
    }

    "use the response specific links" in new Test {
      hateoasFactory.wrap(response, Data1("id")) shouldBe HateoasWrapper(response, Seq(Link("context/id", GET, "rel1")))
    }

    "use the endpoint HateoasData specific links" in new Test {
      hateoasFactory.wrap(response, Data2("id")) shouldBe HateoasWrapper(response, Seq(Link("context/id", GET, "rel2")))
    }
  }

  "wrapList" should {
    case class ListResponse[A](items: Seq[A])
    case class Item(foo: String)
    val item = Item("theItem")

    implicit object ListResponseFunctor extends Functor[ListResponse] {
      override def map[A, B](fa: ListResponse[A])(f: A => B): ListResponse[B] = ListResponse(fa.items.map(f))
    }

    implicit object LinksFactory extends HateoasListLinksFactory[ListResponse, Item, Data1] {
      override def itemLinks(appConfig: AppConfig, data: Data1, item: Item): Seq[Link] =
        Seq(Link(s"${appConfig.apiGatewayContext}/${data.id}/${item.foo}", GET, "item"))

      override def links(appConfig: AppConfig, data: Data1): Seq[Link] = Seq(Link(s"${appConfig.apiGatewayContext}/${data.id}", GET, "rel"))
    }

    "work" in new Test {
      hateoasFactory.wrapList(ListResponse(Seq(item)), Data1("id")) shouldBe
        HateoasWrapper(ListResponse(Seq(HateoasWrapper(item, Seq(Link("context/id/theItem", GET, "item"))))), Seq(Link("context/id", GET, "rel")))
    }
  }

  "wrapList for bifunctor case" should {
    case class ListResponse[A, B](items1: Seq[A], items2: Seq[B])
    case class Item1(foo: String)
    case class Item2(foo: String)
    val item1 = Item1("theItem1")
    val item2 = Item2("theItem2")
    implicit object ListResponseFunctor extends Bifunctor[ListResponse] {
      override def bimap[A, B, C, D](fab: ListResponse[A, B])(f: A => C, g: B => D): ListResponse[C, D] =
        ListResponse(fab.items1.map(f), fab.items2.map(g))
    }

    implicit object LinksFactory extends HateoasListLinksFactory2[ListResponse, Item1, Item2, Data1] {
      override def itemLinks1(appConfig: AppConfig, data1: Data1, item: Item1): Seq[Link] =
        Seq(Link(s"${appConfig.apiGatewayContext}/${data1.id}/${item.foo}", GET, "item1"))

      override def itemLinks2(appConfig: AppConfig, data1: Data1, item: Item2): Seq[Link] =
        Seq(Link(s"${appConfig.apiGatewayContext}/${data1.id}/${item.foo}", GET, "item2"))

      override def links(appConfig: AppConfig, data: Data1): Seq[Link] = Seq(Link(s"${appConfig.apiGatewayContext}/${data.id}", GET, "rel"))
    }

    "work" in new Test {
      hateoasFactory.wrapList(ListResponse(Seq(item1), Seq(item2)), Data1("id")) shouldBe
        HateoasWrapper(ListResponse(
          items1 = Seq(HateoasWrapper(item1, Seq(Link("context/id/theItem1", GET, "item1")))),
          items2 = Seq(HateoasWrapper(item2, Seq(Link("context/id/theItem2", GET, "item2"))))),
          Seq(Link("context/id", GET, "rel")))
    }
  }
}
