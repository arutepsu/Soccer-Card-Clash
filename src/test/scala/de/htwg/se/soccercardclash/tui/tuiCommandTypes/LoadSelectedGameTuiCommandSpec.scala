package de.htwg.se.soccercardclash.tui.tuiCommandTypes

import de.htwg.se.soccercardclash.controller.IController
import de.htwg.se.soccercardclash.view.tui.tuiCommand.base.ITuiCommand
import de.htwg.se.soccercardclash.view.tui.tuiCommand.factory.ITuiCommandFactory
import de.htwg.se.soccercardclash.view.tui.tuiCommand.tuiCommandTypes.LoadSelectedGameTuiCommand
import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import org.scalatestplus.mockito.MockitoSugar
import org.mockito.Mockito.*
import org.mockito.ArgumentMatchers.any

import java.io.{File, PrintWriter}

class LoadSelectedGameTuiCommandSpec extends AnyWordSpec with Matchers with MockitoSugar {

  "LoadSelectedGameTuiCommand" should {

    "execute load command when index is valid" in {
      val mockController = mock[IController]
      val mockFactory = mock[ITuiCommandFactory]

      // Use a real implementation that prints
      val mockLoadCmd = new ITuiCommand {
        override def execute(input: Option[String] = None): Unit = {
          println(s"✅ Game 'save1.xml' loaded successfully.")
        }
      }

      // Setup: Create test "games" directory and file
      val gamesDir = new File("games")
      gamesDir.mkdir()
      val testFile = new File(gamesDir, "save1.xml")
      new PrintWriter(testFile) {
        write("<game/>"); close()
      }

      when(mockFactory.createLoadGameTuiCommand(any[String])).thenReturn(mockLoadCmd)

      val command = new LoadSelectedGameTuiCommand(mockController, 0, mockFactory)

      val output = captureOutput {
        command.execute()
      }

      output should include("✅ Game 'save1.xml' loaded successfully.")
    }

    "print error if index is invalid" in {
      val mockController = mock[IController]
      val mockFactory = mock[ITuiCommandFactory]

      // Setup: Create test "games" directory and file
      val gamesDir = new File("games")
      gamesDir.mkdir()
      val testFile = new File(gamesDir, "save1.xml")
      new PrintWriter(testFile) { write("<game/>"); close() }

      val command = new LoadSelectedGameTuiCommand(mockController, 10, mockFactory) // invalid index

      val output = captureOutput {
        command.execute()
      }

      output should include("❌ Invalid selection.")

      // Cleanup
      testFile.delete()
      gamesDir.delete()
    }
  }

  private def captureOutput(body: => Unit): String = {
    val out = new java.io.ByteArrayOutputStream()
    Console.withOut(out)(body)
    out.toString.trim
  }
}