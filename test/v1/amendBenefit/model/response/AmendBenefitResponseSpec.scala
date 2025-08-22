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

package v1.amendBenefit.model.response


import shared.config.MockSharedAppConfig
import shared.hateoas.Link
import shared.hateoas.Method.*
import shared.utils.UnitSpec

class AmendBenefitResponseSpec extends UnitSpec with MockSharedAppConfig {

  "LinksFactory" should {
    "produce the correct links" when {
      "called" in {
        val data: AmendBenefitHateoasData = AmendBenefitHateoasData("mynino", "mytaxyear", "mybenefitid")

        MockedSharedAppConfig.apiGatewayContext.returns("my/context").anyNumberOfTimes()

        AmendBenefitResponse.AmendBenefitLinksFactory.links(mockSharedAppConfig, data) shouldBe Seq(
          Link(href = s"/my/context/${data.nino}/${data.taxYear}/${data.benefitId}", method = PUT, rel = "amend-state-benefit"),
          Link(href = s"/my/context/${data.nino}/${data.taxYear}?benefitId=${data.benefitId}", method = GET, rel = "self"),
          Link(href = s"/my/context/${data.nino}/${data.taxYear}/${data.benefitId}", method = DELETE, rel = "delete-state-benefit"),
          Link(href = s"/my/context/${data.nino}/${data.taxYear}/${data.benefitId}/amounts", method = PUT, rel = "amend-state-benefit-amounts")
        )
      }
    }
  }

}
