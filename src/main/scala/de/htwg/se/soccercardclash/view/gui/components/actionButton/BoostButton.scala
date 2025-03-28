package de.htwg.se.soccercardclash.view.gui.components.actionButton

import de.htwg.se.soccercardclash.controller.IController
import de.htwg.se.soccercardclash.view.gui.components.sceneComponents.{GameStatusBar, GameStatusMessages}
import de.htwg.se.soccercardclash.view.gui.scenes.{AttackerDefendersScene, PlayingFieldScene}
case class BoostButton() extends ActionButton[AttackerDefendersScene] {
  override def execute(
                        controller: IController,
                        attackerDefendersScene: AttackerDefendersScene,
                        ): Unit = {
    attackerDefendersScene.attackerDefenderField match {
      case Some(field) =>
        if (field.isGoalkeeperSelected) {
          println("⚽ Boosting Goalkeeper!")
          controller.boostGoalkeeper()
          attackerDefendersScene.playingFieldScene.gameStatusBar.updateStatus(GameStatusMessages.BOOST_PERFORMED)
        } else field.selectedCardIndex match {
          case Some(index) =>
            println(s"⚡ Boosting defender at index: $index")
            controller.boostDefender(index)
            attackerDefendersScene.playingFieldScene.gameStatusBar.updateStatus(GameStatusMessages.BOOST_PERFORMED)
          case None =>
            println("⚠️ No defender selected for boosting!")
        }
        
        attackerDefendersScene.playingField.foreach(_.notifyObservers())
        field.updateBar()

      case None => println("❌ No valid attacker/defender field to boost!")
    }
  }
}