package view.gui.actionButtons

import controller.IController
import view.gui.components.sceneView.GameStatusBar
import view.gui.components.sceneView.GameStatusMessages
import view.gui.scenes.{AttackerHandScene, PlayingFieldScene}
class ReverseSwapButton extends ActionButton[AttackerHandScene] {
  override def execute(
                        controller: IController,
                        attackerHandScene: AttackerHandScene,
                      ): Unit = {
    attackerHandScene.attackerHandBar match {
      case Some(handBar) =>
        println(s"🔄 Performing Reverse Swap on entire hand!") // ✅ No index required
        controller.reverseSwap() // ✅ Swap all cards immediately
        attackerHandScene.playingFieldScene.gameStatusBar.updateStatus(GameStatusMessages.REVERSE_SWAP_PERFORMED)
        attackerHandScene.playingField.foreach(_.notifyObservers())
        handBar.updateBar() // ✅ Ensure hand UI updates
      case None =>
        println("❌ No valid attacker hand available!")
    }
  }
}
