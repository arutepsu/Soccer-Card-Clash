package de.htwg.se.soccercardclash.view.tui.tuiCommand.tuiCommandTypes

import de.htwg.se.soccercardclash.view.tui.tuiCommand.base.ITuiCommand
class WorkflowTuiCommand(controllerAction: () => Unit) extends ITuiCommand {
  override def execute(input: Option[String] = None): Unit = {
    controllerAction()
  }
}
