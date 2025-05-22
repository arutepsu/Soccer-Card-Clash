package de.htwg.se.soccercardclash.view.gui.components.actionButton

import de.htwg.se.soccercardclash.controller.IController
import de.htwg.se.soccercardclash.view.gui.scenes.{AttackerHandScene, PlayingFieldScene}
class ReverseSwapButton extends ActionButton[AttackerHandScene] {
  override def execute(controller: IController, attackerHandScene: AttackerHandScene): Unit = {
    val contextHolder = attackerHandScene.getContextHolder
    val ctx = contextHolder.get

    val (newCtx, success) = controller.reverseSwap(ctx)
    if (success) {
      attackerHandScene.attackerHandBar.updateBar(newCtx.state)
    }
  }
}
