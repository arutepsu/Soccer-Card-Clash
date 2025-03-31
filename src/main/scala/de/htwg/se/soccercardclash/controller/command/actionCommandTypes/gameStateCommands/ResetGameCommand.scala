package de.htwg.se.soccercardclash.controller.command.actionCommandTypes.gameStateCommands

import de.htwg.se.soccercardclash.controller.command.ICommand
import de.htwg.se.soccercardclash.controller.command.base.action.ActionCommand
import de.htwg.se.soccercardclash.model.gameComponent.state.memento.factory.IMementoManagerFactory
import de.htwg.se.soccercardclash.model.gameComponent.IGame
import de.htwg.se.soccercardclash.model.gameComponent.playingFiledComponent.IPlayingField


import scala.util.{Failure, Success, Try}

class ResetGameCommand(game: IGame,  mementoManagerFactory: IMementoManagerFactory) extends ActionCommand(game, mementoManagerFactory) {
  private var resetSuccessful: Option[Boolean] = None

  override protected def executeAction(): Boolean = {
    val result = Try(game.reset())
    result match {
      case Success(value) =>
        resetSuccessful = Some(value)
        value
      case Failure(exception) =>
        resetSuccessful = Some(false)
        false
    }
  }
}
