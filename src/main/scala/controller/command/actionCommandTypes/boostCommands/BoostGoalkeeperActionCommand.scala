package controller.command.actionCommandTypes.boostCommands

import controller.command.ICommand
import controller.command.base.action.ActionCommand
import model.playingFiledComponent.manager.IActionManager
import model.gameComponent.IGame

class BoostGoalkeeperActionCommand(game: IGame) extends ActionCommand(game) {

  override protected def executeAction(): Unit = {

  }
}