package de.htwg.se.soccercardclash.view.tui.tuiCommand.tuiCommandTypes

import de.htwg.se.soccercardclash.controller.{IController, IGameContextHolder}
import de.htwg.se.soccercardclash.util.Events
import de.htwg.se.soccercardclash.view.tui.tuiCommand.base.ITuiCommand

class AttackTuiCommand(controller: IController, contextHolder: IGameContextHolder) extends ITuiCommand {
  override def execute(input: Option[String]): Unit = {
    input.flatMap(_.toIntOption) match {
      case Some(index) =>
        controller.singleAttack(index, contextHolder.get)
      case None =>
        println("❌ Invalid input. Please enter a number.")
    }
  }
}


