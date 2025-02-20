package controller.base
import controller.command.ICommand
import controller.command.commandTypes.attackCommands.{DoubleAttackCommand, SingleAttackCommand}
import controller.command.commandTypes.boostCommands.{BoostDefenderCommand, BoostGoalkeeperCommand}
import controller.command.commandTypes.swapCommands.{CircularSwapCommand, HandSwapCommand}
import controller.{Events, IController}
import model.cardComponent.cardFactory.DeckFactory
import model.gameComponent.IGame
import model.gameComponent.base.Game
import model.playerComponent.IPlayer
import model.playingFiledComponent.IPlayingField
import model.playingFiledComponent.manager.ActionManager
import util.{Observer, UndoManager}

import java.io.{FileInputStream, FileOutputStream, ObjectInputStream, ObjectOutputStream}
import scala.collection.mutable
import scala.util.Try

class Controller extends IController {
  private var game: IGame = new Game()
  private val undoManager = new UndoManager
  
  def startGame(player1: String, player2: String): Unit = {
    game.startGame(player1, player2)
    notifyObservers(Events.StartGame)
  }

  def getPlayingField: IPlayingField = game.getPlayingField
  def getPlayer1: IPlayer = game.getPlayer1
  def getPlayer2: IPlayer = game.getPlayer2
  
  private def executeCommand(command: ICommand, event: Events): Unit = {
    undoManager.doStep(command)
    notifyObservers(event)
  }

  def executeSingleAttackCommand(defenderPosition: Int): Unit = {
    executeCommand(new SingleAttackCommand(defenderPosition, game.getGameManager), Events.RegularAttack)
  }

  def executeDoubleAttackCommand(defenderPosition: Int): Unit = {
    executeCommand(new DoubleAttackCommand(defenderPosition, game.getGameManager), Events.DoubleAttack)
  }

  def boostDefender(defenderPosition: Int): Unit = {
    executeCommand(new BoostDefenderCommand(defenderPosition, game.getGameManager), Events.BoostDefender)
  }

  def boostGoalkeeper(): Unit = {
    executeCommand(new BoostGoalkeeperCommand(game.getGameManager), Events.BoostGoalkeeper)
  }

  def regularSwap(index: Int): Unit = {
    executeCommand(new HandSwapCommand(index, game.getGameManager), Events.RegularSwap)
  }

  def circularSwap(index: Int): Unit = {
    executeCommand(new CircularSwapCommand(index, game.getGameManager), Events.CircularSwap)
  }
  
  def selectDefenderPosition(): Int = game.selectDefenderPosition()

  def undo(): Unit = {
    undoManager.undoStep()
    notifyObservers(Events.Undo)
  }

  def redo(): Unit = {
    undoManager.redoStep()
    notifyObservers(Events.Redo)
  }

  def saveGame(): Unit = {
    notifyObservers(Events.SaveGame)
  }

  def loadGame(): Unit = {
    notifyObservers(Events.LoadGame)
  }
}

