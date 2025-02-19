package view.scenes.action
import controller.IController
import model.playingFiledComponent.PlayingField
import view.scenes.{AttackerHandScene, PlayingFieldScene}
import view.components.gameComponents.GameStatusBar
import view.components.gameComponents.GameStatusMessages
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