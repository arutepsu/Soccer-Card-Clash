package de.htwg.se.soccercardclash.tui.tuiCommandTypes

import de.htwg.se.soccercardclash.controller.IController
import de.htwg.se.soccercardclash.view.tui.tuiCommand.tuiCommandTypes.DoubleAttackTuiCommand
import org.mockito.Mockito._
import org.mockito.ArgumentMatchers._
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import org.scalatestplus.mockito.MockitoSugar

import java.io.{ByteArrayOutputStream, PrintStream}
import scala.util.Try

class DoubleAttackTuiCommandTest extends AnyFlatSpec with Matchers with MockitoSugar {

  def captureOutput(block: => Unit): String = {
    val out = new ByteArrayOutputStream()
    Console.withOut(new PrintStream(out)) {
      block
    }
    out.toString.trim
  }

  "DoubleAttackTuiCommand" should "execute the controller's double attack command for valid input" in {
    val mockController = mock[IController]
    val command = new DoubleAttackTuiCommand(mockController)

    val output = captureOutput {
      command.execute(Some("2"))
    }

    verify(mockController).executeDoubleAttackCommand(2)
    output should include("⚔️ Executing attack on position: 2")
  }

  it should "print an error for non-integer input" in {
    val mockController = mock[IController]
    val command = new DoubleAttackTuiCommand(mockController)

    val output = captureOutput {
      command.execute(Some("notANumber"))
    }

    verify(mockController, never()).executeDoubleAttackCommand(anyInt())
    output should include("❌ Error: Invalid attack format")
  }

  it should "print an error for missing input" in {
    val mockController = mock[IController]
    val command = new DoubleAttackTuiCommand(mockController)

    val output = captureOutput {
      command.execute(None)
    }

    verify(mockController, never()).executeDoubleAttackCommand(anyInt())
    output should include("❌ Error: Missing position")
  }
}
