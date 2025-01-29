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

package v2.fixtures

import play.api.libs.json.{JsValue, Json}
import shared.models.domain.{Nino, TaxYear, Timestamp}
import v2.listBenefits.model.request.ListBenefitsRequestData
import v2.listBenefits.model.response.{CustomerStateBenefit, HMRCStateBenefit, ListBenefitsResponse}
import v2.models.domain.BenefitId

object ListBenefitsFixture {

  val nino: String    = "AA123456A"
  val taxYear: String = "2020-21"

  val benefitId                      = "f0d83ac0-a10a-4d57-9e41-6d033832779f"
  val queryBenefitId: Option[String] = Some(benefitId)

  val correlationId: String = "X-123"

  val requestData: Option[String] => ListBenefitsRequestData = maybeBenefitId =>
    ListBenefitsRequestData(Nino(nino), TaxYear.fromMtd(taxYear), maybeBenefitId.map(BenefitId))

  val responseBody: JsValue = Json.parse(s"""
       |{
       |	"stateBenefits": [{
       |		"benefitType": "incapacityBenefit",
       |		"dateIgnored": "2019-04-04T01:01:01.000Z",
       |		"benefitId": "$benefitId",
       |		"startDate": "2020-01-01",
       |		"endDate": "2020-04-01",
       |		"amount": 2000,
       |		"taxPaid": 2132.22
       |	}],
       |	"customerAddedStateBenefits": [{
       |		"benefitType": "incapacityBenefit",
       |		"submittedOn": "2019-04-04T01:01:01.000Z",
       |		"benefitId": "f0d83ac0-a10a-4d57-9e41-6d033832779g",
       |		"startDate": "2020-01-01",
       |		"endDate": "2020-04-01",
       |		"amount": 2000,
       |		"taxPaid": 2132.22
       |	}]
       |}""".stripMargin)

  val singleRetrieveWithAmounts: JsValue = Json.parse("""
      |{
      |	"customerAddedStateBenefits": [{
      |		"benefitType": "incapacityBenefit",
      |		"submittedOn": "2019-04-04T01:01:01.000Z",
      |		"benefitId": "f0d83ac0-a10a-4d57-9e41-6d033832779g",
      |		"startDate": "2020-01-01",
      |		"endDate": "2020-04-01",
      |		"amount": 2000,
      |		"taxPaid": 2132.22
      |	}]
      |}""".stripMargin)

  val singleRetrieveWithAmountsBenefitId: JsValue = Json.parse(s"""
       |{
       |	"customerAddedStateBenefits": [{
       |		"benefitType": "incapacityBenefit",
       |		"submittedOn": "2019-04-04T01:01:01.000Z",
       |		"benefitId": "$benefitId",
       |		"startDate": "2020-01-01",
       |		"endDate": "2020-04-01",
       |		"amount": 2000,
       |		"taxPaid": 2132.22
       |	}]
       |}""".stripMargin)

  val singleRetrieveWithDuplicateBenefitId: JsValue = Json.parse(s"""
       |{
       |	"customerAddedStateBenefits": [{
       |		"benefitType": "incapacityBenefit",
       |		"submittedOn": "2019-04-04T01:01:01.000Z",
       |		"benefitId": "$benefitId",
       |		"startDate": "2020-01-01",
       |		"endDate": "2020-04-01",
       |		"amount": 2000,
       |		"taxPaid": 2132.22
       |	}]
       |}""".stripMargin)

  val responseBodyWithNoAmounts: JsValue = Json.parse(s"""
       |{
       |	"stateBenefits": [{
       |		"benefitType": "incapacityBenefit",
       |		"dateIgnored": "2019-04-04T01:01:01.000Z",
       |		"benefitId": "$benefitId",
       |		"startDate": "2020-01-01",
       |		"endDate": "2020-04-01"
       |  }],
       |  "customerAddedStateBenefits": [{
       |    "benefitType": "incapacityBenefit",
       |    "submittedOn": "2019-04-04T01:01:01.000Z",
       |    "benefitId": "f0d83ac0-a10a-4d57-9e41-6d033832779g",
       |    "startDate": "2020-01-01",
       |    "endDate": "2020-04-01"
       |    }]
       |}""".stripMargin)

  val responseBodyWithNoAmountsBenefitId: JsValue = Json.parse(s"""
       |{
       |	"stateBenefits": [{
       |		"benefitType": "incapacityBenefit",
       |		"dateIgnored": "2019-04-04T01:01:01.000Z",
       |		"benefitId": "$benefitId",
       |		"startDate": "2020-01-01",
       |		"endDate": "2020-04-01"
       |  }]
       |}""".stripMargin)

  val responseBodyWithoutDateIgnored: JsValue = Json.parse(s"""
       |{
       |	"stateBenefits": [{
       |		"benefitType": "incapacityBenefit",
       |		"benefitId": "$benefitId",
       |		"startDate": "2020-01-01",
       |		"endDate": "2020-04-01"
       |  }]
       |}""".stripMargin)

  val hmrcOnlyResponseBody: JsValue = Json.parse(s"""
       |{
       |	"stateBenefits": [{
       |		"benefitType": "incapacityBenefit",
       |		"dateIgnored": "2019-04-04T01:01:01.000Z",
       |		"benefitId": "$benefitId",
       |		"startDate": "2020-01-01",
       |		"endDate": "2020-04-01",
       |		"amount": 2000,
       |		"taxPaid": 2132.22
       |  }]
       |}""".stripMargin)

  val duplicateIdResponse: JsValue = Json.parse(s"""
       |{
       |	"stateBenefits": [{
       |		"benefitType": "incapacityBenefit",
       |		"dateIgnored": "2019-04-04T01:01:01.000Z",
       |		"benefitId": "$benefitId",
       |		"startDate": "2020-01-01",
       |		"endDate": "2020-04-01",
       |		"amount": 2000,
       |		"taxPaid": 2132.22
       |	}],
       |	"customerAddedStateBenefits": [{
       |		"benefitType": "incapacityBenefit",
       |		"submittedOn": "2019-04-04T01:01:01.000Z",
       |		"benefitId": "$benefitId",
       |		"startDate": "2020-01-01",
       |		"endDate": "2020-04-01",
       |		"amount": 2000,
       |		"taxPaid": 2132.22
       |	}]
       |}
       |""".stripMargin)

  val singleStateBenefitDesJson: JsValue = Json.parse(s"""
       |{
       |  "stateBenefits": {
       |    "incapacityBenefit": [
       |    {
       |      "dateIgnored": "2019-04-04T01:01:01.000Z",
       |      "benefitId": "$benefitId",
       |      "startDate": "2020-01-01",
       |      "endDate": "2020-04-01",
       |      "amount": 2000.00,
       |      "taxPaid": 2132.22
       |     }]
       |   }
       |}""".stripMargin)

  val singleStateBenefitDesJsonWithDuplicateId: JsValue = Json.parse(s"""
       |{
       |  "stateBenefits": {
       |    "incapacityBenefit": [
       |    {
       |      "dateIgnored": "2019-04-04T01:01:01.000Z",
       |      "benefitId": "$benefitId",
       |      "startDate": "2020-01-01",
       |      "endDate": "2020-04-01",
       |      "amount": 2000.00,
       |      "taxPaid": 2132.22
       |     }]
       |   },
       |   "customerAddedStateBenefits": {
       |    "incapacityBenefit": [
       |    {
       |      "submittedOn": "2019-04-04T01:01:01.000Z",
       |        "benefitId": "$benefitId",
       |        "startDate": "2020-01-01",
       |        "endDate": "2020-04-01",
       |        "amount": 2000.00,
       |        "taxPaid": 2132.22
       |     }]
       |   }
       |}""".stripMargin)

  val singleCustomerStateBenefitDesJson: JsValue = Json.parse(s"""
       |{
       |  "customerAddedStateBenefits": {
       |    "incapacityBenefit": [
       |    {
       |      "submittedOn": "2019-04-04T01:01:01.000Z",
       |        "benefitId": "$benefitId",
       |        "startDate": "2020-01-01",
       |        "endDate": "2020-04-01",
       |        "amount": 2000.00,
       |        "taxPaid": 2132.22
       |     }]
       |   }
       |}""".stripMargin)

  val ifsJsonWithNoAmounts: JsValue = Json.parse(s"""
       |{
       |  "stateBenefits": {
       |    "incapacityBenefit": [
       |    {
       |      "dateIgnored": "2019-04-04T01:01:01.000Z",
       |      "benefitId": "$benefitId",
       |      "startDate": "2020-01-01",
       |      "endDate": "2020-04-01"
       |     }]
       |   }
       |}""".stripMargin)

  val ifsJsonWithNoDateIgnored: JsValue = Json.parse(s"""
       |{
       |  "stateBenefits": {
       |    "incapacityBenefit": [
       |    {
       |      "benefitId": "$benefitId",
       |      "startDate": "2020-01-01",
       |      "endDate": "2020-04-01"
       |     }]
       |   }
       |}""".stripMargin)

  val ifsJson: JsValue = Json.parse(
    s"""
       |{
       |  "stateBenefits": {
       |    "incapacityBenefit": [
       |    {
       |      "dateIgnored": "2019-04-04T01:01:01.000Z",
       |      "benefitId": "$benefitId",
       |      "startDate": "2020-01-01",
       |      "endDate": "2020-04-01",
       |      "amount": 2000.00,
       |      "taxPaid": 2132.22
       |     },
       |     {
       |      "dateIgnored": "2019-04-04T01:01:01.000Z",
       |      "benefitId": "f0d83ac0-a10a-4d57-9e41-6d033832779g",
       |      "startDate": "2020-03-01",
       |      "endDate": "2020-04-01",
       |      "amount": 1000.00
       |     }
       |    ],
       |    "statePension": {
       |      "dateIgnored": "2019-04-04T01:01:01.000Z",
       |      "benefitId": "$benefitId",
       |      "startDate": "2019-01-01",
       |      "amount": 2000.00
       |    },
       |    "statePensionLumpSum": {
       |      "dateIgnored": "2019-04-04T01:01:01.000Z",
       |      "benefitId": "$benefitId",
       |      "startDate": "2019-01-01",
       |      "endDate"  : "2019-01-01",
       |      "amount": 2000.00,
       |      "taxPaid": 2132.22
       |    },
       |    "employmentSupportAllowance": [
       |      {
       |        "dateIgnored": "2019-04-04T01:01:01.000Z",
       |        "benefitId": "$benefitId",
       |        "startDate": "2020-01-01",
       |        "endDate": "2020-04-01",
       |        "amount": 2000.00,
       |        "taxPaid": 2132.22
       |      },
       |      {
       |        "dateIgnored": "2019-04-04T01:01:01.000Z",
       |        "benefitId": "f0d83ac0-a10a-4d57-9e41-6d033832779g",
       |        "startDate": "2020-01-01",
       |        "endDate": "2020-04-01",
       |        "amount": 1000.00
       |      }
       |    ],
       |    "jobSeekersAllowance": [
       |      {
       |        "dateIgnored": "2019-04-04T01:01:01.000Z",
       |        "benefitId": "$benefitId",
       |        "startDate": "2020-01-01",
       |        "endDate": "2020-04-01",
       |        "amount": 2000.00,
       |        "taxPaid": 2132.22
       |      },
       |      {
       |        "dateIgnored": "2019-04-04T01:01:01.000Z",
       |        "benefitId": "f0d83ac0-a10a-4d57-9e41-6d033832779g",
       |        "startDate": "2020-01-01",
       |        "endDate": "2020-04-01",
       |        "amount": 1000.00
       |      }
       |    ],
       |    "bereavementAllowance": {
       |      "dateIgnored": "2019-04-04T01:01:01.000Z",
       |      "benefitId": "$benefitId",
       |      "startDate": "2020-01-01",
       |      "endDate": "2020-04-01",
       |      "amount": 2000.00
       |    },
       |    "otherStateBenefits": {
       |      "dateIgnored": "2019-04-04T01:01:01.000Z",
       |      "benefitId": "$benefitId",
       |      "startDate": "2020-01-01",
       |      "endDate": "2020-04-01",
       |      "amount": 2000.00
       |    }
       |  },
       |  "customerAddedStateBenefits": {
       |    "incapacityBenefit": [
       |      {
       |        "dateIgnored": "2019-04-04T01:01:01.000Z",
       |        "submittedOn": "2019-04-04T01:01:01.000Z",
       |        "benefitId": "$benefitId",
       |        "startDate": "2020-01-01",
       |        "endDate": "2020-04-01",
       |        "amount": 2000.00,
       |        "taxPaid": 2132.22
       |      },
       |      {
       |        "dateIgnored": "2019-04-04T01:01:01.000Z",
       |        "submittedOn": "2019-04-04T01:01:01.000Z",
       |        "benefitId": "f0d83ac0-a10a-4d57-9e41-6d033832779g",
       |        "startDate": "2020-03-01",
       |        "endDate": "2020-04-01",
       |        "amount": 1000.00
       |      }
       |    ],
       |    "statePension": [{
       |      "dateIgnored": "2019-04-04T01:01:01.000Z",
       |      "submittedOn": "2019-04-04T01:01:01.000Z",
       |      "benefitId": "$benefitId",
       |      "startDate": "2019-01-01",
       |      "amount": 2000.00
       |    }],
       |    "statePensionLumpSum": [{
       |      "dateIgnored": "2019-04-04T01:01:01.000Z",
       |      "submittedOn": "2019-04-04T01:01:01.000Z",
       |      "benefitId": "$benefitId",
       |      "startDate": "2019-01-01",
       |      "endDate" : "2019-01-01",
       |      "amount": 2000.00,
       |      "taxPaid": 2132.22
       |    }],
       |    "employmentSupportAllowance": [
       |      {
       |        "dateIgnored": "2019-04-04T01:01:01.000Z",
       |        "submittedOn": "2019-04-04T01:01:01.000Z",
       |        "benefitId": "$benefitId",
       |        "startDate": "2020-01-01",
       |        "endDate": "2020-04-01",
       |        "amount": 2000.00,
       |        "taxPaid": 2132.22
       |      }
       |    ],
       |    "jobSeekersAllowance": [
       |      {
       |        "dateIgnored": "2019-04-04T01:01:01.000Z",
       |        "submittedOn": "2019-04-04T01:01:01.000Z",
       |        "benefitId": "$benefitId",
       |        "startDate": "2020-01-01",
       |        "endDate": "2020-04-01",
       |        "amount": 2000.00,
       |        "taxPaid": 2132.22
       |      }
       |    ],
       |    "bereavementAllowance": [{
       |      "dateIgnored": "2019-04-04T01:01:01.000Z",
       |      "submittedOn": "2019-04-04T01:01:01.000Z",
       |      "benefitId": "$benefitId",
       |      "startDate": "2020-01-01",
       |      "endDate": "2020-04-01",
       |      "amount": 2000.00
       |    }],
       |    "otherStateBenefits": [{
       |      "dateIgnored": "2019-04-04T01:01:01.000Z",
       |      "submittedOn": "2019-04-04T01:01:01.000Z",
       |      "benefitId": "$benefitId",
       |      "startDate": "2020-01-01",
       |      "endDate": "2020-04-01",
       |      "amount": 2000.00
       |    }]
       |  }
       |}""".stripMargin
  )

  def mtdJson(taxYear: String): JsValue = Json.parse(s"""
       |{
       |	"stateBenefits": [{
       |		"benefitType": "incapacityBenefit",
       |		"dateIgnored": "2019-04-04T01:01:01.000Z",
       |		"benefitId": "$benefitId",
       |		"startDate": "2020-01-01",
       |		"endDate": "2020-04-01",
       |		"amount": 2000,
       |		"taxPaid": 2132.22
       |	}, {
       |		"benefitType": "incapacityBenefit",
       |		"dateIgnored": "2019-04-04T01:01:01.000Z",
       |		"benefitId": "f0d83ac0-a10a-4d57-9e41-6d033832779g",
       |		"startDate": "2020-03-01",
       |		"endDate": "2020-04-01",
       |		"amount": 1000
       |	}, {
       |		"benefitType": "statePension",
       |		"dateIgnored": "2019-04-04T01:01:01.000Z",
       |		"benefitId": "$benefitId",
       |		"startDate": "2019-01-01",
       |		"amount": 2000
       |	}, {
       |		"benefitType": "statePensionLumpSum",
       |		"dateIgnored": "2019-04-04T01:01:01.000Z",
       |		"benefitId": "$benefitId",
       |		"startDate": "2019-01-01",
       |		"endDate": "2019-01-01",
       |		"amount": 2000,
       |		"taxPaid": 2132.22
       |	}, {
       |		"benefitType": "employmentSupportAllowance",
       |		"dateIgnored": "2019-04-04T01:01:01.000Z",
       |		"benefitId": "$benefitId",
       |		"startDate": "2020-01-01",
       |		"endDate": "2020-04-01",
       |		"amount": 2000,
       |		"taxPaid": 2132.22
       |	}, {
       |		"benefitType": "employmentSupportAllowance",
       |		"dateIgnored": "2019-04-04T01:01:01.000Z",
       |		"benefitId": "f0d83ac0-a10a-4d57-9e41-6d033832779g",
       |		"startDate": "2020-01-01",
       |		"endDate": "2020-04-01",
       |		"amount": 1000
       |	}, {
       |		"benefitType": "jobSeekersAllowance",
       |		"dateIgnored": "2019-04-04T01:01:01.000Z",
       |		"benefitId": "$benefitId",
       |		"startDate": "2020-01-01",
       |		"endDate": "2020-04-01",
       |		"amount": 2000,
       |		"taxPaid": 2132.22
       |	}, {
       |		"benefitType": "jobSeekersAllowance",
       |		"dateIgnored": "2019-04-04T01:01:01.000Z",
       |		"benefitId": "f0d83ac0-a10a-4d57-9e41-6d033832779g",
       |		"startDate": "2020-01-01",
       |		"endDate": "2020-04-01",
       |		"amount": 1000
       |	}, {
       |		"benefitType": "bereavementAllowance",
       |		"dateIgnored": "2019-04-04T01:01:01.000Z",
       |		"benefitId": "$benefitId",
       |		"startDate": "2020-01-01",
       |		"endDate": "2020-04-01",
       |		"amount": 2000
       |	}, {
       |		"benefitType": "otherStateBenefits",
       |		"dateIgnored": "2019-04-04T01:01:01.000Z",
       |		"benefitId": "$benefitId",
       |		"startDate": "2020-01-01",
       |		"endDate": "2020-04-01",
       |		"amount": 2000
       |	}],
       |	"customerAddedStateBenefits": [{
       |		"benefitType": "incapacityBenefit",
       |		"submittedOn": "2019-04-04T01:01:01.000Z",
       |		"benefitId": "$benefitId",
       |		"startDate": "2020-01-01",
       |		"endDate": "2020-04-01",
       |		"amount": 2000,
       |		"taxPaid": 2132.22
       |	}, {
       |		"benefitType": "incapacityBenefit",
       |		"submittedOn": "2019-04-04T01:01:01.000Z",
       |		"benefitId": "f0d83ac0-a10a-4d57-9e41-6d033832779g",
       |		"startDate": "2020-03-01",
       |		"endDate": "2020-04-01",
       |		"amount": 1000
       |	}, {
       |		"benefitType": "statePension",
       |		"submittedOn": "2019-04-04T01:01:01.000Z",
       |		"benefitId": "$benefitId",
       |		"startDate": "2019-01-01",
       |		"amount": 2000
       |	}, {
       |		"benefitType": "statePensionLumpSum",
       |		"submittedOn": "2019-04-04T01:01:01.000Z",
       |		"benefitId": "$benefitId",
       |		"startDate": "2019-01-01",
       |		"endDate": "2019-01-01",
       |		"amount": 2000,
       |		"taxPaid": 2132.22
       |	}, {
       |		"benefitType": "employmentSupportAllowance",
       |		"submittedOn": "2019-04-04T01:01:01.000Z",
       |		"benefitId": "$benefitId",
       |		"startDate": "2020-01-01",
       |		"endDate": "2020-04-01",
       |		"amount": 2000,
       |		"taxPaid": 2132.22
       |	}, {
       |		"benefitType": "jobSeekersAllowance",
       |		"submittedOn": "2019-04-04T01:01:01.000Z",
       |		"benefitId": "$benefitId",
       |		"startDate": "2020-01-01",
       |		"endDate": "2020-04-01",
       |		"amount": 2000,
       |		"taxPaid": 2132.22
       |	}, {
       |		"benefitType": "bereavementAllowance",
       |		"submittedOn": "2019-04-04T01:01:01.000Z",
       |		"benefitId": "$benefitId",
       |		"startDate": "2020-01-01",
       |		"endDate": "2020-04-01",
       |		"amount": 2000
       |	}, {
       |		"benefitType": "otherStateBenefits",
       |		"submittedOn": "2019-04-04T01:01:01.000Z",
       |		"benefitId": "$benefitId",
       |		"startDate": "2020-01-01",
       |		"endDate": "2020-04-01",
       |		"amount": 2000
       |	}]
       |}
       |""".stripMargin)

  val stateBenefits: HMRCStateBenefit = HMRCStateBenefit(
    benefitType = "incapacityBenefit",
    dateIgnored = Some(Timestamp("2019-04-04T01:01:01.000Z")),
    benefitId = s"$benefitId",
    startDate = "2020-01-01",
    endDate = Some("2020-04-01"),
    amount = Some(2000.00),
    taxPaid = Some(2132.22),
    submittedOn = None
  )

  val customerAddedStateBenefits: CustomerStateBenefit = CustomerStateBenefit(
    benefitType = "incapacityBenefit",
    benefitId = "f0d83ac0-a10a-4d57-9e41-6d033832779g",
    startDate = "2020-01-01",
    endDate = Some("2020-04-01"),
    amount = Some(2000.00),
    taxPaid = Some(2132.22),
    submittedOn = Some(Timestamp("2019-04-04T01:01:01.000Z"))
  )

  val responseData: ListBenefitsResponse[HMRCStateBenefit, CustomerStateBenefit] = ListBenefitsResponse(
    stateBenefits = Some(Seq(stateBenefits)),
    customerAddedStateBenefits = Some(Seq(customerAddedStateBenefits))
  )

  val responseDataWithNoAmounts: ListBenefitsResponse[HMRCStateBenefit, CustomerStateBenefit] = ListBenefitsResponse(
    stateBenefits = Some(Seq(stateBenefits.copy(amount = None, taxPaid = None))),
    customerAddedStateBenefits = Some(Seq(customerAddedStateBenefits.copy(amount = None, taxPaid = None)))
  )

  val listBenefitsResponse: ListBenefitsResponse[HMRCStateBenefit, CustomerStateBenefit] = ListBenefitsResponse(
    stateBenefits = Some(Seq(stateBenefits)),
    customerAddedStateBenefits = Some(Seq(customerAddedStateBenefits))
  )
}
