package view.gui.components.actionButton

import controller.IController
import view.gui.components.sceneView.{GameStatusBar, GameStatusMessages}
import view.gui.scenes.PlayingFieldScene
case class DoubleButton() extends ActionButton[PlayingFieldScene] {
  override def execute(
                        controller: IController,
                        playingFieldScene: PlayingFieldScene,
                        ): Unit = {

    val defenderFieldBar =
      if (playingFieldScene.playingField.getDefender == playingFieldScene.player1)
        playingFieldScene.player1FieldBar
      else
        playingFieldScene.player2FieldBar
    val defenderCards = playingFieldScene.playingField.getDataManager.getPlayerDefenders(playingFieldScene.playingField.getDefender)

    if (defenderCards.nonEmpty) {
      defenderFieldBar.selectedDefenderIndex match {
        case Some(defenderIndex) =>
          println(s"🔥 Attacking defender at index: $defenderIndex")
          controller.executeDoubleAttackCommand(defenderIndex)
          playingFieldScene.gameStatusBar.updateStatus(GameStatusMessages.ATTACK_INITIATED, playingFieldScene.playingField.getAttacker.name, playingFieldScene.playingField.getDefender.name)
          defenderFieldBar.resetSelectedDefender()

        case None =>
          println("⚠️ No defender selected for attack!")
          playingFieldScene.gameStatusBar.updateStatus(GameStatusMessages.NO_DEFENDER_SELECTED)
      }
    } else {
      println("⚽ All defenders are gone! Attacking the goalkeeper!")
      controller.executeDoubleAttackCommand(0)
      playingFieldScene.gameStatusBar.updateStatus(GameStatusMessages.ATTACK_INITIATED, playingFieldScene.playingField.getAttacker.name, playingFieldScene.playingField.getDefender.name)
    }
  }
}
