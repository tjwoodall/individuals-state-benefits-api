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

package v2.listBenefits.model.response

import play.api.libs.json.{Json, OFormat}
import shared.models.domain.Timestamp

sealed trait StateBenefit {
  def benefitId: String
}

case class HMRCStateBenefit(benefitType: String,
                            dateIgnored: Option[Timestamp] = None,
                            submittedOn: Option[Timestamp],
                            benefitId: String,
                            startDate: String,
                            endDate: Option[String],
                            amount: Option[BigDecimal],
                            taxPaid: Option[BigDecimal])
    extends StateBenefit

object HMRCStateBenefit {
  implicit val format: OFormat[HMRCStateBenefit] = Json.format[HMRCStateBenefit]
}

case class CustomerStateBenefit(benefitType: String,
                                submittedOn: Option[Timestamp],
                                benefitId: String,
                                startDate: String,
                                endDate: Option[String],
                                amount: Option[BigDecimal],
                                taxPaid: Option[BigDecimal])
    extends StateBenefit {

  val hasAmounts: Boolean = amount.isDefined || taxPaid.isDefined
}

object CustomerStateBenefit {
  implicit val format: OFormat[CustomerStateBenefit] = Json.format[CustomerStateBenefit]
}
