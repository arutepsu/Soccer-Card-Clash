package de.htwg.se.soccercardclash.view.gui.components.actionButton

import de.htwg.se.soccercardclash.controller.IController
import de.htwg.se.soccercardclash.view.gui.components.sceneComponents.{GameStatusBar, GameStatusMessages}
import de.htwg.se.soccercardclash.view.gui.scenes.PlayingFieldScene

case class DoubleButton() extends ActionButton[PlayingFieldScene] {
  override def execute(
                        controller: IController,
                        playingFieldScene: PlayingFieldScene
                      ): Unit = {

    val contextHolder = playingFieldScene.contextHolder
    val ctx = contextHolder.get

    val state = ctx.state
    val defender = state.getRoles.defender
    val defenderCards = state.getDataManager.getPlayerDefenders(defender)

    val maybeIndex = if (defenderCards.nonEmpty) {
      playingFieldScene.currentDefenderFieldBar.flatMap(_.selectedDefenderIndex)
    } else {
      Some(0)
    }

    maybeIndex match {
      case Some(defenderIndex) =>
        val (newCtx, success) = controller.doubleAttack(defenderIndex, ctx)
        if (success) {
          playingFieldScene.gameStatusBar.updateStatus(
            GameStatusMessages.ATTACK_INITIATED,
            newCtx.state.getRoles.attacker.name,
            newCtx.state.getRoles.defender.name
          )
          playingFieldScene.currentDefenderFieldBar.foreach(_.resetSelectedDefender())
        }

      case None =>
        println("No defender selected for double attack!")
        playingFieldScene.gameStatusBar.updateStatus(GameStatusMessages.NO_DEFENDER_SELECTED)
    }
  }
}

