package de.htwg.se.soccercardclash.view.tui.tuiCommand.tuiCommandTypes

import de.htwg.se.soccercardclash.controller.IController
import de.htwg.se.soccercardclash.view.tui.tuiCommand.base.ITuiCommand

class BoostGoalkeeperTuiCommand(controller: IController) extends ITuiCommand {

  override def execute(input: Option[String]): Unit = {
    controller.boostGoalkeeper()
  }
}