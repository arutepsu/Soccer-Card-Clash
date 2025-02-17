package controller.base
import controller.IController
import controller.command.commandTypes.attackCommands.{DoubleAttackCommand, SingleAttackCommand}
import controller.command.commandTypes.boostCommands.{BoostDefenderCommand, BoostGoalkeeperCommand}
import controller.command.commandTypes.swapCommands.HandSwapCommand
import controller.gameBase.GameManager
import model.cardComponent.cardFactory.DeckFactory
import model.playerComponent.Player
import model.playingFiledComponent.PlayingField
import util.UndoManager
import view.GameLogger
import java.io.{FileInputStream, FileOutputStream, ObjectInputStream, ObjectOutputStream}
import scala.collection.mutable
import scala.util.Try

import model.gameComponent.{Game, IGame}
import util.{Observer, UndoManager}

class Controller extends IController {
  private var game: IGame = new Game()
  private val undoManager = new UndoManager  // 🔥 Move UndoManager to Controller

  def startGame(): Unit = {
    game.startGame()
    notifyObservers()
  }

  def getPlayingField: PlayingField = game.getPlayingField
  def getPlayer1: Player = game.getPlayer1
  def getPlayer2: Player = game.getPlayer2

  def setPlayerName(playerIndex: Int, name: String): Unit = {
    game.setPlayerName(playerIndex, name)
    notifyObservers()
  }

  def executeAttackCommand(defenderPosition: Int): Unit = {
    val command = new SingleAttackCommand(defenderPosition, game.getGameController)
    undoManager.doStep(command)
    notifyObservers()
  }

  def executeAttackCommandDouble(defenderPosition: Int): Unit = {
    val command = new DoubleAttackCommand(defenderPosition, game.getGameController)
    undoManager.doStep(command)
    notifyObservers()
  }

  def boostDefender(defenderPosition: Int): Unit = {
    val command = new BoostDefenderCommand(defenderPosition, game.getGameController)
    undoManager.doStep(command)
    notifyObservers()
  }

  def boostGoalkeeper(): Unit = {
    val command = new BoostGoalkeeperCommand(game.getGameController)
    undoManager.doStep(command)
    notifyObservers()
  }

  def swapAttackerCard(index: Int): Unit = {
    val command = new HandSwapCommand(index, game.getGameController)
    undoManager.doStep(command)
    notifyObservers()
  }

  def selectDefenderPosition(): Int = game.selectDefenderPosition()

  def undo(): Unit = {
    undoManager.undoStep()
    notifyObservers()
  }

  def redo(): Unit = {
    undoManager.redoStep()
    notifyObservers()
  }

  def saveGame(filePath: String): Unit = {
    game.saveGame(filePath)
  }

  def loadGame(filePath: String): Try[Unit] = {
    game.loadGame(filePath).map { loadedGame =>
      game = loadedGame
      notifyObservers()
    }
  }

}

