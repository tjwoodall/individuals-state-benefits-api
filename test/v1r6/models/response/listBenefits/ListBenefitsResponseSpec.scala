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

import play.api.libs.json.{JsValue, Json}
import support.UnitSpec
import v1r6.fixtures.ListBenefitsFixture._

class ListBenefitsResponseSpec extends UnitSpec {

  val model: ListBenefitsResponse[HMRCStateBenefit, CustomerStateBenefit] = ListBenefitsResponse(
    stateBenefits = Some(
      Seq(
        HMRCStateBenefit(
          benefitType = "incapacityBenefit",
          dateIgnored = Some("2019-04-04T01:01:01Z"),
          benefitId = "f0d83ac0-a10a-4d57-9e41-6d033832779f",
          startDate = "2020-01-01",
          endDate = Some("2020-04-01"),
          amount = Some(2000.00),
          taxPaid = Some(2132.22),
          submittedOn = None
        ),
        HMRCStateBenefit(
          benefitType = "incapacityBenefit",
          dateIgnored = Some("2019-04-04T01:01:01Z"),
          benefitId = "f0d83ac0-a10a-4d57-9e41-6d033832779g",
          startDate = "2020-03-01",
          endDate = Some("2020-04-01"),
          amount = Some(1000.00),
          taxPaid = None,
          submittedOn = None
        ),
        HMRCStateBenefit(
          benefitType = "statePension",
          dateIgnored = Some("2019-04-04T01:01:01Z"),
          benefitId = "f0d83ac0-a10a-4d57-9e41-6d033832779f",
          startDate = "2019-01-01",
          endDate = None,
          amount = Some(2000.00),
          taxPaid = None,
          submittedOn = None
        ),
        HMRCStateBenefit(
          benefitType = "statePensionLumpSum",
          dateIgnored = Some("2019-04-04T01:01:01Z"),
          benefitId = "f0d83ac0-a10a-4d57-9e41-6d033832779f",
          startDate = "2019-01-01",
          endDate = Some("2019-01-01"),
          amount = Some(2000.00),
          taxPaid = Some(2132.22),
          submittedOn = None
        ),
        HMRCStateBenefit(
          benefitType = "employmentSupportAllowance",
          dateIgnored = Some("2019-04-04T01:01:01Z"),
          benefitId = "f0d83ac0-a10a-4d57-9e41-6d033832779f",
          startDate = "2020-01-01",
          endDate = Some("2020-04-01"),
          amount = Some(2000.00),
          taxPaid = Some(2132.22),
          submittedOn = None
        ),
        HMRCStateBenefit(
          benefitType = "employmentSupportAllowance",
          dateIgnored = Some("2019-04-04T01:01:01Z"),
          benefitId = "f0d83ac0-a10a-4d57-9e41-6d033832779g",
          startDate = "2020-01-01",
          endDate = Some("2020-04-01"),
          amount = Some(1000.00),
          taxPaid = None,
          submittedOn = None
        ),
        HMRCStateBenefit(
          benefitType = "jobSeekersAllowance",
          dateIgnored = Some("2019-04-04T01:01:01Z"),
          benefitId = "f0d83ac0-a10a-4d57-9e41-6d033832779f",
          startDate = "2020-01-01",
          endDate = Some("2020-04-01"),
          amount = Some(2000.00),
          taxPaid = Some(2132.22),
          submittedOn = None
        ),
        HMRCStateBenefit(
          benefitType = "jobSeekersAllowance",
          dateIgnored = Some("2019-04-04T01:01:01Z"),
          benefitId = "f0d83ac0-a10a-4d57-9e41-6d033832779g",
          startDate = "2020-01-01",
          endDate = Some("2020-04-01"),
          amount = Some(1000.00),
          taxPaid = None,
          submittedOn = None
        ),
        HMRCStateBenefit(
          benefitType = "bereavementAllowance",
          dateIgnored = Some("2019-04-04T01:01:01Z"),
          benefitId = "f0d83ac0-a10a-4d57-9e41-6d033832779f",
          startDate = "2020-01-01",
          endDate = Some("2020-04-01"),
          amount = Some(2000.00),
          taxPaid = None,
          submittedOn = None
        ),
        HMRCStateBenefit(
          benefitType = "otherStateBenefits",
          dateIgnored = Some("2019-04-04T01:01:01Z"),
          benefitId = "f0d83ac0-a10a-4d57-9e41-6d033832779f",
          startDate = "2020-01-01",
          endDate = Some("2020-04-01"),
          amount = Some(2000.00),
          taxPaid = None,
          submittedOn = None
        )
      )
    ),
    customerAddedStateBenefits = Some(
      Seq(
        CustomerStateBenefit(
          benefitType = "incapacityBenefit",
          benefitId = "f0d83ac0-a10a-4d57-9e41-6d033832779f",
          startDate = "2020-01-01",
          endDate = Some("2020-04-01"),
          amount = Some(2000.00),
          taxPaid = Some(2132.22),
          submittedOn = Some("2019-04-04T01:01:01Z")
        ),
        CustomerStateBenefit(
          benefitType = "incapacityBenefit",
          benefitId = "f0d83ac0-a10a-4d57-9e41-6d033832779g",
          startDate = "2020-03-01",
          endDate = Some("2020-04-01"),
          amount = Some(1000.00),
          taxPaid = None,
          submittedOn = Some("2019-04-04T01:01:01Z")
        ),
        CustomerStateBenefit(
          benefitType = "statePension",
          benefitId = "f0d83ac0-a10a-4d57-9e41-6d033832779f",
          startDate = "2019-01-01",
          endDate = None,
          amount = Some(2000.00),
          taxPaid = None,
          submittedOn = Some("2019-04-04T01:01:01Z")
        ),
        CustomerStateBenefit(
          benefitType = "statePensionLumpSum",
          benefitId = "f0d83ac0-a10a-4d57-9e41-6d033832779f",
          startDate = "2019-01-01",
          endDate = Some("2019-01-01"),
          amount = Some(2000.00),
          taxPaid = Some(2132.22),
          submittedOn = Some("2019-04-04T01:01:01Z")
        ),
        CustomerStateBenefit(
          benefitType = "employmentSupportAllowance",
          benefitId = "f0d83ac0-a10a-4d57-9e41-6d033832779f",
          startDate = "2020-01-01",
          endDate = Some("2020-04-01"),
          amount = Some(2000.00),
          taxPaid = Some(2132.22),
          submittedOn = Some("2019-04-04T01:01:01Z")
        ),
        CustomerStateBenefit(
          benefitType = "jobSeekersAllowance",
          benefitId = "f0d83ac0-a10a-4d57-9e41-6d033832779f",
          startDate = "2020-01-01",
          endDate = Some("2020-04-01"),
          amount = Some(2000.00),
          taxPaid = Some(2132.22),
          submittedOn = Some("2019-04-04T01:01:01Z")
        ),
        CustomerStateBenefit(
          benefitType = "bereavementAllowance",
          benefitId = "f0d83ac0-a10a-4d57-9e41-6d033832779f",
          startDate = "2020-01-01",
          endDate = Some("2020-04-01"),
          amount = Some(2000.00),
          taxPaid = None,
          submittedOn = Some("2019-04-04T01:01:01Z")
        ),
        CustomerStateBenefit(
          benefitType = "otherStateBenefits",
          benefitId = "f0d83ac0-a10a-4d57-9e41-6d033832779f",
          startDate = "2020-01-01",
          endDate = Some("2020-04-01"),
          amount = Some(2000.00),
          taxPaid = None,
          submittedOn = Some("2019-04-04T01:01:01Z")
        )
      )
    )
  )

  val mtdJson: JsValue = Json.parse(
    """
      |  {
      |    "stateBenefits": [
      |      {
      |        "benefitType": "incapacityBenefit",
      |        "dateIgnored": "2019-04-04T01:01:01Z",
      |        "benefitId": "f0d83ac0-a10a-4d57-9e41-6d033832779f",
      |        "startDate": "2020-01-01",
      |        "endDate": "2020-04-01",
      |        "amount": 2000.00,
      |        "taxPaid": 2132.22
      |      },
      |      {
      |        "benefitType": "incapacityBenefit",
      |        "dateIgnored": "2019-04-04T01:01:01Z",
      |        "benefitId": "f0d83ac0-a10a-4d57-9e41-6d033832779g",
      |        "startDate": "2020-03-01",
      |        "endDate": "2020-04-01",
      |        "amount": 1000.00
      |      },
      |      {
      |        "benefitType": "statePension",
      |        "dateIgnored": "2019-04-04T01:01:01Z",
      |        "benefitId": "f0d83ac0-a10a-4d57-9e41-6d033832779f",
      |        "startDate": "2019-01-01",
      |        "amount": 2000.00
      |      },
      |      {
      |        "benefitType": "statePensionLumpSum",
      |        "dateIgnored": "2019-04-04T01:01:01Z",
      |        "benefitId": "f0d83ac0-a10a-4d57-9e41-6d033832779f",
      |        "startDate": "2019-01-01",
      |        "endDate"  : "2019-01-01",
      |        "amount": 2000.00,
      |        "taxPaid": 2132.22
      |      },
      |      {
      |        "benefitType": "employmentSupportAllowance",
      |        "dateIgnored": "2019-04-04T01:01:01Z",
      |        "benefitId": "f0d83ac0-a10a-4d57-9e41-6d033832779f",
      |        "startDate": "2020-01-01",
      |        "endDate": "2020-04-01",
      |        "amount": 2000.00,
      |        "taxPaid": 2132.22
      |      },
      |      {
      |        "benefitType": "employmentSupportAllowance",
      |        "dateIgnored": "2019-04-04T01:01:01Z",
      |        "benefitId": "f0d83ac0-a10a-4d57-9e41-6d033832779g",
      |        "startDate": "2020-01-01",
      |        "endDate": "2020-04-01",
      |        "amount": 1000.00
      |      },
      |      {
      |        "benefitType": "jobSeekersAllowance",
      |        "dateIgnored": "2019-04-04T01:01:01Z",
      |        "benefitId": "f0d83ac0-a10a-4d57-9e41-6d033832779f",
      |        "startDate": "2020-01-01",
      |        "endDate": "2020-04-01",
      |        "amount": 2000.00,
      |        "taxPaid": 2132.22
      |      },
      |      {
      |        "benefitType": "jobSeekersAllowance",
      |        "dateIgnored": "2019-04-04T01:01:01Z",
      |        "benefitId": "f0d83ac0-a10a-4d57-9e41-6d033832779g",
      |        "startDate": "2020-01-01",
      |        "endDate": "2020-04-01",
      |        "amount": 1000.00
      |      },
      |      {
      |        "benefitType": "bereavementAllowance",
      |        "dateIgnored": "2019-04-04T01:01:01Z",
      |        "benefitId": "f0d83ac0-a10a-4d57-9e41-6d033832779f",
      |        "startDate": "2020-01-01",
      |        "endDate": "2020-04-01",
      |        "amount": 2000.00
      |      },
      |      {
      |        "benefitType": "otherStateBenefits",
      |        "dateIgnored": "2019-04-04T01:01:01Z",
      |        "benefitId": "f0d83ac0-a10a-4d57-9e41-6d033832779f",
      |        "startDate": "2020-01-01",
      |        "endDate": "2020-04-01",
      |        "amount": 2000.00
      |      }
      |    ],
      |    "customerAddedStateBenefits": [
      |      {
      |        "benefitType": "incapacityBenefit",
      |        "submittedOn": "2019-04-04T01:01:01Z",
      |        "benefitId": "f0d83ac0-a10a-4d57-9e41-6d033832779f",
      |        "startDate": "2020-01-01",
      |        "endDate": "2020-04-01",
      |        "amount": 2000.00,
      |        "taxPaid": 2132.22
      |      },
      |      {
      |        "benefitType": "incapacityBenefit",
      |        "submittedOn": "2019-04-04T01:01:01Z",
      |        "benefitId": "f0d83ac0-a10a-4d57-9e41-6d033832779g",
      |        "startDate": "2020-03-01",
      |        "endDate": "2020-04-01",
      |        "amount": 1000.00
      |      },
      |      {
      |        "benefitType": "statePension",
      |        "submittedOn": "2019-04-04T01:01:01Z",
      |        "benefitId": "f0d83ac0-a10a-4d57-9e41-6d033832779f",
      |        "startDate": "2019-01-01",
      |        "amount": 2000.00
      |      },
      |      {
      |        "benefitType": "statePensionLumpSum",
      |        "submittedOn": "2019-04-04T01:01:01Z",
      |        "benefitId": "f0d83ac0-a10a-4d57-9e41-6d033832779f",
      |        "startDate": "2019-01-01",
      |        "endDate" : "2019-01-01",
      |        "amount": 2000.00,
      |        "taxPaid": 2132.22
      |      },
      |      {
      |        "benefitType": "employmentSupportAllowance",
      |        "submittedOn": "2019-04-04T01:01:01Z",
      |        "benefitId": "f0d83ac0-a10a-4d57-9e41-6d033832779f",
      |        "startDate": "2020-01-01",
      |        "endDate": "2020-04-01",
      |        "amount": 2000.00,
      |        "taxPaid": 2132.22
      |      },
      |      {
      |        "benefitType": "jobSeekersAllowance",
      |        "submittedOn": "2019-04-04T01:01:01Z",
      |        "benefitId": "f0d83ac0-a10a-4d57-9e41-6d033832779f",
      |        "startDate": "2020-01-01",
      |        "endDate": "2020-04-01",
      |        "amount": 2000.00,
      |        "taxPaid": 2132.22
      |      },
      |      {
      |        "benefitType": "bereavementAllowance",
      |        "submittedOn": "2019-04-04T01:01:01Z",
      |        "benefitId": "f0d83ac0-a10a-4d57-9e41-6d033832779f",
      |        "startDate": "2020-01-01",
      |        "endDate": "2020-04-01",
      |        "amount": 2000.00
      |      },
      |      {
      |        "benefitType": "otherStateBenefits",
      |        "submittedOn": "2019-04-04T01:01:01Z",
      |        "benefitId": "f0d83ac0-a10a-4d57-9e41-6d033832779f",
      |        "startDate": "2020-01-01",
      |        "endDate": "2020-04-01",
      |        "amount": 2000.00
      |      }
      |    ]
      |  }""".stripMargin)


  "ListBenefitsResponse" when {
    "read from valid JSON" should {

      "produce the expected ListBenefitsResponse object" in {
        desJson.as[ListBenefitsResponse[HMRCStateBenefit, CustomerStateBenefit]] shouldBe model
      }

      def hmrcBenefit(benefitType: String, benefitId: String): HMRCStateBenefit = HMRCStateBenefit(
        benefitType,
        dateIgnored = None,
        submittedOn = None,
        benefitId,
        startDate = "2020-01-01",
        endDate = None,
        amount = None,
        taxPaid = None)

      def customerBenefit(benefitType: String, benefitId: String): CustomerStateBenefit = CustomerStateBenefit(
        benefitType,
        submittedOn = None,
        benefitId,
        startDate = "2020-01-01",
        endDate = None,
        amount = None,
        taxPaid = None)

      "read any HMRCStateBenefits from nested arrays" in {
        Json.parse(
          """
            |{
            |  "stateBenefits": {
            |    "benefit1": [
            |       {"benefitId": "benefit1Id1", "startDate": "2020-01-01"},
            |       {"benefitId": "benefit1Id2", "startDate": "2020-01-01"}
            |    ],
            |    "benefit2": [
            |       {"benefitId": "benefit2Id1", "startDate": "2020-01-01"},
            |       {"benefitId": "benefit2Id2", "startDate": "2020-01-01"}
            |    ]
            |  }
            |}
            |""".stripMargin).as[ListBenefitsResponse[HMRCStateBenefit, CustomerStateBenefit]] shouldBe
          ListBenefitsResponse(
            stateBenefits = Some(Seq(
              hmrcBenefit(benefitType = "benefit1", benefitId = "benefit1Id1"),
              hmrcBenefit(benefitType = "benefit1", benefitId = "benefit1Id2"),
              hmrcBenefit(benefitType = "benefit2", benefitId = "benefit2Id1"),
              hmrcBenefit(benefitType = "benefit2", benefitId = "benefit2Id2"),
            )),
            customerAddedStateBenefits = None)
      }

      "read any HMRCStateBenefits from nested objects" in {
        Json.parse(
          """
            |{
            |  "stateBenefits": {
            |    "benefit1": {"benefitId": "benefit1Id", "startDate": "2020-01-01"},
            |    "benefit2": {"benefitId": "benefit2Id", "startDate": "2020-01-01"}
            |  }
            |}
            |""".stripMargin).as[ListBenefitsResponse[HMRCStateBenefit, CustomerStateBenefit]] shouldBe
          ListBenefitsResponse(
            stateBenefits = Some(Seq(
              hmrcBenefit(benefitType = "benefit1", benefitId = "benefit1Id"),
              hmrcBenefit(benefitType = "benefit2", benefitId = "benefit2Id"),
            )),
            customerAddedStateBenefits = None)
      }

      "read any CustomerStateBenefits from nested arrays" in {
        Json.parse(
          """
            |{
            |  "customerAddedStateBenefits": {
            |    "benefit1": [
            |       {"benefitId": "benefit1Id1", "startDate": "2020-01-01"},
            |       {"benefitId": "benefit1Id2", "startDate": "2020-01-01"}
            |    ],
            |    "benefit2": [
            |       {"benefitId": "benefit2Id1", "startDate": "2020-01-01"},
            |       {"benefitId": "benefit2Id2", "startDate": "2020-01-01"}
            |    ]
            |  }
            |}
            |""".stripMargin).as[ListBenefitsResponse[HMRCStateBenefit, CustomerStateBenefit]] shouldBe
          ListBenefitsResponse(
            stateBenefits = None,
            customerAddedStateBenefits = Some(Seq(
              customerBenefit(benefitType = "benefit1", benefitId = "benefit1Id1"),
              customerBenefit(benefitType = "benefit1", benefitId = "benefit1Id2"),
              customerBenefit(benefitType = "benefit2", benefitId = "benefit2Id1"),
              customerBenefit(benefitType = "benefit2", benefitId = "benefit2Id2"),
            )))
      }

      "read any CustomerStateBenefits from nested objects" in {
        Json.parse(
          """
            |{
            |  "customerAddedStateBenefits": {
            |    "benefit1": {"benefitId": "benefit1Id", "startDate": "2020-01-01"},
            |    "benefit2": {"benefitId": "benefit2Id", "startDate": "2020-01-01"}
            |  }
            |}
            |""".stripMargin).as[ListBenefitsResponse[HMRCStateBenefit, CustomerStateBenefit]] shouldBe
          ListBenefitsResponse(
            stateBenefits = None,
            customerAddedStateBenefits = Some(Seq(
              customerBenefit(benefitType = "benefit1", benefitId = "benefit1Id"),
              customerBenefit(benefitType = "benefit2", benefitId = "benefit2Id"),
            )))
      }
    }

    "written to JSON" should {
      "produce the expected JsObject" in {

        Json.toJson(model) shouldBe mtdJson
      }
    }
  }
}
