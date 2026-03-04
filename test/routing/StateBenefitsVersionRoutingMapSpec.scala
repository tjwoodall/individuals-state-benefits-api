/*
 * Copyright 2026 HM Revenue & Customs
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

package routing

import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import shared.utils.UnitSpec
import shared.config.SharedAppConfig
import play.api.routing.Router
import shared.routing.Version2

class StateBenefitsVersionRoutingMapSpec extends UnitSpec with GuiceOneAppPerSuite {
  val appConfig: SharedAppConfig = mock[SharedAppConfig]
  val defaultRouter: Router      = mock[Router]
  val v2Routes: v2.Routes        = app.injector.instanceOf[v2.Routes]

  "StateBenefitsVersionRoutingMap" when {
    "map is accessed" should {
      "route to v2 when Version2 is requested" in {
        val versionRoutingMap: StateBenefitsVersionRoutingMap = StateBenefitsVersionRoutingMap(
          appConfig = appConfig,
          defaultRouter = defaultRouter,
          v2Router = v2Routes
        )
        versionRoutingMap.map(Version2) shouldBe v2Routes
      }
    }
  }

}
