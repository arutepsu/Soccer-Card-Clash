package view.tui.tuiCommand.base

trait ITuiCommand {
  def execute(input: Option[String] = None): Unit
}
