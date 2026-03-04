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

package v2.models.domain

import play.api.libs.json.{JsString, Json}
import shared.utils.UnitSpec

class BenefitTypeSpec extends UnitSpec {

  "BenefitType" should {

    "contain all expected enum values" in {
      BenefitType.values should contain theSameElementsAs Seq(
        BenefitType.incapacityBenefit,
        BenefitType.statePension,
        BenefitType.statePensionLumpSum,
        BenefitType.employmentSupportAllowance,
        BenefitType.jobSeekersAllowance,
        BenefitType.bereavementAllowance,
        BenefitType.otherStateBenefits
      )
    }

    "serialize to JSON" in {
      val benefitType = BenefitType.incapacityBenefit
      Json.toJson(benefitType) shouldBe JsString("incapacityBenefit")
    }

    "deserialize from JSON" in {
      val json = JsString("statePension")
      json.as[BenefitType] shouldBe BenefitType.statePension
    }

  }

}
