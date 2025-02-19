package view.scenes.action
import controller.IController
import model.playingFiledComponent.PlayingField
import view.scenes.{AttackerDefendersScene, PlayingFieldScene}
import view.components.gameComponents.GameStatusBar
import view.components.gameComponents.GameStatusMessages
case class BoostButton() extends ActionButton[AttackerDefendersScene] {
  override def execute(
                        controller: IController,
                        attackerDefendersScene: AttackerDefendersScene,
                        gameStatusBar: GameStatusBar): Unit = {
    attackerDefendersScene.attackerDefenderField match {
      case Some(field) =>
        if (field.isGoalkeeperSelected) {
          println("⚽ Boosting Goalkeeper!")
          controller.boostGoalkeeper()
        } else field.selectedCardIndex match {
          case Some(index) =>
            println(s"⚡ Boosting defender at index: $index")
            controller.boostDefender(index)
          case None =>
            println("⚠️ No defender selected for boosting!")
        }
        
        attackerDefendersScene.playingField.foreach(_.notifyObservers())
        field.updateBar()

      case None => println("❌ No valid attacker/defender field to boost!")
    }
  }
}