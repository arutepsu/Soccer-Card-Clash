package controller.command.actionCommandTypes.attackActionCommands

import controller.command.base.action.ActionCommand
import model.playingFiledComponent.manager.IActionManager
import model.playingFiledComponent.strategy.attackStrategy.base.{DoubleAttackStrategy, SingleAttackStrategy}
import model.gameComponent.IGame
import scala.collection.mutable

class DoubleAttackActionCommand(defenderIndex: Int, game: IGame) extends ActionCommand(game) {
  private val actionManager: IActionManager = game.getActionManager
  private var attackSuccessful: Option[Boolean] = None // Store attack result

  override protected def executeAction(): Unit = {
    attackSuccessful = Some(actionManager.doubleAttack(defenderIndex))
  }

  def wasAttackSuccessful: Boolean = attackSuccessful.getOrElse(false)
}
