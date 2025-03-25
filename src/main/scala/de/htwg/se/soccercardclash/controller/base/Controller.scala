package de.htwg.se.soccercardclash.controller.base

import com.google.inject.Inject
import de.htwg.se.soccercardclash.controller.command.ICommand
import de.htwg.se.soccercardclash.controller.command.factory.ICommandFactory
import de.htwg.se.soccercardclash.controller.IController
import de.htwg.se.soccercardclash.model.gameComponent.IGame
import de.htwg.se.soccercardclash.model.playerComponent.IPlayer
import de.htwg.se.soccercardclash.model.playingFiledComponent.IPlayingField
import de.htwg.se.soccercardclash.util.{Events, Observer, UndoManager}


class Controller @Inject()(private val game: IGame, private val commandFactory: ICommandFactory) extends IController {
  private val undoManager = new UndoManager

  override def getCurrentGame: IGame = game

  override def undo(): Unit = {
    undoManager.undoStep()
    notifyObservers(Events.Undo)
  }

  override def redo(): Unit = {
    undoManager.redoStep()
    notifyObservers(Events.Redo)
  }

  override def executeSingleAttackCommand(defenderPosition: Int): Unit = {
    executeCommand(commandFactory.createSingleAttackCommand(defenderPosition), Events.RegularAttack)
  }

  override def executeDoubleAttackCommand(defenderPosition: Int): Unit = {
    executeCommand(commandFactory.createDoubleAttackCommand(defenderPosition), Events.DoubleAttack)
  }

  override def boostDefender(defenderPosition: Int): Unit = {
    executeCommand(commandFactory.createBoostDefenderCommand(defenderPosition), Events.BoostDefender)
  }

  override def boostGoalkeeper(): Unit = {
    executeCommand(commandFactory.createBoostGoalkeeperCommand(), Events.BoostGoalkeeper)
  }

  override def regularSwap(index: Int): Unit = {
    executeCommand(commandFactory.createRegularSwapCommand(index), Events.RegularSwap)
  }

  override def reverseSwap(): Unit = {
    executeCommand(commandFactory.createReverseSwapCommand(), Events.ReverseSwap)
  }

  override def createGame(player1: String, player2: String): Unit =
    executeCommand(commandFactory.createCreateGameCommand(game, player1, player2), Events.PlayingField)

  override def quit(): Unit =
    executeCommand(commandFactory.createQuitCommand(game), Events.Quit)

  override def saveGame(): Unit =
    executeCommand(commandFactory.createSaveGameCommand(), Events.SaveGame)

  override def loadGame(fileName: String): Unit = {
    executeCommand(commandFactory.createLoadGameCommand(fileName), Events.PlayingField)
  }

  override def resetGame(): Unit = {
    executeCommand(commandFactory.createResetGameCommand(), Events.ResetGame)
  }

  override def executeCommand(command: ICommand, event: Events): Unit = {
    undoManager.doStep(command)
    notifyObservers(event)
  }
}