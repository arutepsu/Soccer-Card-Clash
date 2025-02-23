package view.tui

trait ITuiCommand {
  def execute(input: Option[String] = None): Unit
}
