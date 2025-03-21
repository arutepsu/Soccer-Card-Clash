package controller.command.actionCommandTypes.gameStateCommands

import controller.command.ICommand
import controller.command.base.action.ActionCommand
import controller.command.memento.factory.IMementoManagerFactory
import model.gameComponent.IGame
import model.playingFiledComponent.IPlayingField

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
