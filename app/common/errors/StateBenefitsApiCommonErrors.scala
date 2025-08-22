/*
 * Copyright 2024 HM Revenue & Customs
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

package common.errors

import play.api.http.Status.*
import shared.models.errors.MtdError

object BenefitIdFormatError extends MtdError("FORMAT_BENEFIT_ID", "The provided benefit ID is invalid", BAD_REQUEST)

object BenefitTypeFormatError extends MtdError("FORMAT_BENEFIT_TYPE", "The provided benefit type is invalid", BAD_REQUEST)

object BusinessIdFormatError extends MtdError("FORMAT_BUSINESS_ID", "The Business ID format is invalid", BAD_REQUEST)

object RuleStartDateAfterTaxYearEndError
    extends MtdError("RULE_START_DATE_AFTER_TAX_YEAR_END", "The start date cannot be later than the tax year end", BAD_REQUEST)

object RuleEndDateBeforeTaxYearStartError
    extends MtdError("RULE_END_DATE_BEFORE_TAX_YEAR_START", "The end date cannot be before the tax year starts", BAD_REQUEST)

object RuleIncorrectOrEmptyBodyError
    extends MtdError("RULE_INCORRECT_OR_EMPTY_BODY_SUBMITTED", "An empty or non-matching body was submitted", BAD_REQUEST)

object RuleDeleteForbiddenError extends MtdError("RULE_DELETE_FORBIDDEN", "A deletion of a HMRC held state benefit is not permitted", BAD_REQUEST)

object RuleUpdateForbiddenError extends MtdError("RULE_UPDATE_FORBIDDEN", "The update for a HMRC held benefit is not permitted", BAD_REQUEST)

object RuleIgnoreForbiddenError extends MtdError("RULE_IGNORE_FORBIDDEN", "A customer added state benefit cannot be ignored", BAD_REQUEST)

object RuleUnignoreForbiddenError extends MtdError("RULE_UNIGNORE_FORBIDDEN", "A customer added state benefit cannot be unignored", BAD_REQUEST)

object RuleBenefitTypeExists extends MtdError("RULE_BENEFIT_TYPE_EXISTS", "A benefit of this type has already been created", BAD_REQUEST)

object RuleOutsideAmendmentWindow extends  MtdError("RULE_OUTSIDE_AMENDMENT_WINDOW", "You are outside the amendment window", BAD_REQUEST)