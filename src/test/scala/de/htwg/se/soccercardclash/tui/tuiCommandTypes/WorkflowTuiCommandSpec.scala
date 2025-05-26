package de.htwg.se.soccercardclash.tui.tuiCommandTypes

import de.htwg.se.soccercardclash.view.tui.tuiCommand.tuiCommandTypes.WorkflowTuiCommand
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import org.mockito.Mockito.*
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.mockito.MockitoSugar

class WorkflowTuiCommandSpec extends AnyWordSpec with Matchers with MockitoSugar {

  "WorkflowTuiCommand" should {

    "execute the provided controller action" in {
      var called = false
      val action = () => called = true

      val cmd = new WorkflowTuiCommand(action)
      cmd.execute() // default param = None

      called shouldBe true
    }

    "still execute the action even if input is provided" in {
      var called = false
      val action = () => called = true

      val cmd = new WorkflowTuiCommand(action)
      cmd.execute(Some("ignored"))

      called shouldBe true
    }
  }
}
