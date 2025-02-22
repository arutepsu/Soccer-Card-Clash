package view.tui

trait TuiCommand {
  def execute(input: Option[String] = None): Unit
}
