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

package config

import org.scalamock.handlers.{CallHandler, CallHandler0}
import org.scalamock.scalatest.MockFactory
import shared.config.DownstreamConfig

trait MockStateBenefitsAppConfig extends MockFactory {

  implicit val mockStateBenefitsAppConfig: StateBenefitsAppConfig = mock[StateBenefitsAppConfig]

  object MockedStateBenefitsAppConfig {

    def api1651DownstreamConfig: CallHandler[DownstreamConfig] = (() => mockStateBenefitsAppConfig.api1651DownstreamConfig).expects()

    def tysIfsDownstreamConfig: CallHandler0[DownstreamConfig] = (() => mockStateBenefitsAppConfig.tysIfsDownstreamConfig: DownstreamConfig).expects()
  }

}
