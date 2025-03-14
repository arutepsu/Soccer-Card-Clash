package view.gui.action

import controller.IController
import view.gui.scenes.{AttackerHandScene, PlayingFieldScene}
import view.gui.components.sceneBar.GameStatusBar
import view.gui.components.sceneBar.GameStatusMessages
case class RegularSwapButton() extends ActionButton[AttackerHandScene] {
  override def execute(
                        controller: IController,
                        attackerHandScene: AttackerHandScene,
                        ): Unit = {
    attackerHandScene.attackerHandBar match {
      case Some(handBar) =>
        handBar.selectedCardIndex match {
          case Some(index) =>
            println(s"🔄 Swapping card at index: $index")
            controller.regularSwap(index)
            attackerHandScene.playingFieldScene.gameStatusBar.updateStatus(GameStatusMessages.REGULAR_SWAP_PERFORMED)
            attackerHandScene.playingField.foreach(_.notifyObservers())
            handBar.updateBar()
          case None =>
            println("❌ No card selected to swap!")
        }
      case None => println("❌ No valid attacker hand available!")
    }
  }
}