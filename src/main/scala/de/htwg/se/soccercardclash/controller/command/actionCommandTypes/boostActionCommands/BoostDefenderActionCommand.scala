package de.htwg.se.soccercardclash.controller.command.actionCommandTypes.boostActionCommands

import de.htwg.se.soccercardclash.controller.command.ICommand
import de.htwg.se.soccercardclash.controller.command.base.action.ActionCommand
import de.htwg.se.soccercardclash.model.gameComponent.state.memento.factory.IMementoManagerFactory
import de.htwg.se.soccercardclash.model.gameComponent.IGame
import de.htwg.se.soccercardclash.model.playingFiledComponent.manager.{IActionManager, IPlayerActionManager}
import de.htwg.se.soccercardclash.util.Events

import scala.util.{Failure, Success, Try}

class BoostDefenderActionCommand(cardIndex: Int, game: IGame,  mementoManagerFactory: IMementoManagerFactory) extends ActionCommand(game, mementoManagerFactory) {
  private val actionManager: IActionManager = game.getActionManager
  protected var boostSuccessful: Option[Boolean] = None
  override protected def executeAction(): Boolean = {
    val result = Try(actionManager.boostDefender(cardIndex, actionManager.getPlayerActionService))
    result match {
      case Success(value) =>
        boostSuccessful = Some(value)
        value
      case Failure(exception) =>
        boostSuccessful = Some(false)
        false
    }
  }
  override def undoStep(): Unit = {
    memento.foreach(m => mementoManager.restoreBoosts(m, cardIndex))
  }

  override def redoStep(): Unit = {
    memento match {
      case Some(savedMemento) if boostSuccessful.contains(true) =>
        mementoManager.restoreGameState(savedMemento)
        executeAction()
      case _ =>
    }
  }
  
}
