package controller.base
import controller.command.ICommand
import controller.{Events, IController}
import model.cardComponent.cardFactory.DeckFactory
import model.gameComponent.IGame
import model.gameComponent.base.Game
import model.playerComponent.IPlayer
import model.playingFiledComponent.IPlayingField
import util.{Observer, UndoManager}

import java.io.{FileInputStream, FileOutputStream, ObjectInputStream, ObjectOutputStream}
import scala.collection.mutable
import scala.util.Try
import com.google.inject.Inject
import controller.command.factories.ICommandFactory
import model.playingFiledComponent.manager.base.ActionManager

class Controller @Inject() (private val game: IGame, private val commandFactory: ICommandFactory) extends IController {
  private val undoManager = new UndoManager

  def startGame(player1: String, player2: String): Unit = {
    game.startGame(player1, player2)
    notifyObservers(Events.StartGame)
  }

  private def executeCommand(command: ICommand, event: Events): Unit = {
    undoManager.doStep(command)
    notifyObservers(event)
  }
  def getPlayingField: IPlayingField = game.getPlayingField
  def getPlayer1: IPlayer = game.getPlayer1
  def getPlayer2: IPlayer = game.getPlayer2

  def executeSingleAttackCommand(defenderPosition: Int): Unit = {
    executeCommand(commandFactory.createSingleAttackCommand(defenderPosition), Events.RegularAttack)
  }

  def executeDoubleAttackCommand(defenderPosition: Int): Unit = {
    executeCommand(commandFactory.createDoubleAttackCommand(defenderPosition), Events.DoubleAttack)
  }

  def boostDefender(defenderPosition: Int): Unit = {
    executeCommand(commandFactory.createBoostDefenderCommand(defenderPosition), Events.BoostDefender)
  }

  def boostGoalkeeper(): Unit = {
    executeCommand(commandFactory.createBoostGoalkeeperCommand(), Events.BoostGoalkeeper)
  }

  def regularSwap(index: Int): Unit = {
    executeCommand(commandFactory.createRegularSwapCommand(index), Events.RegularSwap)
  }

  def circularSwap(index: Int): Unit = {
    executeCommand(commandFactory.createCircularSwapCommand(index), Events.CircularSwap)
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