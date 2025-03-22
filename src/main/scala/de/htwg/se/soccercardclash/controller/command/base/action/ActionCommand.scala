package de.htwg.se.soccercardclash.controller.command.base.action

import de.htwg.se.soccercardclash.controller.command.ICommand
import de.htwg.se.soccercardclash.controller.command.memento.componenets.IMementoRestorer
import de.htwg.se.soccercardclash.controller.command.memento.IMementoManager
import de.htwg.se.soccercardclash.controller.command.memento.base.{Memento, MementoManager}
import de.htwg.se.soccercardclash.controller.command.memento.factory.IMementoManagerFactory
import de.htwg.se.soccercardclash.model.gameComponent.IGame
import de.htwg.se.soccercardclash.model.playingFiledComponent.manager.IActionManager


abstract class ActionCommand(
                              val game: IGame,
                              mementoManagerFactory: IMementoManagerFactory
                            ) extends ICommand {

  protected val actionManager: IActionManager = game.getActionManager
  protected var mementoManager: IMementoManager = mementoManagerFactory.create(game)
  protected var memento: Option[Memento] = None

  override def doStep(): Boolean = {
    val preActionState = mementoManager.createMemento()

    if (executeAction()) {
      memento = Some(preActionState)
      game.updateGameState()
      true
    } else {
      false
    }
  }

  override def undoStep(): Unit = {
    memento match {
      case Some(savedMemento) =>
        mementoManager.restoreGameState(savedMemento)
        game.updateGameState()
      case None =>
    }
  }

  override def redoStep(): Unit = {
    memento match {
      case Some(_) =>
        val success = doStep()
      case None =>
    }
  }

  protected def executeAction(): Boolean
}
