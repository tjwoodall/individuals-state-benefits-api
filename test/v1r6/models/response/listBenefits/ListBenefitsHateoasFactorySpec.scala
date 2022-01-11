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

import mocks.MockAppConfig
import org.scalatest.prop.TableDrivenPropertyChecks
import support.UnitSpec
import v1r6.hateoas.{HateoasLinks, HateoasListLinksFactory2}

class ListBenefitsHateoasFactorySpec extends UnitSpec with HateoasLinks with MockAppConfig with TableDrivenPropertyChecks {

  private val nino = "nino"
  private val taxYear = "2020-21"
  private val benefitId = "benefitId"

  private val dateIgnored = Some("ignoreDate")

  class Test {
    MockAppConfig.apiGatewayContext returns "gatewayContext" anyNumberOfTimes()

    val hateoasFactory: HateoasListLinksFactory2[ListBenefitsResponse,
      HMRCStateBenefit, CustomerStateBenefit, ListBenefitsHateoasData] = implicitly
  }

  "Hateoas factory" when {
    "links obtained for response" must {
      val hateoasData = ListBenefitsHateoasData(nino, taxYear, queryIsFiltered = false, hmrcBenefitIds = Nil)

      "be self and add" in new Test {
        hateoasFactory.links(mockAppConfig, hateoasData) should
          contain theSameElementsAs Seq(
          addBenefit(mockAppConfig, nino, taxYear),
          listBenefits(mockAppConfig, nino, taxYear),
        )
      }
    }

    "links obtained for HMRC benefit item" when {
      def stateBenefit(dateIgnored: Option[String] = None): HMRCStateBenefit =
        HMRCStateBenefit(benefitType = "someBenefitType",
          dateIgnored = dateIgnored,
          submittedOn = None,
          benefitId = benefitId,
          startDate = "startDate",
          endDate = None,
          amount = None,
          taxPaid = None)

      val benefitNotIgnored = stateBenefit()
      val benefitIgnored = stateBenefit(dateIgnored = dateIgnored)

      "query full list" when {
        val hateoasData = ListBenefitsHateoasData(nino, taxYear, queryIsFiltered = false, hmrcBenefitIds = Seq(benefitId))

        "any scenario" must {
          "just return list link with query param" in new Test {
            forAll(Table("benefit", benefitNotIgnored, benefitIgnored)) { benefit =>
              hateoasFactory.itemLinks1(mockAppConfig, hateoasData, benefit) should
                contain theSameElementsAs Seq(
                retrieveSingleBenefit(mockAppConfig, nino, taxYear, benefitId),
              )
            }
          }
        }
      }

      "query specific benefitId" when {
        val hateoasData = ListBenefitsHateoasData(nino, taxYear, queryIsFiltered = true, hmrcBenefitIds = Seq(benefitId))

        "no dateIgnored is set" must {
          "include ignore link" in new Test {
            hateoasFactory.itemLinks1(mockAppConfig, hateoasData, benefitNotIgnored) should
              contain theSameElementsAs Seq(
              retrieveSingleBenefit(mockAppConfig, nino, taxYear, benefitId),
              updateBenefitAmounts(mockAppConfig, nino, taxYear, benefitId),
              ignoreBenefit(mockAppConfig, nino, taxYear, benefitId),
            )
          }
        }

        "a dateIgnored is set" must {
          "include unignore link" in new Test {
            hateoasFactory.itemLinks1(mockAppConfig, hateoasData, benefitIgnored) should
              contain theSameElementsAs Seq(
              retrieveSingleBenefit(mockAppConfig, nino, taxYear, benefitId),
              updateBenefitAmounts(mockAppConfig, nino, taxYear, benefitId),
              unignoreBenefit(mockAppConfig, nino, taxYear, benefitId),
            )
          }
        }
      }
    }

    "links obtained for custom benefit item" when {
      def stateBenefit(amount: Option[BigDecimal] = None,
                       taxPaid: Option[BigDecimal] = None): CustomerStateBenefit =
        CustomerStateBenefit(benefitType = "someBenefitType",
          submittedOn = None,
          benefitId = benefitId,
          startDate = "startDate",
          endDate = None,
          amount = amount,
          taxPaid = taxPaid)

      val benefit = stateBenefit()
      val benefitWithAmount = stateBenefit(amount = Some(1))
      val benefitWithTaxPaid = stateBenefit(taxPaid = Some(1))

      "query full list" when {
        "any scenario" must {
          "just return list link with query param" in new Test {
            forAll(Table(("benefit", "hmrcBenefitIds"),
              (benefit, Nil),
              (benefit, Seq(benefitId)),
              (benefitWithAmount, Nil),
              (benefitWithTaxPaid, Nil))) {
              case (benefit, hmrcBenefitIds) =>
                hateoasFactory.itemLinks2(
                  mockAppConfig,
                  ListBenefitsHateoasData(nino, taxYear, queryIsFiltered = false, hmrcBenefitIds),
                  benefit) should
                  contain theSameElementsAs Seq(
                  retrieveSingleBenefit(mockAppConfig, nino, taxYear, benefitId),
                )
            }
          }
        }
      }

      "query specific benefitId" when {
        def hateoasData(hmrcBenefitIds: Seq[String] = Nil): ListBenefitsHateoasData =
          ListBenefitsHateoasData(nino, taxYear, queryIsFiltered = true, hmrcBenefitIds)

        "a benefit is not duplicated in HMRC benefits" must {
          "include update and delete links" in new Test {
            hateoasFactory.itemLinks2(mockAppConfig, hateoasData(hmrcBenefitIds = Seq("otherBenefitId")), benefit) should
              contain theSameElementsAs Seq(
              retrieveSingleBenefit(mockAppConfig, nino, taxYear, benefitId),
              updateBenefit(mockAppConfig, nino, taxYear, benefitId),
              deleteBenefit(mockAppConfig, nino, taxYear, benefitId),
              updateBenefitAmounts(mockAppConfig, nino, taxYear, benefitId),
            )
          }
        }

        "a benefit is duplicated in HMRC benefits" must {
          "not include update and delete links" in new Test {
            hateoasFactory.itemLinks2(mockAppConfig, hateoasData(hmrcBenefitIds = Seq(benefitId)), benefit) should
              contain theSameElementsAs Seq(
              retrieveSingleBenefit(mockAppConfig, nino, taxYear, benefitId),
              updateBenefitAmounts(mockAppConfig, nino, taxYear, benefitId),
            )
          }
        }

        "benefit has amount" must {
          "include delete amount link" in new Test {
            hateoasFactory.itemLinks2(mockAppConfig, hateoasData(), benefitWithAmount) should
              contain theSameElementsAs Seq(
              retrieveSingleBenefit(mockAppConfig, nino, taxYear, benefitId),
              updateBenefit(mockAppConfig, nino, taxYear, benefitId),
              deleteBenefit(mockAppConfig, nino, taxYear, benefitId),
              updateBenefitAmounts(mockAppConfig, nino, taxYear, benefitId),
              deleteBenefitAmounts(mockAppConfig, nino, taxYear, benefitId),
            )
          }
        }

        "benefit has taxPaid" must {
          "include delete amount link" in new Test {
            hateoasFactory.itemLinks2(mockAppConfig, hateoasData(), benefitWithTaxPaid) should
              contain theSameElementsAs Seq(
              retrieveSingleBenefit(mockAppConfig, nino, taxYear, benefitId),
              updateBenefit(mockAppConfig, nino, taxYear, benefitId),
              deleteBenefit(mockAppConfig, nino, taxYear, benefitId),
              updateBenefitAmounts(mockAppConfig, nino, taxYear, benefitId),
              deleteBenefitAmounts(mockAppConfig, nino, taxYear, benefitId),
            )
          }
        }
      }
    }
  }
}

