package view.scenes.action
import controller.IController
import model.playingFiledComponent.PlayingField
import view.scenes.{AttackerHandScene, PlayingFieldScene}
import view.components.gameComponents.GameStatusBar
import view.components.gameComponents.GameStatusMessages
class CircularSwapButton extends ActionButton[AttackerHandScene] {
  override def execute(
                        controller: IController,
                        attackerHandScene: AttackerHandScene,
                        gameStatusBar: GameStatusBar
                      ): Unit = {
    attackerHandScene.attackerHandBar match {
      case Some(handBar) =>
        handBar.selectedCardIndex match {
          case Some(index) =>
            println(s"🔄 Performing Circular Swap on card at index: $index")
            controller.circularSwap(index) // ✅ Calls the controller's circular swap logic
            attackerHandScene.playingField.foreach(_.notifyObservers()) // ✅ Updates UI
            handBar.updateBar() // ✅ Refresh UI after swapping
          case None =>
            println("❌ No card selected for circular swap!")
            gameStatusBar.updateStatus(GameStatusMessages.NO_CARD_SELECTED)
        }
      case None =>
        println("❌ No valid attacker hand available!")
    }
  }
}