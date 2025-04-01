package de.htwg.se.soccercardclash.tui.tuiCommandTypes

import de.htwg.se.soccercardclash.controller.IController
import de.htwg.se.soccercardclash.view.tui.tuiCommand.tuiCommandTypes.SaveGameTuiCommand
import org.mockito.Mockito._
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import org.scalatestplus.mockito.MockitoSugar

import java.io.{ByteArrayOutputStream, PrintStream}

class SaveGameTuiCommandTest extends AnyFlatSpec with Matchers with MockitoSugar {

  def captureOutput(block: => Unit): String = {
    val out = new ByteArrayOutputStream()
    Console.withOut(new PrintStream(out)) {
      block
    }
    out.toString.trim
  }

  "SaveGameTuiCommand" should "call saveGame and print success message" in {
    val mockController = mock[IController]
    val command = new SaveGameTuiCommand(mockController)

    val output = captureOutput {
      command.execute()
    }

    verify(mockController).saveGame()
    output should include("✅ Game saved successfully.")
  }

  it should "print error message if saveGame throws an exception" in {
    val mockController = mock[IController]
    val command = new SaveGameTuiCommand(mockController)

    when(mockController.saveGame()).thenThrow(new RuntimeException("disk full"))

    val output = captureOutput {
      command.execute()
    }

    output should include("❌ ERROR: Failed to save game")
    output should include("disk full")
  }
}
