package view.gui.action

import controller.IController
import model.playingFiledComponent.base.PlayingField
import view.gui.scenes.PlayingFieldScene
import view.gui.components.sceneBar.GameStatusBar
import view.gui.components.sceneBar.GameStatusMessages
case class DoubleButton() extends ActionButton[PlayingFieldScene] {
  override def execute(
                        controller: IController,
                        playingFieldScene: PlayingFieldScene,
                        gameStatusBar: GameStatusBar): Unit = {

    val defenderFieldBar = if (playingFieldScene.playingField.getDefender == playingFieldScene.player1) playingFieldScene.player1FieldBar else playingFieldScene.player2FieldBar
    val defenderCards = playingFieldScene.playingField.dataManager.getPlayerDefenders(playingFieldScene.playingField.getDefender)

    if (defenderCards.nonEmpty) {
      defenderFieldBar.selectedDefenderIndex match {
        case Some(defenderIndex) =>
          println(s"🔥 Attacking defender at index: $defenderIndex")
          controller.executeDoubleAttackCommand(defenderIndex)
          playingFieldScene.updateDisplay()
          defenderFieldBar.resetSelectedDefender()

        case None =>
          println("⚠️ No defender selected for attack!")
          gameStatusBar.updateStatus(GameStatusMessages.NO_DEFENDER_SELECTED)
      }
    } else {
      println("⚽ All defenders are gone! Attacking the goalkeeper!")
      controller.executeDoubleAttackCommand(0)
    }
  }
}
