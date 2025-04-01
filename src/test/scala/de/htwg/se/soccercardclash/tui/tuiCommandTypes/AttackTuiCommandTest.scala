package de.htwg.se.soccercardclash.tui.tuiCommandTypes

import de.htwg.se.soccercardclash.controller.IController
import de.htwg.se.soccercardclash.view.tui.tuiCommand.base.ITuiCommand
import de.htwg.se.soccercardclash.view.tui.tuiCommand.tuiCommandTypes.AttackTuiCommand
import org.mockito.Mockito.*
import org.mockito.ArgumentMatchers.*
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import org.scalatestplus.mockito.MockitoSugar

import java.io.{ByteArrayOutputStream, PrintStream}

class AttackTuiCommandTest extends AnyFlatSpec with Matchers with MockitoSugar {

  // Helper to capture console output
  def captureOutput(block: => Unit): String = {
    val out = new ByteArrayOutputStream()
    Console.withOut(new PrintStream(out)) {
      block
    }
    out.toString.trim
  }

  "AttackTuiCommand" should "execute the controller's attack command with a valid integer input" in {
    val mockController = mock[IController]
    val command = new AttackTuiCommand(mockController)

    command.execute(Some("2"))

    verify(mockController).executeSingleAttackCommand(2)
  }

  it should "print an error for invalid (non-integer) input" in {
    val mockController = mock[IController]
    val command = new AttackTuiCommand(mockController)

    val output = captureOutput {
      command.execute(Some("notANumber"))
    }

    verify(mockController, never()).executeSingleAttackCommand(anyInt())
    output should include("❌ Invalid input. Please enter a number.")
  }

  it should "print an error when input is None" in {
    val mockController = mock[IController]
    val command = new AttackTuiCommand(mockController)

    val output = captureOutput {
      command.execute(None)
    }

    verify(mockController, never()).executeSingleAttackCommand(anyInt())
    output should include("❌ Invalid input. Please enter a number.")
  }
}
