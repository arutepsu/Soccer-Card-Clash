package de.htwg.se.soccercardclash.view.gui.components.actionButton

import de.htwg.se.soccercardclash.controller.IController
import de.htwg.se.soccercardclash.view.gui.components.sceneComponents.{GameStatusBar, GameStatusMessages}
import de.htwg.se.soccercardclash.view.gui.scenes.{AttackerHandScene, PlayingFieldScene}
class ReverseSwapButton extends ActionButton[AttackerHandScene] {
  override def execute(controller: IController, attackerHandScene: AttackerHandScene): Unit = {
    val contextHolder = attackerHandScene.contextHolder
    val ctx = contextHolder.get

    val (newCtx, success) = controller.reverseSwap(ctx)
    if (success) {
//      attackerHandScene.playingFieldScene.gameStatusBar.updateStatus(GameStatusMessages.REVERSE_SWAP_PERFORMED)
      attackerHandScene.attackerHandBar.updateBar(newCtx.state)
    }
  }
}
