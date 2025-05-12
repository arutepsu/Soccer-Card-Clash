package de.htwg.se.soccercardclash.view.tui.tuiCommand.tuiCommandTypes

import de.htwg.se.soccercardclash.controller.{IController, IGameContextHolder}
import de.htwg.se.soccercardclash.view.tui.tuiCommand.base.ITuiCommand

import scala.util.{Failure, Success, Try}

class DoubleAttackTuiCommand(controller: IController, contextHolder: IGameContextHolder) extends ITuiCommand {
  override def execute(input: Option[String]): Unit = {
    input match {
      case Some(indexStr) =>
        Try(indexStr.toInt) match {
          case Success(position) =>
            println(s"⚔️ Executing attack on position: $position")
            controller.doubleAttack(position, contextHolder.get)
          case Failure(_) =>
            println("❌ Error: Invalid attack format! Expected `:attack <position>`.")
        }

      case None =>
        println("❌ Error: Missing position! Use `:attack <position>`.")
    }
  }
}

