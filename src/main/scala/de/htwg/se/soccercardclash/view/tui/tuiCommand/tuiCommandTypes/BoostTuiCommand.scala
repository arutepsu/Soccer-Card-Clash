package de.htwg.se.soccercardclash.view.tui.tuiCommand.tuiCommandTypes

import de.htwg.se.soccercardclash.controller.IController
import de.htwg.se.soccercardclash.util.Events
import de.htwg.se.soccercardclash.view.tui.tuiCommand.base.ITuiCommand
import scala.util.{Try, Success, Failure}

class BoostTuiCommand(controller: IController) extends ITuiCommand {
  override def execute(input: Option[String]): Unit = {
    val attacker = controller.getCurrentGame.getPlayingField.getRoles.attacker
    val defenders = controller.getCurrentGame.getPlayingField.getDataManager.getPlayerDefenders(attacker)

    if (defenders.isEmpty) {
      println("❌ No defenders available to boost!")
    } else {
      println(s"⚡ Available defenders for ${attacker.name}:")
      defenders.zipWithIndex.foreach { case (defender, index) =>
        println(s"[$index] ${defender}")
      }

      input match {
        case Some(indexStr) =>
          Try(indexStr.toInt) match {
            case Success(position) if position < defenders.size =>
              println(s"✅ Boosting defender at position: $position")
              controller.boostDefender(position)
            case Success(_) =>
              println("❌ Invalid index! Choose from the available defenders.")
            case Failure(_) =>
              println("❌ Error: Invalid boost format! Expected `:boost <position>`.")
          }
        case None =>
          println("❌ Error: Missing position! Use `:boost <position>`.")
      }
    }
  }
}
