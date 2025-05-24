package de.htwg.se.soccercardclash.view.tui.tuiCommand.tuiCommandTypes

import de.htwg.se.soccercardclash.controller.IController
import de.htwg.se.soccercardclash.util.IGameContextHolder
import de.htwg.se.soccercardclash.view.tui.tuiCommand.base.ITuiCommand

import scala.util.{Failure, Success, Try}

class BoostDefenderTuiCommand(controller: IController, contextHolder: IGameContextHolder) extends ITuiCommand {
  override def execute(input: Option[String]): Unit = {
    input.flatMap(_.toIntOption) match {
      case Some(index) =>
        controller.boostDefender(index, contextHolder.get)
      case None =>
        println("❌ Invalid input. Please enter a number.")
    }
  }
}
