package de.htwg.se.soccercardclash.tui.tuiCommandTypes

import de.htwg.se.soccercardclash.controller.IController
import de.htwg.se.soccercardclash.util.Events
import de.htwg.se.soccercardclash.view.tui.tuiCommand.tuiCommandTypes.StartGameTuiCommand
import org.mockito.Mockito._
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import org.scalatestplus.mockito.MockitoSugar

import java.io.{ByteArrayOutputStream, PrintStream}

class StartGameTuiCommandTest extends AnyFlatSpec with Matchers with MockitoSugar {

  def captureOutput(block: => Unit): String = {
    val out = new ByteArrayOutputStream()
    Console.withOut(new PrintStream(out)) {
      block
    }
    out.toString.trim
  }

//  "StartGameTuiCommand" should "create a game and notify observers" in {
//    val mockController = mock[IController]
//    val command = new StartGameTuiCommand(mockController, "Alice", "Bob")
//
//    val output = captureOutput {
//      command.execute()
//    }
//
//    verify(mockController).createGame("Alice", "Bob")
//    verify(mockController).notifyObservers(Events.PlayingField)
//    output should include("🎮 Starting game with players: Alice & Bob")
//  }
}
