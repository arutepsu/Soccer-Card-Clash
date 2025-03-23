package de.htwg.se.soccercardclash.controller.command.actionCommandTypes.attackActionCommands

import de.htwg.se.soccercardclash.controller.command.base.action.ActionCommand
import de.htwg.se.soccercardclash.controller.command.memento.factory.IMementoManagerFactory
import de.htwg.se.soccercardclash.model.gameComponent.IGame
import de.htwg.se.soccercardclash.model.playingFiledComponent.manager.{IActionManager, IPlayerActionManager}

import scala.util.{Failure, Success, Try}

class DoubleAttackActionCommand(
                                 defenderIndex: Int,
                                 game: IGame,
                                 mementoManagerFactory: IMementoManagerFactory
                               ) extends ActionCommand(game, mementoManagerFactory) {
  private val actionManager: IActionManager = game.getActionManager
  private var attackSuccessful: Option[Boolean] = None
  private val playerActionService: IPlayerActionManager = game.getActionManager.getPlayerActionService
  override protected def executeAction(): Boolean = {
    val result = Try(actionManager.doubleAttack(defenderIndex, playerActionService))
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
