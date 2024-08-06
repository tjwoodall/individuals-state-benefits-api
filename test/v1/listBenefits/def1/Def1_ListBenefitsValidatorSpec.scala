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

package v1.listBenefits.def1

import api.models.domain.{Nino, TaxYear}
import api.models.errors._
import support.UnitSpec
import v1.listBenefits.model.request.ListBenefitsRequestData
import v1.models.domain.BenefitId

class Def1_ListBenefitsValidatorSpec extends UnitSpec {

  private implicit val correlationId: String = "a1e8057e-fbbc-47a8-a8b4-78d9f015c253"

  private val validNino      = "AA123456B"
  private val validTaxYear   = "2020-21"
  private val validBenefitId = Some("4557ecb5-fd32-48cc-81f5-e6acd1099f3c")

  private val parsedNino      = Nino(validNino)
  private val parsedTaxYear   = TaxYear.fromMtd(validTaxYear)
  private val parsedBenefitId = validBenefitId.map(BenefitId)

  private def validator(nino: String, taxYear: String, benefitId: Option[String]) = new Def1_ListBenefitsValidator(nino, taxYear, benefitId)

  "validator" should {
    "return the parsed domain object" when {
      "valid request data is supplied" in {
        val result = validator(validNino, validTaxYear, validBenefitId).validateAndWrapResult()
        result shouldBe Right(ListBenefitsRequestData(parsedNino, parsedTaxYear, parsedBenefitId))
      }
      "passed a valid request with no query parameters" in {
        val result = validator(validNino, validTaxYear, None).validateAndWrapResult()
        result shouldBe Right(ListBenefitsRequestData(parsedNino, parsedTaxYear, None))
      }
    }

    "return a single error" when {
      "passed an invalid nino" in {
        val result = validator("A12344A", validTaxYear, validBenefitId).validateAndWrapResult()

        result shouldBe Left(ErrorWrapper(correlationId, NinoFormatError))
      }

      "passed an invalid tax year" in {
        val result = validator(validNino, "202223", validBenefitId).validateAndWrapResult()

        result shouldBe Left(ErrorWrapper(correlationId, TaxYearFormatError))
      }

      "passed a tax year with an invalid range" in {
        val result = validator(validNino, "2022-24", validBenefitId).validateAndWrapResult()

        result shouldBe Left(ErrorWrapper(correlationId, RuleTaxYearRangeInvalidError))
      }

      "passed a tax year that precedes the minimum" in {
        val result = validator(validNino, "2018-19", validBenefitId).validateAndWrapResult()

        result shouldBe Left(ErrorWrapper(correlationId, RuleTaxYearNotSupportedError))
      }

      "passed an invalid benefitId" in {
        val result = validator(validNino, validTaxYear, Some("invalid")).validateAndWrapResult()

        result shouldBe Left(ErrorWrapper(correlationId, BenefitIdFormatError))
      }
    }

    "return multiple errors" when {
      "passed multiple invalid fields" in {
        val result = validator("not-a-nino", "not-a-tax-year", Some("not-a-benefit-id")).validateAndWrapResult()

        result shouldBe Left(
          ErrorWrapper(
            correlationId,
            BadRequestError,
            Some(List(BenefitIdFormatError, NinoFormatError, TaxYearFormatError))
          )
        )
      }
    }
  }

}
