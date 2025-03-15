package controller.command.actionCommandTypes.attackActionCommands
import controller.command.ICommand
import controller.command.base.action.ActionCommand
import model.playingFiledComponent.manager.IActionManager
import model.gameComponent.IGame
class SingleAttackActionCommand(defenderIndex: Int, game: IGame) extends ActionCommand(game) {
  private val actionManager: IActionManager = game.getActionManager
  private var attackSuccessful: Option[Boolean] = None

  override protected def executeAction(): Boolean = {
    attackSuccessful = Some(actionManager.singleAttack(defenderIndex))
    attackSuccessful.getOrElse(false) // Ensure executeAction() returns a Boolean
  }

  def wasAttackSuccessful: Boolean = attackSuccessful.getOrElse(false)
}
