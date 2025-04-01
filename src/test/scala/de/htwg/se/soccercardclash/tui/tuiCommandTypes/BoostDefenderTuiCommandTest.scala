package de.htwg.se.soccercardclash.tui.tuiCommandTypes

import de.htwg.se.soccercardclash.controller.IController
import de.htwg.se.soccercardclash.view.tui.tuiCommand.tuiCommandTypes.BoostDefenderTuiCommand
import org.mockito.Mockito._
import org.mockito.ArgumentMatchers._
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import org.scalatestplus.mockito.MockitoSugar

import java.io.{ByteArrayOutputStream, PrintStream}

class BoostDefenderTuiCommandTest extends AnyFlatSpec with Matchers with MockitoSugar {

  // Utility to capture printed output
  def captureOutput(block: => Unit): String = {
    val out = new ByteArrayOutputStream()
    Console.withOut(new PrintStream(out)) {
      block
    }
    out.toString.trim
  }

  "BoostDefenderTuiCommand" should "call controller.boostDefender with a valid integer input" in {
    val mockController = mock[IController]
    val command = new BoostDefenderTuiCommand(mockController)

    command.execute(Some("1"))

    verify(mockController).boostDefender(1)
  }

  it should "print an error message for non-numeric input" in {
    val mockController = mock[IController]
    val command = new BoostDefenderTuiCommand(mockController)

    val output = captureOutput {
      command.execute(Some("abc"))
    }

    verify(mockController, never()).boostDefender(anyInt())
    output should include("❌ Invalid input. Please enter a number.")
  }

  it should "print an error message for missing input" in {
    val mockController = mock[IController]
    val command = new BoostDefenderTuiCommand(mockController)

    val output = captureOutput {
      command.execute(None)
    }

    verify(mockController, never()).boostDefender(anyInt())
    output should include("❌ Invalid input. Please enter a number.")
  }
}
