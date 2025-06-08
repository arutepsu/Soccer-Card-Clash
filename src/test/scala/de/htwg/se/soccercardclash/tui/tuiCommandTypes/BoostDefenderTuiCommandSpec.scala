package de.htwg.se.soccercardclash.tui.tuiCommandTypes

import de.htwg.se.soccercardclash.controller.IController
import de.htwg.se.soccercardclash.controller.contextHolder.IGameContextHolder
import de.htwg.se.soccercardclash.model.gameComponent.context.GameContext
import de.htwg.se.soccercardclash.view.tui.tuiCommand.base.ITuiCommand
import de.htwg.se.soccercardclash.view.tui.tuiCommand.tuiCommandTypes.{AttackTuiCommand, BoostDefenderTuiCommand}
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import org.mockito.Mockito.*
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.mockito.MockitoSugar

class BoostDefenderTuiCommandSpec extends AnyWordSpec with Matchers with MockitoSugar {

  "BoostDefenderTuiCommand" should {

    "call controller.boostDefender with valid index" in {
      val controller = mock[IController]
      val contextHolder = mock[IGameContextHolder]
      val context = mock[GameContext]

      when(contextHolder.get).thenReturn(context)

      val cmd = new BoostDefenderTuiCommand(controller, contextHolder)
      cmd.execute(Some("1"))

      verify(controller).boostDefender(1, context)
    }

    "print error message for non-integer input" in {
      val controller = mock[IController]
      val contextHolder = mock[IGameContextHolder]
      val cmd = new BoostDefenderTuiCommand(controller, contextHolder)

      val output = new java.io.ByteArrayOutputStream()
      Console.withOut(output) {
        cmd.execute(Some("abc"))
      }

      output.toString should include("Invalid input")
      verifyNoInteractions(controller)
    }

    "print error message for missing input" in {
      val controller = mock[IController]
      val contextHolder = mock[IGameContextHolder]
      val cmd = new BoostDefenderTuiCommand(controller, contextHolder)

      val output = new java.io.ByteArrayOutputStream()
      Console.withOut(output) {
        cmd.execute(None)
      }

      output.toString should include("Invalid input")
      verifyNoInteractions(controller)
    }
  }
}
