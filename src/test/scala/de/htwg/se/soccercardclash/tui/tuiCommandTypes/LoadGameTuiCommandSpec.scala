package de.htwg.se.soccercardclash.tui.tuiCommandTypes

import de.htwg.se.soccercardclash.controller.IController
import de.htwg.se.soccercardclash.controller.contextHolder.IGameContextHolder
import de.htwg.se.soccercardclash.model.gameComponent.context.GameContext
import de.htwg.se.soccercardclash.view.tui.tuiCommand.base.ITuiCommand
import de.htwg.se.soccercardclash.view.tui.tuiCommand.tuiCommandTypes.{AttackTuiCommand, DoubleAttackTuiCommand, LoadGameTuiCommand}
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import org.mockito.Mockito.*
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.mockito.MockitoSugar

class LoadGameTuiCommandSpec extends AnyWordSpec with Matchers with MockitoSugar {

  "LoadGameTuiCommand" should {

    "print success message and notify scene switch when load succeeds" in {
      val controller = mock[IController]
      val command = new LoadGameTuiCommand(controller, "game.xml")

      // Spy on GlobalObservable (if replaceable). Otherwise, we check only println.
      val output = new java.io.ByteArrayOutputStream()
      Console.withOut(output) {
        command.execute()
      }

      verify(controller).loadGame("game.xml")
      output.toString should include("loaded successfully")
    }

    "print error message when controller.loadGame throws" in {
      val controller = mock[IController]
      val command = new LoadGameTuiCommand(controller, "corrupt.xml")

      when(controller.loadGame("corrupt.xml")).thenThrow(new RuntimeException("file not found"))

      val output = new java.io.ByteArrayOutputStream()
      Console.withOut(output) {
        command.execute()
      }

      output.toString should include("ERROR")
      output.toString should include("file not found")
    }
  }
}
