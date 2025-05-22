package de.htwg.se.soccercardclash.view.gui.components.actionButton

import de.htwg.se.soccercardclash.controller.IController
import de.htwg.se.soccercardclash.view.gui.scenes.PlayingFieldScene
case class SingleButton() extends ActionButton[PlayingFieldScene] {
  override def execute(
                        controller: IController,
                        playingFieldScene: PlayingFieldScene
                      ): Unit = {

    val contextHolder = playingFieldScene.contextHolder
    val ctx = contextHolder.get

    val playingField = ctx.state
    val attacker = playingField.getRoles.attacker
    val defender = playingField.getRoles.defender

    val defenderCards = playingField.getGameCards.getPlayerDefenders(defender)

    val targetIndex = playingFieldScene.currentDefenderFieldBar
      .flatMap(_.selectedDefenderIndex)
      .orElse(if (defenderCards.isEmpty) Some(0) else None)

    targetIndex match {
      case Some(index) =>
        val (newCtx, success) = controller.singleAttack(index, ctx)
        if (success) {
          playingFieldScene.currentDefenderFieldBar.foreach(_.resetSelectedDefender())
        }
      case None =>
    }
  }
}
