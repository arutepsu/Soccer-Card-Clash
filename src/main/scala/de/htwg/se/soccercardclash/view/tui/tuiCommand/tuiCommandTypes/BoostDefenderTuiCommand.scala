package de.htwg.se.soccercardclash.view.tui.tuiCommand.tuiCommandTypes

import de.htwg.se.soccercardclash.controller.IController
import de.htwg.se.soccercardclash.util.Events
import de.htwg.se.soccercardclash.view.tui.tuiCommand.base.ITuiCommand
import scala.util.{Try, Success, Failure}

class BoostDefenderTuiCommand(controller: IController) extends ITuiCommand {
  override def execute(input: Option[String]): Unit = {
    input.flatMap(_.toIntOption) match {
      case Some(index) =>
        controller.boostDefender(index)
      case None =>
        println("❌ Invalid input. Please enter a number.")
    }
  }
}
