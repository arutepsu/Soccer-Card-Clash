package de.htwg.se.soccercardclash.tui.tuiCommandTypes

import de.htwg.se.soccercardclash.view.tui.tuiCommand.tuiCommandTypes.WorkflowTuiCommand
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class WorkflowTuiCommandTest extends AnyFlatSpec with Matchers {

  "WorkflowTuiCommand" should "execute the given controllerAction" in {
    var called = false
    val action = () => called = true

    val command = new WorkflowTuiCommand(action)
    command.execute()

    called shouldBe true
  }
}
