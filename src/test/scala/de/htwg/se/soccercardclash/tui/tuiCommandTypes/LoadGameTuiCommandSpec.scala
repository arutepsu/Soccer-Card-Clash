package de.htwg.se.soccercardclash.tui.tuiCommandTypes

import de.htwg.se.soccercardclash.controller.IController
import de.htwg.se.soccercardclash.view.tui.tuiCommand.tuiCommandTypes.LoadGameTuiCommand
import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import org.scalatestplus.mockito.MockitoSugar
import org.mockito.Mockito._

class LoadGameTuiCommandSpec extends AnyWordSpec with Matchers with MockitoSugar {

  "LoadGameTuiCommand" should {

    "print success message when game loads successfully" in {
      val mockController = mock[IController]
      val fileName = "testGame"
      val command = new LoadGameTuiCommand(mockController, fileName)

      // Mock behavior
      doNothing().when(mockController).loadGame(fileName)

      val output = captureOutput(command.execute())

      output should include(s"✅ Game '$fileName' loaded successfully.")
      verify(mockController).loadGame(fileName)
    }

    "print error message when game loading fails" in {
      val mockController = mock[IController]
      val fileName = "missingGame"
      val command = new LoadGameTuiCommand(mockController, fileName)

      // Simulate failure
      when(mockController.loadGame(fileName)).thenThrow(new RuntimeException("File not found"))

      val output = captureOutput(command.execute())

      output should include(s"❌ ERROR: Failed to load game '$fileName'")
      output should include("File not found")
      verify(mockController).loadGame(fileName)
    }
  }

  def captureOutput(f: => Unit): String = {
    val stream = new java.io.ByteArrayOutputStream()
    Console.withOut(stream)(f)
    stream.toString.trim
  }
}
