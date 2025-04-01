package de.htwg.se.soccercardclash.tui.tuiCommandTypes

import de.htwg.se.soccercardclash.controller.IController
import de.htwg.se.soccercardclash.view.tui.tuiCommand.tuiCommandTypes.ReverseSwapTuiCommand
import org.mockito.Mockito._
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import org.scalatestplus.mockito.MockitoSugar

class ReverseSwapTuiCommandTest extends AnyFlatSpec with Matchers with MockitoSugar {

  "ReverseSwapTuiCommand" should "call controller.reverseSwap when executed" in {
    val mockController = mock[IController]
    val command = new ReverseSwapTuiCommand(mockController)

    command.execute(Some("anything")) // should be ignored
    verify(mockController).reverseSwap()
  }

  it should "still call reverseSwap when input is None" in {
    val mockController = mock[IController]
    val command = new ReverseSwapTuiCommand(mockController)

    command.execute(None)
    verify(mockController).reverseSwap()
  }
}
