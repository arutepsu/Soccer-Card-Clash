package de.htwg.se.soccercardclash.view.gui.components.actionButton

import de.htwg.se.soccercardclash.controller.IController
import de.htwg.se.soccercardclash.view.gui.components.sceneComponents.{GameStatusBar, GameStatusMessages}
import de.htwg.se.soccercardclash.view.gui.scenes.PlayingFieldScene
case class DoubleButton() extends ActionButton[PlayingFieldScene] {
  override def execute(
                        controller: IController,
                        playingFieldScene: PlayingFieldScene,
                        ): Unit = {

    val defenderFieldBar =
      if (playingFieldScene.playingField.getRoles.defender == playingFieldScene.player1)
        playingFieldScene.player1FieldBar
      else
        playingFieldScene.player2FieldBar
    val defenderCards = playingFieldScene.playingField.getDataManager.getPlayerDefenders(playingFieldScene.playingField.getRoles.defender)

    if (defenderCards.nonEmpty) {
      defenderFieldBar.selectedDefenderIndex match {
        case Some(defenderIndex) =>
          println(s"🔥 Attacking defender at index: $defenderIndex")
          controller.executeDoubleAttackCommand(defenderIndex)
          playingFieldScene.gameStatusBar.updateStatus(GameStatusMessages.ATTACK_INITIATED, playingFieldScene.playingField.getRoles.attacker.name, playingFieldScene.playingField.getRoles.defender.name)
          defenderFieldBar.resetSelectedDefender()

        case None =>
          println("⚠️ No defender selected for attack!")
          playingFieldScene.gameStatusBar.updateStatus(GameStatusMessages.NO_DEFENDER_SELECTED)
      }
    } else {
      println("⚽ All defenders are gone! Attacking the goalkeeper!")
      controller.executeDoubleAttackCommand(0)
      playingFieldScene.gameStatusBar.updateStatus(GameStatusMessages.ATTACK_INITIATED, playingFieldScene.playingField.getRoles.attacker.name, playingFieldScene.playingField.getRoles.defender.name)
    }
  }
}
