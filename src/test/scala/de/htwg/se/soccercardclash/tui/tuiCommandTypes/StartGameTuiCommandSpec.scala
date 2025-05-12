package de.htwg.se.soccercardclash.tui.tuiCommandTypes

import de.htwg.se.soccercardclash.controller.IController
import de.htwg.se.soccercardclash.util.Events
import de.htwg.se.soccercardclash.view.tui.tuiCommand.tuiCommandTypes.StartGameTuiCommand
import org.mockito.Mockito.*
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import org.scalatestplus.mockito.MockitoSugar

import java.io.{ByteArrayOutputStream, PrintStream}
import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import org.scalatestplus.mockito.MockitoSugar
import org.mockito.Mockito.*
import org.mockito.ArgumentMatchers.*
import de.htwg.se.soccercardclash.controller.IController

class StartGameTuiCommandSpec extends AnyWordSpec with Matchers with MockitoSugar {

  "StartGameTuiCommand" should {
    "call controller.createGame with the given player names and print message" in {
      val mockController = mock[IController]
      val player1 = "Alice"
      val player2 = "Bob"

      val command = new StartGameTuiCommand(mockController, player1, player2)

      // Capture console output
      val stream = new java.io.ByteArrayOutputStream()
      Console.withOut(stream) {
        command.execute()
      }

      // Check that controller.createGame was called
      verify(mockController).createGame(player1, player2)

      // Check that the correct message was printed
      stream.toString.trim should include(s"🎮 Starting game with players: $player1 & $player2")
    }
  }
}

