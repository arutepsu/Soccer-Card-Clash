package de.htwg.se.soccercardclash.view.tui.tuiCommand.tuiCommandTypes

import de.htwg.se.soccercardclash.controller.IController
import de.htwg.se.soccercardclash.util.Events
import de.htwg.se.soccercardclash.view.tui.tuiCommand.base.ITuiCommand

class AttackTuiCommand(controller: IController) extends ITuiCommand {
  override def execute(input: Option[String]): Unit = {
    input.flatMap(_.toIntOption) match {
      case Some(index) =>
        controller.executeSingleAttackCommand(index)
      case None =>
        println("❌ Invalid input. Please enter a number.")
    }
  }
}


