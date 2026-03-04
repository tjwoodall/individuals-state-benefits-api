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

import cats.implicits.catsSyntaxValidatedId
import shared.config.Deprecation.NotDeprecated
import shared.config.MockSharedAppConfig
import shared.definition.APIStatus.BETA
import shared.definition.{APIDefinition, APIVersion, Definition}
import shared.mocks.MockHttpClient
import shared.routing.Version2
import shared.utils.UnitSpec

class StateBenefitsApiDefinitionFactorySpec extends UnitSpec with MockHttpClient with MockSharedAppConfig {

  "definition" when {
    "called" should {
      "return a valid Definition case class" in {
        MockedSharedAppConfig.apiGatewayContext.anyNumberOfTimes() returns "individuals/state-benefits"
        MockedSharedAppConfig.apiStatus(Version2) returns "BETA"
        MockedSharedAppConfig.endpointsEnabled(Version2) returns true
        MockedSharedAppConfig.deprecationFor(Version2).returns(NotDeprecated.valid).anyNumberOfTimes()

        val apiDefinitionFactory = new StateBenefitsApiDefinitionFactory(mockSharedAppConfig)

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
                  endpointsEnabled = true
                )
              ),
              requiresTrust = None
            )
          )
      }
    }
  }

}
