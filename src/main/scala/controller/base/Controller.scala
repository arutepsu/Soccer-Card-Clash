package controller.base
import controller.command.commandTypes.attackCommands.{DoubleAttackCommand, SingleAttackCommand}
import controller.command.commandTypes.boostCommands.{BoostDefenderCommand, BoostGoalkeeperCommand}
import controller.command.commandTypes.swapCommands.HandSwapCommand
import controller.{Events, IController}
import model.cardComponent.cardFactory.DeckFactory
import model.gameComponent.{Game, GameManager, IGame}
import model.playerComponent.base.Player
import model.playingFiledComponent.PlayingField
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


  def getPlayingField: PlayingField = game.getPlayingField
  def getPlayer1: Player = game.getPlayer1
  def getPlayer2: Player = game.getPlayer2

  def executeSingleAttackCommand(defenderPosition: Int): Unit = {
    val command = new SingleAttackCommand(defenderPosition, game.getGameManager)
    undoManager.doStep(command)
    notifyObservers(Events.RegularAttack)
  }

  def executeDoubleAttackCommand(defenderPosition: Int): Unit = {
    val command = new DoubleAttackCommand(defenderPosition, game.getGameManager)
    undoManager.doStep(command)
    notifyObservers(Events.DoubleAttack)
  }

  def boostDefender(defenderPosition: Int): Unit = {
    val command = new BoostDefenderCommand(defenderPosition, game.getGameManager)
    undoManager.doStep(command)
    notifyObservers(Events.BoostDefender)
  }

  def boostGoalkeeper(): Unit = {
    val command = new BoostGoalkeeperCommand(game.getGameManager)
    undoManager.doStep(command)
    notifyObservers(Events.BoostGoalkeeper)
  }

  def regularSwap(index: Int): Unit = {
    val command = new HandSwapCommand(index, game.getGameManager)
    undoManager.doStep(command)
    notifyObservers(Events.RegularSwap)
  }

  def circularSwap(index: Int): Unit = {
    val command = new HandSwapCommand(index, game.getGameManager)
    undoManager.doStep(command)
    notifyObservers(Events.CircularSwap)
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
//    game.saveGame()
    notifyObservers(Events.SaveGame)
  }

  def loadGame(): Unit = {
    //    game.loadGame().map { loadedGame =>
    //      game = loadedGame
    //      notifyObservers(ControllerEvents.LoadGame)
    //    }
  }


}

