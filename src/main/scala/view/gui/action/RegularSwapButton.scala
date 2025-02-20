package view.gui.action

import controller.IController
import model.playingFiledComponent.base.PlayingField
import view.gui.scenes.{AttackerHandScene, PlayingFieldScene}
import view.gui.components.sceneBar.GameStatusBar
import view.gui.components.sceneBar.GameStatusMessages
case class RegularSwapButton() extends ActionButton[AttackerHandScene] {
  override def execute(
                        controller: IController,
                        attackerHandScene: AttackerHandScene,
                        gameStatusBar: GameStatusBar): Unit = {
    attackerHandScene.attackerHandBar match {
      case Some(handBar) =>
        handBar.selectedCardIndex match {
          case Some(index) =>
            println(s"🔄 Swapping card at index: $index")
            controller.regularSwap(index)
            attackerHandScene.playingField.foreach(_.notifyObservers())
            handBar.updateBar()
          case None =>
            println("❌ No card selected to swap!")
        }
      case None => println("❌ No valid attacker hand available!")
    }
  }
}