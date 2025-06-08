package de.htwg.se.soccercardclash.tui.tuiCommandTypes

import de.htwg.se.soccercardclash.controller.IController
import de.htwg.se.soccercardclash.controller.contextHolder.IGameContextHolder
import de.htwg.se.soccercardclash.model.gameComponent.context.GameContext
import de.htwg.se.soccercardclash.view.tui.tuiCommand.base.ITuiCommand
import de.htwg.se.soccercardclash.view.tui.tuiCommand.tuiCommandTypes.{AttackTuiCommand, BoostDefenderTuiCommand, BoostGoalkeeperTuiCommand}
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import org.mockito.Mockito.*
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.mockito.MockitoSugar

class BoostGoalkeeperTuiCommandSpec extends AnyWordSpec with Matchers with MockitoSugar {

  "BoostGoalkeeperTuiCommand" should {

    "always call controller.boostGoalkeeper regardless of input" in {
      val controller = mock[IController]
      val contextHolder = mock[IGameContextHolder]
      val context = mock[GameContext]

      when(contextHolder.get).thenReturn(context)

      val cmd = new BoostGoalkeeperTuiCommand(controller, contextHolder)

      cmd.execute(Some("2"))
      cmd.execute(Some("invalid"))
      cmd.execute(None)

      verify(controller, times(3)).boostGoalkeeper(context)
    }
  }
}
