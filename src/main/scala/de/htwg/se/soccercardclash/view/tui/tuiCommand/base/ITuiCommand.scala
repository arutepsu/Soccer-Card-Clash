package de.htwg.se.soccercardclash.view.tui.tuiCommand.base

trait ITuiCommand {
  def execute(input: Option[String] = None): Unit
}
