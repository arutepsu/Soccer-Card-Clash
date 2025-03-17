package controller
import controller.command.ICommand
import model.cardComponent.factory.DeckFactory
import model.gameComponent.IGame
import model.playerComponent.IPlayer
import model.playingFiledComponent.IPlayingField
import util.{Observable, ObservableEvent, Observer, UndoManager}

import scala.collection.mutable
trait IController extends Observable{
  def getCurrentGame: IGame
  def executeCommand(command: ICommand, event: Events): Unit
  def undo(): Unit
  def redo(): Unit
  def executeSingleAttackCommand(defenderPosition: Int): Unit
  def executeDoubleAttackCommand(defenderPosition: Int): Unit
  def boostDefender(defenderPosition: Int): Unit
  def boostGoalkeeper(): Unit
  def regularSwap(index: Int): Unit
  def circularSwap(index: Int): Unit
  def createGame(player1: String, player2: String): Unit
  def quit(): Unit
  def saveGame(): Unit
  def loadGame(fileName: String): Unit
  def resetGame(): Unit
}
