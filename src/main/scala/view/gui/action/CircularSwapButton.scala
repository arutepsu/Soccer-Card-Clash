package view.gui.action

import controller.IController
import view.gui.components.sceneBar.GameStatusBar
import view.gui.components.sceneBar.GameStatusMessages
import view.gui.scenes.{AttackerHandScene, PlayingFieldScene}
class CircularSwapButton extends ActionButton[AttackerHandScene] {
  override def execute(
                        controller: IController,
                        attackerHandScene: AttackerHandScene,
                      ): Unit = {
    attackerHandScene.attackerHandBar match {
      case Some(handBar) =>
        handBar.selectedCardIndex match {
          case Some(index) =>
            println(s"🔄 Performing Circular Swap on card at index: $index")
            controller.circularSwap(index)
            attackerHandScene.playingFieldScene.gameStatusBar.updateStatus(GameStatusMessages.CIRCULAR_SWAP_PERFORMED)
            attackerHandScene.playingField.foreach(_.notifyObservers())
            handBar.updateBar() 
          case None =>
            println("❌ No card selected for circular swap!")
            attackerHandScene.getPlayingField
        }
      case None =>
        println("❌ No valid attacker hand available!")
    }
  }
}