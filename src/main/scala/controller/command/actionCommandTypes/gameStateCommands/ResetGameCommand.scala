package controller.command.actionCommandTypes.gameStateCommands

import controller.command.base.action.ActionCommand
import model.playingFiledComponent.IPlayingField
import controller.command.ICommand
import model.playingFiledComponent.strategy.attackStrategy.base.{DoubleAttackStrategy, SingleAttackStrategy}
import model.gameComponent.IGame

class ResetGameCommand(game: IGame) extends ActionCommand(game) {
  private var resetSuccessful: Option[Boolean] = None

  override protected def executeAction(): Boolean = {
    resetSuccessful = Some(game.reset())
    resetSuccessful.getOrElse(false)
  }
}
