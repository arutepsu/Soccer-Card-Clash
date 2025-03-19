package view.gui.actionButtons

import controller.IController
import view.gui.scenes.{AttackerHandScene, PlayingFieldScene}
import view.gui.components.sceneView.GameStatusBar
import view.gui.components.sceneView.GameStatusMessages
case class RegularSwapButton() extends ActionButton[AttackerHandScene] {
  override def execute(
                        controller: IController,
                        attackerHandScene: AttackerHandScene,
                        ): Unit = {
    attackerHandScene.attackerHandBar match {
      case Some(handBar) =>
        handBar.selectedCardIndex match {
          case Some(index) =>
            controller.regularSwap(index)
            attackerHandScene.playingFieldScene.gameStatusBar.updateStatus(GameStatusMessages.REGULAR_SWAP_PERFORMED)
            attackerHandScene.playingField.foreach(_.notifyObservers())
            attackerHandScene.playingFieldScene.updateDisplay() // TODO: Fix later
            handBar.updateBar()
          case None =>
        }
      case None =>
    }
  }
}