package controller.command.actionCommandTypes.gameStateCommands

import controller.command.base.action.ActionCommand
import model.playingFiledComponent.IPlayingField
import controller.command.ICommand
import model.playingFiledComponent.strategy.attackStrategy.base.{DoubleAttackStrategy, SingleAttackStrategy}
import model.gameComponent.IGame
import scala.util.{Try, Success, Failure}
class ResetGameCommand(game: IGame) extends ActionCommand(game) {
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
