package de.htwg.se.soccercardclash.view.gui.components.actionButton

import de.htwg.se.soccercardclash.controller.IController
import de.htwg.se.soccercardclash.view.gui.components.sceneComponents.{GameStatusBar, GameStatusMessages}
import de.htwg.se.soccercardclash.view.gui.scenes.{AttackerHandScene, PlayingFieldScene}
case class RegularSwapButton() extends ActionButton[AttackerHandScene] {
  override def execute(controller: IController, attackerHandScene: AttackerHandScene): Unit = {
    val contextHolder = attackerHandScene.playingFieldScene.contextHolder
    val ctx = contextHolder.get

    attackerHandScene.attackerHandBar.selectedCardIndex match {
      case Some(index) =>
        val (newCtx, success) = controller.regularSwap(index, ctx)
        if (success) {
          contextHolder.set(newCtx)
          attackerHandScene.playingFieldScene.gameStatusBar.updateStatus(GameStatusMessages.REGULAR_SWAP_PERFORMED)
          attackerHandScene.playingFieldScene.updateDisplay()
          attackerHandScene.attackerHandBar.updateBar(newCtx.state)
        }
      case None =>
        println("⚠️ No card selected for swap!")
        attackerHandScene.playingFieldScene.gameStatusBar.updateStatus(GameStatusMessages.NO_CARD_SELECTED)
    }
  }
}
