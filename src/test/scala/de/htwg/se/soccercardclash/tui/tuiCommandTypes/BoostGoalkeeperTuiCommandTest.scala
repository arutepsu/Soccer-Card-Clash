package de.htwg.se.soccercardclash.tui.tuiCommandTypes

import de.htwg.se.soccercardclash.controller.IController
import de.htwg.se.soccercardclash.view.tui.tuiCommand.tuiCommandTypes.BoostGoalkeeperTuiCommand
import org.mockito.Mockito._
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import org.scalatestplus.mockito.MockitoSugar

class BoostGoalkeeperTuiCommandTest extends AnyFlatSpec with Matchers with MockitoSugar {

  "BoostGoalkeeperTuiCommand" should "call controller.boostGoalkeeper() regardless of input" in {
    val mockController = mock[IController]
    val command = new BoostGoalkeeperTuiCommand(mockController)

    // Call with None
    command.execute(None)
    verify(mockController).boostGoalkeeper()

    // Call with Some input (even if it's invalid or unused)
    command.execute(Some("anything"))
    verify(mockController, times(2)).boostGoalkeeper()
  }
}
