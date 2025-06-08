package de.htwg.se.soccercardclash.tui.tuiCommandTypes
import de.htwg.se.soccercardclash.controller.IController
import de.htwg.se.soccercardclash.controller.contextHolder.IGameContextHolder
import de.htwg.se.soccercardclash.model.gameComponent.context.GameContext
import de.htwg.se.soccercardclash.view.tui.tuiCommand.base.ITuiCommand
import de.htwg.se.soccercardclash.view.tui.tuiCommand.tuiCommandTypes.{AttackTuiCommand, RegularSwapTuiCommand, ReverseSwapTuiCommand, SaveGameTuiCommand}
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import org.mockito.Mockito.*
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.mockito.MockitoSugar

class SaveGameTuiCommandSpec extends AnyWordSpec with Matchers with MockitoSugar {

  "SaveGameTuiCommand" should {

    "print success message when saving works" in {
      val controller = mock[IController]
      val context = mock[GameContext]
      val contextHolder = mock[IGameContextHolder]

      when(contextHolder.get).thenReturn(context)

      val cmd = new SaveGameTuiCommand(controller, contextHolder)

      val out = new java.io.ByteArrayOutputStream()
      Console.withOut(out) {
        cmd.execute()
      }

      verify(controller).saveGame(context)
      out.toString should include("Game saved successfully.")
    }

    "print error message when saveGame throws exception" in {
      val controller = mock[IController]
      val context = mock[GameContext]
      val contextHolder = mock[IGameContextHolder]

      when(contextHolder.get).thenReturn(context)
      when(controller.saveGame(context)).thenThrow(new RuntimeException("disk full"))

      val cmd = new SaveGameTuiCommand(controller, contextHolder)

      val out = new java.io.ByteArrayOutputStream()
      Console.withOut(out) {
        cmd.execute()
      }

      verify(controller).saveGame(context)
      out.toString should include("ERROR: Failed to save game")
      out.toString should include("disk full")
    }
  }
}
