package controller.command.actionCommandTypes.attackActionCommands

import controller.command.base.action.ActionCommand
import model.playingFiledComponent.manager.IActionManager
import model.playingFiledComponent.strategy.attackStrategy.base.{DoubleAttackStrategy, SingleAttackStrategy}
import model.gameComponent.IGame
import scala.collection.mutable

class DoubleAttackActionCommand(defenderIndex: Int, game: IGame) extends ActionCommand(game) {
  private val actionManager: IActionManager = game.getActionManager
  private var attackSuccessful: Option[Boolean] = None

  override protected def executeAction(): Boolean = {
    attackSuccessful = Some(actionManager.doubleAttack(defenderIndex))
    attackSuccessful.getOrElse(false)
  }

  def wasAttackSuccessful: Boolean = attackSuccessful.getOrElse(false)
}
