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

package navigation

import controllers.routes
import models.{CheckMode, Mode, NormalMode}
import pages.{DisposalDatePage, Page, SellForLessPage, SellOrGiveAwayPage}
import play.api.mvc.{Call, Request}

import javax.inject.{Inject, Singleton}

@Singleton
class Navigation @Inject() {
  lazy val checkingRoutes: Map[Page, (Request[?], Option[Boolean]) => Call] = Map(
    DisposalDatePage -> ((request, _) => routesToCYAPage(request, CheckMode)),
    SellOrGiveAwayPage -> ((request, _) => routesToCYAPage(request, CheckMode)),
    SellForLessPage -> ((request, _) => routesToCYAPage(request, CheckMode))
  )

  lazy val normalRoutes: Map[Page, (Request[?], Option[Boolean]) => Call] = Map(
    //todo:include tax year
    DisposalDatePage -> ((_, _) => routes.GainController.sellOrGiveAway(NormalMode)),
    SellOrGiveAwayPage -> ((_, yesOrNo) => routesToSellOrGiveAway(NormalMode, yesOrNo))
  )

  def nextPage(page: Page, mode: Mode, yesOrNo: Option[Boolean])(implicit request: Request[?]): Call = {
    mode match {
      case CheckMode => println("Checkmode======="+yesOrNo)
        checkingRoutes(page)(request, yesOrNo)
      case NormalMode =>
        normalRoutes(page)(request, yesOrNo)
    }
  }

  def routesToCYAPage(userRequest: Request[?], mode: Mode): Call = {
    //todo: include three reviewAnswers cases here
    controllers.routes.ReviewAnswersController.reviewGainAnswers
  }

  def routesToSellOrGiveAway(mode: Mode, yesOrNo: Option[Boolean]): Call = {
    if yesOrNo.get then
      routes.GainController.whoDidYouGiveItTo
    else
      routes.GainController.sellForLess(mode)
  }
}
