package controller.command.actionCommandTypes.attackActionCommands

import controller.command.base.action.ActionCommand
import controller.command.memento.factory.IMementoManagerFactory
import model.gameComponent.IGame
import model.playingFiledComponent.manager.IActionManager
import scala.util.{Failure, Success, Try}
class DoubleAttackActionCommand(
                                 defenderIndex: Int,
                                 game: IGame,
                                 mementoManagerFactory: IMementoManagerFactory
                               ) extends ActionCommand(game, mementoManagerFactory) {
  private val actionManager: IActionManager = game.getActionManager
  private var attackSuccessful: Option[Boolean] = None

  override protected def executeAction(): Boolean = {
    val result = Try(actionManager.doubleAttack(defenderIndex))
    result match {
      case Success(value) =>
        attackSuccessful = Some(value)
        value
      case Failure(exception) =>
        attackSuccessful = Some(false)
        false
    }
  }
}
