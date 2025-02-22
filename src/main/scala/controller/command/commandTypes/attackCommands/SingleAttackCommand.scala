package controller.command.commandTypes.attackCommands
import controller.command.ICommand
import controller.command.base.Command
import model.playingFiledComponent.manager.IActionManager

class SingleAttackCommand(defenderIndex: Int, actionManager: IActionManager) extends Command(actionManager) {
  private var attackSuccessful: Boolean = false

  override protected def executeAction(): Unit = {
    attackSuccessful = actionManager.attack(defenderIndex)
  }
}