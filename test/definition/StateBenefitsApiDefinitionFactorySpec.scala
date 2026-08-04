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

package definition

import api.config.Deprecation.NotDeprecated
import api.config.MockAppConfig
import api.definition.APIStatus.BETA
import api.definition.{APIAccessType, APIDefinition, APIVersion, Definition}
import api.routing.Version2
import api.utils.UnitSpec
import cats.implicits.catsSyntaxValidatedId

class StateBenefitsApiDefinitionFactorySpec extends UnitSpec {

  "definition" when {
    "called" should {
      "return a valid Definition case class" in new Test {
        MockedAppConfig.apiStatus(Version2) returns "BETA"
        MockedAppConfig.endpointsEnabled(Version2) returns true
        MockedAppConfig.controlledAccessEnabled returns false
        MockedAppConfig.deprecationFor(Version2).returns(NotDeprecated.valid).anyNumberOfTimes()

        apiDefinitionFactory.definition shouldBe
          Definition(
            api = APIDefinition(
              name = "Individuals State Benefits (MTD)",
              description = "An API for providing individual state benefit data",
              context = "individuals/state-benefits",
              categories = Seq("INCOME_TAX_MTD"),
              versions = Seq(
                APIVersion(
                  version = Version2,
                  status = BETA,
                  access = APIAccessType.PUBLIC,
                  endpointsEnabled = true
                )
              ),
              requiresTrust = None
            )
          )
      }
    }

    "the controlled access flag is enabled" should {
      "set the access type to CONTROLLED" in new Test {
        MockedAppConfig.apiStatus(Version2) returns "BETA"
        MockedAppConfig.endpointsEnabled(Version2) returns true
        MockedAppConfig.deprecationFor(Version2).returns(NotDeprecated.valid).anyNumberOfTimes()

        MockedAppConfig.controlledAccessEnabled returns true

        apiDefinitionFactory.definition.api.versions.head.access shouldBe APIAccessType.CONTROLLED
      }
    }

    "the controlled access flag is disabled" should {
      "set the access type to PUBLIC" in new Test {
        MockedAppConfig.apiStatus(Version2) returns "BETA"
        MockedAppConfig.endpointsEnabled(Version2) returns true
        MockedAppConfig.deprecationFor(Version2).returns(NotDeprecated.valid).anyNumberOfTimes()

        MockedAppConfig.controlledAccessEnabled returns false

        apiDefinitionFactory.definition.api.versions.head.access shouldBe APIAccessType.PUBLIC
      }
    }
  }

  trait Test extends MockAppConfig {
    MockedAppConfig.apiGatewayContext.anyNumberOfTimes() returns "individuals/state-benefits"

    val apiDefinitionFactory = new StateBenefitsApiDefinitionFactory(mockAppConfig)
  }

}
