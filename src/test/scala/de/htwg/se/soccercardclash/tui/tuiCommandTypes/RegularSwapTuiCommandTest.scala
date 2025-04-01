package de.htwg.se.soccercardclash.tui.tuiCommandTypes

import de.htwg.se.soccercardclash.controller.IController
import de.htwg.se.soccercardclash.view.tui.tuiCommand.tuiCommandTypes.RegularSwapTuiCommand
import org.mockito.Mockito._
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import org.scalatestplus.mockito.MockitoSugar
import org.mockito.ArgumentMatchers.any

import java.io.{ByteArrayOutputStream, PrintStream}

class RegularSwapTuiCommandTest extends AnyFlatSpec with Matchers with MockitoSugar {

  def captureOutput(block: => Unit): String = {
    val out = new ByteArrayOutputStream()
    Console.withOut(new PrintStream(out)) {
      block
    }
    out.toString.trim
  }

  "RegularSwapTuiCommand" should "call controller.regularSwap with valid input" in {
    val mockController = mock[IController]
    val command = new RegularSwapTuiCommand(mockController)

    command.execute(Some("2"))

    verify(mockController).regularSwap(2)
  }

  it should "print an error for invalid input" in {
    val mockController = mock[IController]
    val command = new RegularSwapTuiCommand(mockController)

    val output = captureOutput {
      command.execute(Some("not-a-number"))
    }

    output should include("❌ Invalid input. Please enter a number.")
    verify(mockController, never()).regularSwap(any[Int])
  }

  it should "print an error when input is None" in {
    val mockController = mock[IController]
    val command = new RegularSwapTuiCommand(mockController)

    val output = captureOutput {
      command.execute(None)
    }

    output should include("❌ Invalid input. Please enter a number.")
    verify(mockController, never()).regularSwap(any[Int])
  }
}
