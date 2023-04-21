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

package v1

import api.models.errors.ErrorWrapper
import api.models.outcomes.ResponseWrapper
import v1.models.response.createBenefit.CreateBenefitResponse
import v1.models.response.listBenefits.{CustomerStateBenefit, HMRCStateBenefit, ListBenefitsResponse}

package object services {

  type ServiceOutcome[A] = Either[ErrorWrapper, ResponseWrapper[A]]

  type AmendBenefitAmountsServiceOutcome = ServiceOutcome[Unit]

  type AmendBenefitServiceOutcome = ServiceOutcome[Unit]

  type CreateBenefitServiceOutcome = ServiceOutcome[CreateBenefitResponse]

  type DeleteBenefitAmountsServiceOutcome = ServiceOutcome[Unit]

  type DeleteBenefitServiceOutcome = ServiceOutcome[Unit]

  type IgnoreBenefitServiceOutcome = ServiceOutcome[Unit]

  type ListBenefitsServiceOutcome = ServiceOutcome[ListBenefitsResponse[HMRCStateBenefit, CustomerStateBenefit]]

  type UnignoreBenefitServiceOutcome = ServiceOutcome[Unit]

}
