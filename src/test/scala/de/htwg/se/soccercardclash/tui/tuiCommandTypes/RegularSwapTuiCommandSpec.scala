package de.htwg.se.soccercardclash.tui.tuiCommandTypes

import de.htwg.se.soccercardclash.controller.IController
import de.htwg.se.soccercardclash.model.gameComponent.context.GameContext
import de.htwg.se.soccercardclash.util.IGameContextHolder
import de.htwg.se.soccercardclash.view.tui.tuiCommand.base.ITuiCommand
import de.htwg.se.soccercardclash.view.tui.tuiCommand.tuiCommandTypes.{AttackTuiCommand, RegularSwapTuiCommand}
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import org.mockito.Mockito.*
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.mockito.MockitoSugar

class RegularSwapTuiCommandSpec extends AnyWordSpec with Matchers with MockitoSugar {

  "RegularSwapTuiCommand" should {

    "call controller.regularSwap with valid index" in {
      val controller = mock[IController]
      val context = mock[GameContext]
      val contextHolder = mock[IGameContextHolder]
      when(contextHolder.get).thenReturn(context)

      val cmd = new RegularSwapTuiCommand(controller, contextHolder)
      cmd.execute(Some("2"))

      verify(controller).regularSwap(2, context)
    }

    "print error message for invalid input" in {
      val controller = mock[IController]
      val contextHolder = mock[IGameContextHolder]

      val cmd = new RegularSwapTuiCommand(controller, contextHolder)
      val output = new java.io.ByteArrayOutputStream()

      Console.withOut(output) {
        cmd.execute(Some("abc"))
      }

      output.toString should include("Invalid input")
      verifyNoInteractions(controller)
    }

    "print error message when input is missing" in {
      val controller = mock[IController]
      val contextHolder = mock[IGameContextHolder]

      val cmd = new RegularSwapTuiCommand(controller, contextHolder)
      val output = new java.io.ByteArrayOutputStream()

      Console.withOut(output) {
        cmd.execute(None)
      }

      output.toString should include("Invalid input")
      verifyNoInteractions(controller)
    }
  }
}
