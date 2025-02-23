package view.tui.tuiCommand.tuiCommandTypes
import view.tui.tuiCommand.base.ITuiCommand
class WorkflowTuiCommand(controllerAction: () => Unit) extends ITuiCommand {
  override def execute(input: Option[String] = None): Unit = {
    controllerAction()
  }
}
