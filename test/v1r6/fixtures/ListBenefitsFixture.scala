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

package v1r6.fixtures

import play.api.libs.json.{JsValue, Json}
import v1r6.models.domain.Nino
import v1r6.models.hateoas.Method.{DELETE, GET, POST, PUT}
import v1r6.models.hateoas.{HateoasWrapper, Link}
import v1r6.models.request.listBenefits.{ListBenefitsRawData, ListBenefitsRequest}
import v1r6.models.response.listBenefits.{CustomerStateBenefit, HMRCStateBenefit, ListBenefitsResponse}

object ListBenefitsFixture {

  val nino: String = "AA123456A"
  val taxYear: String = "2020-21"

  val benefitId = "f0d83ac0-a10a-4d57-9e41-6d033832779f"
  val queryBenefitId: Option[String] = Some(benefitId)

  val correlationId: String = "X-123"

  val rawData: Option[String] => ListBenefitsRawData = reqBenefitId => ListBenefitsRawData(
    nino = nino,
    taxYear = taxYear,
    benefitId = reqBenefitId
  )

  val requestData: Option[String] => ListBenefitsRequest = reqBenefitId => ListBenefitsRequest(
    nino = Nino(nino),
    taxYear = taxYear,
    benefitId = reqBenefitId
  )

  val hateosJson: JsValue = Json.parse(
    s"""
       |{
       |  "links": [
       |    {
       |      "href": "/individuals/state-benefits/$nino/$taxYear",
       |      "method": "GET",
       |      "rel": "self"
       |    },
       |    {
       |      "href": "/individuals/state-benefits/$nino/$taxYear",
       |      "method": "POST",
       |      "rel": "create-state-benefit"
       |    }
       |  ]
       |}
    """.stripMargin
  )

  val responseBody: JsValue = Json.parse(
    s"""
      |{
      |	"stateBenefits": [{
      |		"benefitType": "incapacityBenefit",
      |		"dateIgnored": "2019-04-04T01:01:01Z",
      |		"benefitId": "$benefitId",
      |		"startDate": "2020-01-01",
      |		"endDate": "2020-04-01",
      |		"amount": 2000,
      |		"taxPaid": 2132.22,
      |		"links": [{
      |			"href": "/individuals/state-benefits/AA123456A/2020-21?benefitId=$benefitId",
      |			"method": "GET",
      |			"rel": "self"
      |		}]
      |	}],
      |	"customerAddedStateBenefits": [{
      |		"benefitType": "incapacityBenefit",
      |		"submittedOn": "2019-04-04T01:01:01Z",
      |		"benefitId": "f0d83ac0-a10a-4d57-9e41-6d033832779g",
      |		"startDate": "2020-01-01",
      |		"endDate": "2020-04-01",
      |		"amount": 2000,
      |		"taxPaid": 2132.22,
      |		"links": [{
      |			"href": "/individuals/state-benefits/AA123456A/2020-21?benefitId=$benefitId",
      |			"method": "GET",
      |			"rel": "self"
      |		}]
      |	}],
      |	"links": [{
      |		"href": "/individuals/state-benefits/AA123456A/2020-21",
      |		"method": "POST",
      |		"rel": "create-state-benefit"
      |	}, {
      |		"href": "/individuals/state-benefits/AA123456A/2020-21",
      |		"method": "GET",
      |		"rel": "self"
      |	}]
      |}""".stripMargin)

  val singleRetrieveWithAmounts: JsValue = Json.parse(
    """
      |{
      |	"customerAddedStateBenefits": [{
      |		"benefitType": "incapacityBenefit",
      |		"submittedOn": "2019-04-04T01:01:01Z",
      |		"benefitId": "f0d83ac0-a10a-4d57-9e41-6d033832779g",
      |		"startDate": "2020-01-01",
      |		"endDate": "2020-04-01",
      |		"amount": 2000,
      |		"taxPaid": 2132.22,
      |		"links": [{
      |			"href": "/individuals/state-benefits/AA123456A/2020-21?benefitId=f0d83ac0-a10a-4d57-9e41-6d033832779g",
      |			"method": "GET",
      |			"rel": "self"
      |		}, {
      |			"href": "/individuals/state-benefits/AA123456A/2020-21/f0d83ac0-a10a-4d57-9e41-6d033832779g/amounts",
      |			"method": "PUT",
      |			"rel": "amend-state-benefit-amounts"
      |		}, {
      |			"href": "/individuals/state-benefits/AA123456A/2020-21/f0d83ac0-a10a-4d57-9e41-6d033832779g/amounts",
      |			"method": "DELETE",
      |			"rel": "delete-state-benefit-amounts"
      |		}, {
      |			"href": "/individuals/state-benefits/AA123456A/2020-21/f0d83ac0-a10a-4d57-9e41-6d033832779g",
      |			"method": "DELETE",
      |			"rel": "delete-state-benefit"
      |		}, {
      |			"href": "/individuals/state-benefits/AA123456A/2020-21/f0d83ac0-a10a-4d57-9e41-6d033832779g",
      |			"method": "PUT",
      |			"rel": "amend-state-benefit"
      |		}]
      |	}],
      |	"links": [{
      |		"href": "/individuals/state-benefits/AA123456A/2020-21",
      |		"method": "POST",
      |		"rel": "create-state-benefit"
      |	}, {
      |		"href": "/individuals/state-benefits/AA123456A/2020-21",
      |		"method": "GET",
      |		"rel": "self"
      |	}]
      |}""".stripMargin)

  val singleRetrieveWithAmountsBenefitId: JsValue = Json.parse(
    s"""
      |{
      |	"customerAddedStateBenefits": [{
      |		"benefitType": "incapacityBenefit",
      |		"submittedOn": "2019-04-04T01:01:01Z",
      |		"benefitId": "$benefitId",
      |		"startDate": "2020-01-01",
      |		"endDate": "2020-04-01",
      |		"amount": 2000,
      |		"taxPaid": 2132.22,
      |		"links": [{
      |			"href": "/individuals/state-benefits/AA123456A/2020-21?benefitId=$benefitId",
      |			"method": "GET",
      |			"rel": "self"
      |		}, {
      |			"href": "/individuals/state-benefits/AA123456A/2020-21/$benefitId/amounts",
      |			"method": "PUT",
      |			"rel": "amend-state-benefit-amounts"
      |		}, {
      |			"href": "/individuals/state-benefits/AA123456A/2020-21/$benefitId/amounts",
      |			"method": "DELETE",
      |			"rel": "delete-state-benefit-amounts"
      |		}, {
      |			"href": "/individuals/state-benefits/AA123456A/2020-21/$benefitId",
      |			"method": "DELETE",
      |			"rel": "delete-state-benefit"
      |		}, {
      |			"href": "/individuals/state-benefits/AA123456A/2020-21/$benefitId",
      |			"method": "PUT",
      |			"rel": "amend-state-benefit"
      |		}]
      |	}],
      |	"links": [{
      |		"href": "/individuals/state-benefits/AA123456A/2020-21",
      |		"method": "POST",
      |		"rel": "create-state-benefit"
      |	}, {
      |		"href": "/individuals/state-benefits/AA123456A/2020-21",
      |		"method": "GET",
      |		"rel": "self"
      |	}]
      |}""".stripMargin)

  val singleRetrieveWithDuplicateBenefitId: JsValue = Json.parse(
    s"""
      |{
      |	"customerAddedStateBenefits": [{
      |		"benefitType": "incapacityBenefit",
      |		"submittedOn": "2019-04-04T01:01:01Z",
      |		"benefitId": "$benefitId",
      |		"startDate": "2020-01-01",
      |		"endDate": "2020-04-01",
      |		"amount": 2000,
      |		"taxPaid": 2132.22,
      |		"links": [{
      |			"href": "/individuals/state-benefits/AA123456A/2020-21?benefitId=$benefitId",
      |			"method": "GET",
      |			"rel": "self"
      |		}, {
      |			"href": "/individuals/state-benefits/AA123456A/2020-21/$benefitId/amounts",
      |			"method": "PUT",
      |			"rel": "amend-state-benefit-amounts"
      |		}, {
      |			"href": "/individuals/state-benefits/AA123456A/2020-21/$benefitId/amounts",
      |			"method": "DELETE",
      |			"rel": "delete-state-benefit-amounts"
      |		}, {
      |			"href": "/individuals/state-benefits/AA123456A/2020-21/$benefitId",
      |			"method": "DELETE",
      |			"rel": "delete-state-benefit"
      |		}, {
      |			"href": "/individuals/state-benefits/AA123456A/2020-21/$benefitId",
      |			"method": "PUT",
      |			"rel": "amend-state-benefit"
      |		}]
      |	}],
      |	"links": [{
      |		"href": "/individuals/state-benefits/AA123456A/2020-21",
      |		"method": "POST",
      |		"rel": "create-state-benefit"
      |	}, {
      |		"href": "/individuals/state-benefits/AA123456A/2020-21",
      |		"method": "GET",
      |		"rel": "self"
      |	}]
      |}""".stripMargin)

  val responseBodyWithNoAmounts: JsValue = Json.parse(
    s"""
      |{
      |	"stateBenefits": [{
      |		"benefitType": "incapacityBenefit",
      |		"dateIgnored": "2019-04-04T01:01:01Z",
      |		"benefitId": "$benefitId",
      |		"startDate": "2020-01-01",
      |		"endDate": "2020-04-01",
      |		"links": [{
      |			"href": "/individuals/state-benefits/AA123456A/2020-21?benefitId=$benefitId",
      |			"method": "GET",
      |			"rel": "self"
      |		}, {
      |			"href": "/individuals/state-benefits/AA123456A/2020-21/$benefitId/amounts",
      |			"method": "PUT",
      |			"rel": "amend-state-benefit-amounts"
      |		}, {
      |			"href": "/individuals/state-benefits/AA123456A/2020-21/$benefitId/ignore",
      |			"method": "POST",
      |			"rel": "ignore-state-benefit"
      |		}]
      |	}],
      |	"links": [{
      |		"href": "/individuals/state-benefits/AA123456A/2020-21",
      |		"method": "POST",
      |		"rel": "create-state-benefit"
      |	}, {
      |		"href": "/individuals/state-benefits/AA123456A/2020-21",
      |		"method": "GET",
      |		"rel": "self"
      |	}]
      |}""".stripMargin)

  val responseBodyWithNoAmountsBenefitId: JsValue = Json.parse(
    s"""
      |{
      |	"stateBenefits": [{
      |		"benefitType": "incapacityBenefit",
      |		"dateIgnored": "2019-04-04T01:01:01Z",
      |		"benefitId": "$benefitId",
      |		"startDate": "2020-01-01",
      |		"endDate": "2020-04-01",
      |		"links": [{
      |			"href": "/individuals/state-benefits/AA123456A/2020-21?benefitId=$benefitId",
      |			"method": "GET",
      |			"rel": "self"
      |		}, {
      |			"href": "/individuals/state-benefits/AA123456A/2020-21/$benefitId/amounts",
      |			"method": "PUT",
      |			"rel": "amend-state-benefit-amounts"
      |		}, {
      |			"href": "/individuals/state-benefits/AA123456A/2020-21/$benefitId/unignore",
      |			"method": "POST",
      |			"rel": "unignore-state-benefit"
      |		}]
      |	}],
      |	"links": [{
      |		"href": "/individuals/state-benefits/AA123456A/2020-21",
      |		"method": "POST",
      |		"rel": "create-state-benefit"
      |	}, {
      |		"href": "/individuals/state-benefits/AA123456A/2020-21",
      |		"method": "GET",
      |		"rel": "self"
      |	}]
      |}""".stripMargin)

  val responseBodyWithoutDateIgnored: JsValue = Json.parse(
    s"""
      |{
      |	"stateBenefits": [{
      |		"benefitType": "incapacityBenefit",
      |		"benefitId": "$benefitId",
      |		"startDate": "2020-01-01",
      |		"endDate": "2020-04-01",
      |		"links": [{
      |			"href": "/individuals/state-benefits/AA123456A/2020-21?benefitId=$benefitId",
      |			"method": "GET",
      |			"rel": "self"
      |		}, {
      |			"href": "/individuals/state-benefits/AA123456A/2020-21/$benefitId/amounts",
      |			"method": "PUT",
      |			"rel": "amend-state-benefit-amounts"
      |		}, {
      |			"href": "/individuals/state-benefits/AA123456A/2020-21/$benefitId/ignore",
      |			"method": "POST",
      |			"rel": "ignore-state-benefit"
      |		}]
      |	}],
      |	"links": [{
      |		"href": "/individuals/state-benefits/AA123456A/2020-21",
      |		"method": "POST",
      |		"rel": "create-state-benefit"
      |	}, {
      |		"href": "/individuals/state-benefits/AA123456A/2020-21",
      |		"method": "GET",
      |		"rel": "self"
      |	}]
      |}""".stripMargin)

  val hmrcOnlyResponseBody: JsValue = Json.parse(
    s"""
      |{
      |	"stateBenefits": [{
      |		"benefitType": "incapacityBenefit",
      |		"dateIgnored": "2019-04-04T01:01:01Z",
      |		"benefitId": "$benefitId",
      |		"startDate": "2020-01-01",
      |		"endDate": "2020-04-01",
      |		"amount": 2000,
      |		"taxPaid": 2132.22,
      |		"links": [{
      |			"href": "/individuals/state-benefits/AA123456A/2020-21?benefitId=$benefitId",
      |			"method": "GET",
      |			"rel": "self"
      |		}]
      |	}],
      |	"links": [{
      |		"href": "/individuals/state-benefits/AA123456A/2020-21",
      |		"method": "POST",
      |		"rel": "create-state-benefit"
      |	}, {
      |		"href": "/individuals/state-benefits/AA123456A/2020-21",
      |		"method": "GET",
      |		"rel": "self"
      |	}]
      |}""".stripMargin)

  val duplicateIdResponse: JsValue = Json.parse(
    s"""
      |{
      |	"stateBenefits": [{
      |		"benefitType": "incapacityBenefit",
      |		"dateIgnored": "2019-04-04T01:01:01Z",
      |		"benefitId": "$benefitId",
      |		"startDate": "2020-01-01",
      |		"endDate": "2020-04-01",
      |		"amount": 2000,
      |		"taxPaid": 2132.22,
      |		"links": [{
      |			"href": "/individuals/state-benefits/AA123456A/2020-21?benefitId=$benefitId",
      |			"method": "GET",
      |			"rel": "self"
      |		}, {
      |			"href": "/individuals/state-benefits/AA123456A/2020-21/$benefitId/amounts",
      |			"method": "PUT",
      |			"rel": "amend-state-benefit-amounts"
      |		}, {
      |			"href": "/individuals/state-benefits/AA123456A/2020-21/$benefitId/unignore",
      |			"method": "POST",
      |			"rel": "unignore-state-benefit"
      |		}]
      |	}],
      |	"customerAddedStateBenefits": [{
      |		"benefitType": "incapacityBenefit",
      |		"submittedOn": "2019-04-04T01:01:01Z",
      |		"benefitId": "$benefitId",
      |		"startDate": "2020-01-01",
      |		"endDate": "2020-04-01",
      |		"amount": 2000,
      |		"taxPaid": 2132.22,
      |		"links": [{
      |			"href": "/individuals/state-benefits/AA123456A/2020-21?benefitId=$benefitId",
      |			"method": "GET",
      |			"rel": "self"
      |		}, {
      |			"href": "/individuals/state-benefits/AA123456A/2020-21/$benefitId/amounts",
      |			"method": "PUT",
      |			"rel": "amend-state-benefit-amounts"
      |		}, {
      |			"href": "/individuals/state-benefits/AA123456A/2020-21/$benefitId/amounts",
      |			"method": "DELETE",
      |			"rel": "delete-state-benefit-amounts"
      |		}]
      |	}],
      |	"links": [{
      |		"href": "/individuals/state-benefits/AA123456A/2020-21",
      |		"method": "POST",
      |		"rel": "create-state-benefit"
      |	}, {
      |		"href": "/individuals/state-benefits/AA123456A/2020-21",
      |		"method": "GET",
      |		"rel": "self"
      |	}]
      |}
      |""".stripMargin)

  val singleStateBenefitDesJson: JsValue = Json.parse(
    s"""
      |{
      |  "stateBenefits": {
      |    "incapacityBenefit": [
      |    {
      |      "dateIgnored": "2019-04-04T01:01:01Z",
      |      "benefitId": "$benefitId",
      |      "startDate": "2020-01-01",
      |      "endDate": "2020-04-01",
      |      "amount": 2000.00,
      |      "taxPaid": 2132.22
      |     }]
      |   }
      |}""".stripMargin)

  val singleStateBenefitDesJsonWithDuplicateId: JsValue = Json.parse(
    s"""
      |{
      |  "stateBenefits": {
      |    "incapacityBenefit": [
      |    {
      |      "dateIgnored": "2019-04-04T01:01:01Z",
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
      |      "submittedOn": "2019-04-04T01:01:01Z",
      |        "benefitId": "$benefitId",
      |        "startDate": "2020-01-01",
      |        "endDate": "2020-04-01",
      |        "amount": 2000.00,
      |        "taxPaid": 2132.22
      |     }]
      |   }
      |}""".stripMargin)

  val singleCustomerStateBenefitDesJson: JsValue = Json.parse(
    s"""
      |{
      |  "customerAddedStateBenefits": {
      |    "incapacityBenefit": [
      |    {
      |      "submittedOn": "2019-04-04T01:01:01Z",
      |        "benefitId": "$benefitId",
      |        "startDate": "2020-01-01",
      |        "endDate": "2020-04-01",
      |        "amount": 2000.00,
      |        "taxPaid": 2132.22
      |     }]
      |   }
      |}""".stripMargin)

  val desJsonWithNoAmounts: JsValue = Json.parse(
    s"""
      |{
      |  "stateBenefits": {
      |    "incapacityBenefit": [
      |    {
      |      "dateIgnored": "2019-04-04T01:01:01Z",
      |      "benefitId": "$benefitId",
      |      "startDate": "2020-01-01",
      |      "endDate": "2020-04-01"
      |     }]
      |   }
      |}""".stripMargin)

  val desJsonWithNoDateIgnored: JsValue = Json.parse(
    s"""
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

  val desJson: JsValue = Json.parse(
    s"""
      |{
      |  "stateBenefits": {
      |    "incapacityBenefit": [
      |    {
      |      "dateIgnored": "2019-04-04T01:01:01Z",
      |      "benefitId": "$benefitId",
      |      "startDate": "2020-01-01",
      |      "endDate": "2020-04-01",
      |      "amount": 2000.00,
      |      "taxPaid": 2132.22
      |     },
      |     {
      |      "dateIgnored": "2019-04-04T01:01:01Z",
      |      "benefitId": "f0d83ac0-a10a-4d57-9e41-6d033832779g",
      |      "startDate": "2020-03-01",
      |      "endDate": "2020-04-01",
      |      "amount": 1000.00
      |     }
      |    ],
      |    "statePension": {
      |      "dateIgnored": "2019-04-04T01:01:01Z",
      |      "benefitId": "$benefitId",
      |      "startDate": "2019-01-01",
      |      "amount": 2000.00
      |    },
      |    "statePensionLumpSum": {
      |      "dateIgnored": "2019-04-04T01:01:01Z",
      |      "benefitId": "$benefitId",
      |      "startDate": "2019-01-01",
      |      "endDate"  : "2019-01-01",
      |      "amount": 2000.00,
      |      "taxPaid": 2132.22
      |    },
      |    "employmentSupportAllowance": [
      |      {
      |        "dateIgnored": "2019-04-04T01:01:01Z",
      |        "benefitId": "$benefitId",
      |        "startDate": "2020-01-01",
      |        "endDate": "2020-04-01",
      |        "amount": 2000.00,
      |        "taxPaid": 2132.22
      |      },
      |      {
      |        "dateIgnored": "2019-04-04T01:01:01Z",
      |        "benefitId": "f0d83ac0-a10a-4d57-9e41-6d033832779g",
      |        "startDate": "2020-01-01",
      |        "endDate": "2020-04-01",
      |        "amount": 1000.00
      |      }
      |    ],
      |    "jobSeekersAllowance": [
      |      {
      |        "dateIgnored": "2019-04-04T01:01:01Z",
      |        "benefitId": "$benefitId",
      |        "startDate": "2020-01-01",
      |        "endDate": "2020-04-01",
      |        "amount": 2000.00,
      |        "taxPaid": 2132.22
      |      },
      |      {
      |        "dateIgnored": "2019-04-04T01:01:01Z",
      |        "benefitId": "f0d83ac0-a10a-4d57-9e41-6d033832779g",
      |        "startDate": "2020-01-01",
      |        "endDate": "2020-04-01",
      |        "amount": 1000.00
      |      }
      |    ],
      |    "bereavementAllowance": {
      |      "dateIgnored": "2019-04-04T01:01:01Z",
      |      "benefitId": "$benefitId",
      |      "startDate": "2020-01-01",
      |      "endDate": "2020-04-01",
      |      "amount": 2000.00
      |    },
      |    "otherStateBenefits": {
      |      "dateIgnored": "2019-04-04T01:01:01Z",
      |      "benefitId": "$benefitId",
      |      "startDate": "2020-01-01",
      |      "endDate": "2020-04-01",
      |      "amount": 2000.00
      |    }
      |  },
      |  "customerAddedStateBenefits": {
      |    "incapacityBenefit": [
      |      {
      |        "dateIgnored": "2019-04-04T01:01:01Z",
      |        "submittedOn": "2019-04-04T01:01:01Z",
      |        "benefitId": "$benefitId",
      |        "startDate": "2020-01-01",
      |        "endDate": "2020-04-01",
      |        "amount": 2000.00,
      |        "taxPaid": 2132.22
      |      },
      |      {
      |        "dateIgnored": "2019-04-04T01:01:01Z",
      |        "submittedOn": "2019-04-04T01:01:01Z",
      |        "benefitId": "f0d83ac0-a10a-4d57-9e41-6d033832779g",
      |        "startDate": "2020-03-01",
      |        "endDate": "2020-04-01",
      |        "amount": 1000.00
      |      }
      |    ],
      |    "statePension": [{
      |      "dateIgnored": "2019-04-04T01:01:01Z",
      |      "submittedOn": "2019-04-04T01:01:01Z",
      |      "benefitId": "$benefitId",
      |      "startDate": "2019-01-01",
      |      "amount": 2000.00
      |    }],
      |    "statePensionLumpSum": [{
      |      "dateIgnored": "2019-04-04T01:01:01Z",
      |      "submittedOn": "2019-04-04T01:01:01Z",
      |      "benefitId": "$benefitId",
      |      "startDate": "2019-01-01",
      |      "endDate" : "2019-01-01",
      |      "amount": 2000.00,
      |      "taxPaid": 2132.22
      |    }],
      |    "employmentSupportAllowance": [
      |      {
      |        "dateIgnored": "2019-04-04T01:01:01Z",
      |        "submittedOn": "2019-04-04T01:01:01Z",
      |        "benefitId": "$benefitId",
      |        "startDate": "2020-01-01",
      |        "endDate": "2020-04-01",
      |        "amount": 2000.00,
      |        "taxPaid": 2132.22
      |      }
      |    ],
      |    "jobSeekersAllowance": [
      |      {
      |        "dateIgnored": "2019-04-04T01:01:01Z",
      |        "submittedOn": "2019-04-04T01:01:01Z",
      |        "benefitId": "$benefitId",
      |        "startDate": "2020-01-01",
      |        "endDate": "2020-04-01",
      |        "amount": 2000.00,
      |        "taxPaid": 2132.22
      |      }
      |    ],
      |    "bereavementAllowance": [{
      |      "dateIgnored": "2019-04-04T01:01:01Z",
      |      "submittedOn": "2019-04-04T01:01:01Z",
      |      "benefitId": "$benefitId",
      |      "startDate": "2020-01-01",
      |      "endDate": "2020-04-01",
      |      "amount": 2000.00
      |    }],
      |    "otherStateBenefits": [{
      |      "dateIgnored": "2019-04-04T01:01:01Z",
      |      "submittedOn": "2019-04-04T01:01:01Z",
      |      "benefitId": "$benefitId",
      |      "startDate": "2020-01-01",
      |      "endDate": "2020-04-01",
      |      "amount": 2000.00
      |    }]
      |  }
      |}
      |""".stripMargin
  )

  val mtdJson: JsValue = Json.parse(
    s"""
      |{
      |	"stateBenefits": [{
      |		"benefitType": "incapacityBenefit",
      |		"dateIgnored": "2019-04-04T01:01:01Z",
      |		"benefitId": "$benefitId",
      |		"startDate": "2020-01-01",
      |		"endDate": "2020-04-01",
      |		"amount": 2000,
      |		"taxPaid": 2132.22,
      |		"links": [{
      |			"href": "/individuals/state-benefits/AA123456A/2020-21?benefitId=$benefitId",
      |			"method": "GET",
      |			"rel": "self"
      |		}]
      |	}, {
      |		"benefitType": "incapacityBenefit",
      |		"dateIgnored": "2019-04-04T01:01:01Z",
      |		"benefitId": "f0d83ac0-a10a-4d57-9e41-6d033832779g",
      |		"startDate": "2020-03-01",
      |		"endDate": "2020-04-01",
      |		"amount": 1000,
      |		"links": [{
      |			"href": "/individuals/state-benefits/AA123456A/2020-21?benefitId=f0d83ac0-a10a-4d57-9e41-6d033832779g",
      |			"method": "GET",
      |			"rel": "self"
      |		}]
      |	}, {
      |		"benefitType": "statePension",
      |		"dateIgnored": "2019-04-04T01:01:01Z",
      |		"benefitId": "$benefitId",
      |		"startDate": "2019-01-01",
      |		"amount": 2000,
      |		"links": [{
      |			"href": "/individuals/state-benefits/AA123456A/2020-21?benefitId=$benefitId",
      |			"method": "GET",
      |			"rel": "self"
      |		}]
      |	}, {
      |		"benefitType": "statePensionLumpSum",
      |		"dateIgnored": "2019-04-04T01:01:01Z",
      |		"benefitId": "$benefitId",
      |		"startDate": "2019-01-01",
      |		"endDate": "2019-01-01",
      |		"amount": 2000,
      |		"taxPaid": 2132.22,
      |		"links": [{
      |			"href": "/individuals/state-benefits/AA123456A/2020-21?benefitId=$benefitId",
      |			"method": "GET",
      |			"rel": "self"
      |		}]
      |	}, {
      |		"benefitType": "employmentSupportAllowance",
      |		"dateIgnored": "2019-04-04T01:01:01Z",
      |		"benefitId": "$benefitId",
      |		"startDate": "2020-01-01",
      |		"endDate": "2020-04-01",
      |		"amount": 2000,
      |		"taxPaid": 2132.22,
      |		"links": [{
      |			"href": "/individuals/state-benefits/AA123456A/2020-21?benefitId=$benefitId",
      |			"method": "GET",
      |			"rel": "self"
      |		}]
      |	}, {
      |		"benefitType": "employmentSupportAllowance",
      |		"dateIgnored": "2019-04-04T01:01:01Z",
      |		"benefitId": "f0d83ac0-a10a-4d57-9e41-6d033832779g",
      |		"startDate": "2020-01-01",
      |		"endDate": "2020-04-01",
      |		"amount": 1000,
      |		"links": [{
      |			"href": "/individuals/state-benefits/AA123456A/2020-21?benefitId=f0d83ac0-a10a-4d57-9e41-6d033832779g",
      |			"method": "GET",
      |			"rel": "self"
      |		}]
      |	}, {
      |		"benefitType": "jobSeekersAllowance",
      |		"dateIgnored": "2019-04-04T01:01:01Z",
      |		"benefitId": "$benefitId",
      |		"startDate": "2020-01-01",
      |		"endDate": "2020-04-01",
      |		"amount": 2000,
      |		"taxPaid": 2132.22,
      |		"links": [{
      |			"href": "/individuals/state-benefits/AA123456A/2020-21?benefitId=$benefitId",
      |			"method": "GET",
      |			"rel": "self"
      |		}]
      |	}, {
      |		"benefitType": "jobSeekersAllowance",
      |		"dateIgnored": "2019-04-04T01:01:01Z",
      |		"benefitId": "f0d83ac0-a10a-4d57-9e41-6d033832779g",
      |		"startDate": "2020-01-01",
      |		"endDate": "2020-04-01",
      |		"amount": 1000,
      |		"links": [{
      |			"href": "/individuals/state-benefits/AA123456A/2020-21?benefitId=f0d83ac0-a10a-4d57-9e41-6d033832779g",
      |			"method": "GET",
      |			"rel": "self"
      |		}]
      |	}, {
      |		"benefitType": "bereavementAllowance",
      |		"dateIgnored": "2019-04-04T01:01:01Z",
      |		"benefitId": "$benefitId",
      |		"startDate": "2020-01-01",
      |		"endDate": "2020-04-01",
      |		"amount": 2000,
      |		"links": [{
      |			"href": "/individuals/state-benefits/AA123456A/2020-21?benefitId=$benefitId",
      |			"method": "GET",
      |			"rel": "self"
      |		}]
      |	}, {
      |		"benefitType": "otherStateBenefits",
      |		"dateIgnored": "2019-04-04T01:01:01Z",
      |		"benefitId": "$benefitId",
      |		"startDate": "2020-01-01",
      |		"endDate": "2020-04-01",
      |		"amount": 2000,
      |		"links": [{
      |			"href": "/individuals/state-benefits/AA123456A/2020-21?benefitId=$benefitId",
      |			"method": "GET",
      |			"rel": "self"
      |		}]
      |	}],
      |	"customerAddedStateBenefits": [{
      |		"benefitType": "incapacityBenefit",
      |		"submittedOn": "2019-04-04T01:01:01Z",
      |		"benefitId": "$benefitId",
      |		"startDate": "2020-01-01",
      |		"endDate": "2020-04-01",
      |		"amount": 2000,
      |		"taxPaid": 2132.22,
      |		"links": [{
      |			"href": "/individuals/state-benefits/AA123456A/2020-21?benefitId=$benefitId",
      |			"method": "GET",
      |			"rel": "self"
      |		}]
      |	}, {
      |		"benefitType": "incapacityBenefit",
      |		"submittedOn": "2019-04-04T01:01:01Z",
      |		"benefitId": "f0d83ac0-a10a-4d57-9e41-6d033832779g",
      |		"startDate": "2020-03-01",
      |		"endDate": "2020-04-01",
      |		"amount": 1000,
      |		"links": [{
      |			"href": "/individuals/state-benefits/AA123456A/2020-21?benefitId=f0d83ac0-a10a-4d57-9e41-6d033832779g",
      |			"method": "GET",
      |			"rel": "self"
      |		}]
      |	}, {
      |		"benefitType": "statePension",
      |		"submittedOn": "2019-04-04T01:01:01Z",
      |		"benefitId": "$benefitId",
      |		"startDate": "2019-01-01",
      |		"amount": 2000,
      |		"links": [{
      |			"href": "/individuals/state-benefits/AA123456A/2020-21?benefitId=$benefitId",
      |			"method": "GET",
      |			"rel": "self"
      |		}]
      |	}, {
      |		"benefitType": "statePensionLumpSum",
      |		"submittedOn": "2019-04-04T01:01:01Z",
      |		"benefitId": "$benefitId",
      |		"startDate": "2019-01-01",
      |		"endDate": "2019-01-01",
      |		"amount": 2000,
      |		"taxPaid": 2132.22,
      |		"links": [{
      |			"href": "/individuals/state-benefits/AA123456A/2020-21?benefitId=$benefitId",
      |			"method": "GET",
      |			"rel": "self"
      |		}]
      |	}, {
      |		"benefitType": "employmentSupportAllowance",
      |		"submittedOn": "2019-04-04T01:01:01Z",
      |		"benefitId": "$benefitId",
      |		"startDate": "2020-01-01",
      |		"endDate": "2020-04-01",
      |		"amount": 2000,
      |		"taxPaid": 2132.22,
      |		"links": [{
      |			"href": "/individuals/state-benefits/AA123456A/2020-21?benefitId=$benefitId",
      |			"method": "GET",
      |			"rel": "self"
      |		}]
      |	}, {
      |		"benefitType": "jobSeekersAllowance",
      |		"submittedOn": "2019-04-04T01:01:01Z",
      |		"benefitId": "$benefitId",
      |		"startDate": "2020-01-01",
      |		"endDate": "2020-04-01",
      |		"amount": 2000,
      |		"taxPaid": 2132.22,
      |		"links": [{
      |			"href": "/individuals/state-benefits/AA123456A/2020-21?benefitId=$benefitId",
      |			"method": "GET",
      |			"rel": "self"
      |		}]
      |	}, {
      |		"benefitType": "bereavementAllowance",
      |		"submittedOn": "2019-04-04T01:01:01Z",
      |		"benefitId": "$benefitId",
      |		"startDate": "2020-01-01",
      |		"endDate": "2020-04-01",
      |		"amount": 2000,
      |		"links": [{
      |			"href": "/individuals/state-benefits/AA123456A/2020-21?benefitId=$benefitId",
      |			"method": "GET",
      |			"rel": "self"
      |		}]
      |	}, {
      |		"benefitType": "otherStateBenefits",
      |		"submittedOn": "2019-04-04T01:01:01Z",
      |		"benefitId": "$benefitId",
      |		"startDate": "2020-01-01",
      |		"endDate": "2020-04-01",
      |		"amount": 2000,
      |		"links": [{
      |			"href": "/individuals/state-benefits/AA123456A/2020-21?benefitId=$benefitId",
      |			"method": "GET",
      |			"rel": "self"
      |		}]
      |	}],
      |	"links": [{
      |		"href": "/individuals/state-benefits/AA123456A/2020-21",
      |		"method": "POST",
      |		"rel": "create-state-benefit"
      |	}, {
      |		"href": "/individuals/state-benefits/AA123456A/2020-21",
      |		"method": "GET",
      |		"rel": "self"
      |	}]
      |}
      |""".stripMargin)

  val stateBenefits: HMRCStateBenefit = HMRCStateBenefit(
    benefitType = "incapacityBenefit",
    dateIgnored = Some("2019-04-04T01:01:01Z"),
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
    submittedOn = Some("2019-04-04T01:01:01Z")
  )

  val responseData: ListBenefitsResponse[HMRCStateBenefit, CustomerStateBenefit] = ListBenefitsResponse(
    stateBenefits = Some(Seq(stateBenefits)),
    customerAddedStateBenefits = Some(Seq(customerAddedStateBenefits))
  )

  val responseDataWithNoAmounts: ListBenefitsResponse[HMRCStateBenefit, CustomerStateBenefit] = ListBenefitsResponse(
    stateBenefits = Some(Seq(stateBenefits.copy(amount = None, taxPaid = None))),
    customerAddedStateBenefits = Some(Seq(customerAddedStateBenefits.copy(amount = None, taxPaid = None)))
  )

  val stateBenefitsLinks: Seq[Link] = List(Link(s"/individuals/state-benefits/AA123456A/2020-21?benefitId=$benefitId", GET, "self"))

  val singleStateBenefitsLinks: Seq[Link] = List(
    Link(s"/individuals/state-benefits/AA123456A/2020-21?benefitId=$benefitId", GET, "self"),
    Link(s"/individuals/state-benefits/AA123456A/2020-21/$benefitId/amounts", PUT, "amend-state-benefit-amounts"),
    Link(s"/individuals/state-benefits/AA123456A/2020-21/$benefitId/ignore", POST, "ignore-state-benefit"))

  val amountsLink: Link = Link(s"/individuals/state-benefits/AA123456A/2020-21/$benefitId/amounts",
    DELETE, "delete-state-benefit-amounts")

  val customerStateBenefitsLinks: Seq[Link] = List(
    Link(s"/individuals/state-benefits/AA123456A/2020-21?benefitId=$benefitId", GET, "self"))

  val singleCustomerStateBenefitsLinks: Seq[Link] = List(
    Link("/individuals/state-benefits/AA123456A/2020-21?benefitId=f0d83ac0-a10a-4d57-9e41-6d033832779g", GET, "self"),
    Link("/individuals/state-benefits/AA123456A/2020-21/f0d83ac0-a10a-4d57-9e41-6d033832779g/amounts", PUT, "amend-state-benefit-amounts"),
    Link("/individuals/state-benefits/AA123456A/2020-21/f0d83ac0-a10a-4d57-9e41-6d033832779g/amounts",
      DELETE, "delete-state-benefit-amounts"),
    Link("/individuals/state-benefits/AA123456A/2020-21/f0d83ac0-a10a-4d57-9e41-6d033832779g", DELETE, "delete-state-benefit"),
    Link("/individuals/state-benefits/AA123456A/2020-21/f0d83ac0-a10a-4d57-9e41-6d033832779g", PUT, "amend-state-benefit"))

  val listBenefitsLink: Seq[Link] = List(Link("/individuals/state-benefits/AA123456A/2020-21", POST, "create-state-benefit"),
    Link("/individuals/state-benefits/AA123456A/2020-21", GET, "self"))

  val listBenefitsResponse: ListBenefitsResponse[HMRCStateBenefit, CustomerStateBenefit] = ListBenefitsResponse(
    stateBenefits = Some(Seq(stateBenefits)),
    customerAddedStateBenefits = Some(Seq(customerAddedStateBenefits)
    )
  )

  val hateoasResponse: HateoasWrapper[ListBenefitsResponse[HateoasWrapper[HMRCStateBenefit], HateoasWrapper[CustomerStateBenefit]]] =
    HateoasWrapper(
      ListBenefitsResponse(
        Some(List(HateoasWrapper(stateBenefits, stateBenefitsLinks))),
        Some(List(HateoasWrapper(customerAddedStateBenefits, customerStateBenefitsLinks)))),
      listBenefitsLink)

  val hmrcOnlyHateoasResponse: HateoasWrapper[ListBenefitsResponse[HateoasWrapper[HMRCStateBenefit], HateoasWrapper[CustomerStateBenefit]]] =
    HateoasWrapper(
      ListBenefitsResponse(
        Some(List(HateoasWrapper(stateBenefits, stateBenefitsLinks))),
        None),
      listBenefitsLink)

  val customOnlyHateoasResponse: HateoasWrapper[ListBenefitsResponse[HateoasWrapper[HMRCStateBenefit], HateoasWrapper[CustomerStateBenefit]]] =
    HateoasWrapper(
      ListBenefitsResponse(
        None,
        Some(List(HateoasWrapper(customerAddedStateBenefits, customerStateBenefitsLinks)))),
      listBenefitsLink)

  val singleCustomOnlyHateoasResponse: HateoasWrapper[ListBenefitsResponse[HateoasWrapper[HMRCStateBenefit], HateoasWrapper[CustomerStateBenefit]]] =
    HateoasWrapper(
      ListBenefitsResponse(
        None,
        Some(List(HateoasWrapper(customerAddedStateBenefits, singleCustomerStateBenefitsLinks)))),
      listBenefitsLink)


  val hateoasResponseWithOutAmounts: HateoasWrapper[ListBenefitsResponse[HateoasWrapper[HMRCStateBenefit], HateoasWrapper[CustomerStateBenefit]]] =
    HateoasWrapper(
      ListBenefitsResponse(
        Some(List(HateoasWrapper(stateBenefits.copy(amount = None, taxPaid = None), singleStateBenefitsLinks))),
        None),
      listBenefitsLink)
}
