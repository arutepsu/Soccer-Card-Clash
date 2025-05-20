package de.htwg.se.soccercardclash.view.gui.components.actionButton

import de.htwg.se.soccercardclash.controller.IController
import de.htwg.se.soccercardclash.view.gui.components.sceneComponents.{GameStatusBar, GameStatusMessages}
import de.htwg.se.soccercardclash.view.gui.scenes.{AttackerDefendersScene, PlayingFieldScene}
case class BoostButton() extends ActionButton[AttackerDefendersScene] {
  override def execute(
                        controller: IController,
                        attackerDefendersScene: AttackerDefendersScene
                      ): Unit = {

    attackerDefendersScene.attackerDefenderField match {
      case Some(field) =>
        val contextHolder = attackerDefendersScene.contextHolder
        val ctx = contextHolder.get

        if (field.isGoalkeeperSelected) {
          println("Boosting Goalkeeper!")
          val (newCtx, success) = controller.boostGoalkeeper(ctx)
          if (success) {
//            attackerDefendersScene.playingFieldScene.gameStatusBar.updateStatus(GameStatusMessages.BOOST_PERFORMED)
          }
        } else field.selectedDefenderIndex match {
          case Some(index) =>
            println(s"Boosting defender at index: $index")
            val (newCtx, success) = controller.boostDefender(index, ctx)
            if (success) {
//              attackerDefendersScene.playingFieldScene.gameStatusBar.updateStatus(GameStatusMessages.BOOST_PERFORMED)
            }
          case None =>
            println("No defender selected for boosting!")
        }

//        attackerDefendersScene.playingField.foreach(_.notifyObservers())
        field.updateBar(contextHolder.get.state)

      case None =>
        println("No valid attacker/defender field to boost!")
    }
  }
}
