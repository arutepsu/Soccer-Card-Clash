package de.htwg.se.soccercardclash.view.gui.components.actionButton

import de.htwg.se.soccercardclash.controller.IController
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
          val (newCtx, success) = controller.boostGoalkeeper(ctx)
          if (success) {
          }
        } else field.selectedDefenderIndex match {
          case Some(index) =>
            val (newCtx, success) = controller.boostDefender(index, ctx)
            if (success) {
            }
          case None =>
        }
        field.updateBar(contextHolder.get.state)

      case None =>
    }
  }
}
