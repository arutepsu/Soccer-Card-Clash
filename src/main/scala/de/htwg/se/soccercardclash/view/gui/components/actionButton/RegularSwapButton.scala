package de.htwg.se.soccercardclash.view.gui.components.actionButton

import de.htwg.se.soccercardclash.controller.IController
import de.htwg.se.soccercardclash.view.gui.scenes.{AttackerHandScene, PlayingFieldScene}
case class RegularSwapButton() extends ActionButton[AttackerHandScene] {
  override def execute(controller: IController, attackerHandScene: AttackerHandScene): Unit = {
    val contextHolder = attackerHandScene.getContextHolder
    val ctx = contextHolder.get

    attackerHandScene.attackerHandBar.selectedCardIndex match {
      case Some(index) =>
        val (newCtx, success) = controller.regularSwap(index, ctx)
        if (success) {
          attackerHandScene.attackerHandBar.updateBar(newCtx.state)
        }
      case None =>
    }
  }
}
