package controller.command.actionCommandTypes.attackActionCommands

import controller.command.base.action.ActionCommand
import model.playingFiledComponent.manager.IActionManager
import model.playingFiledComponent.strategy.attackStrategy.base.{DoubleAttackStrategy, SingleAttackStrategy}
import model.gameComponent.IGame
import scala.collection.mutable
import scala.util.{Try, Success, Failure}

class DoubleAttackActionCommand(defenderIndex: Int, game: IGame) extends ActionCommand(game) {
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
