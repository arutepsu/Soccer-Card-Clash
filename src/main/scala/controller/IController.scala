package controller
import controller.command.ICommand
import model.cardComponent.factory.DeckFactory
import model.playerComponent.IPlayer
import model.playingFiledComponent.IPlayingField
import util.{Observable, ObservableEvent, Observer, UndoManager}

import scala.collection.mutable
trait IController extends Observable {
  def getPlayingField: IPlayingField
  def getPlayer1: IPlayer
  def getPlayer2: IPlayer
  def startGame(player1: String, player2: String): Unit
  def executeSingleAttackCommand(defenderPosition: Int): Unit
  def executeDoubleAttackCommand(defenderPosition: Int): Unit
  def boostDefender(defenderPosition: Int): Unit
  def boostGoalkeeper(): Unit
  def regularSwap(index: Int): Unit
  def circularSwap(index: Int): Unit
  def undo(): Unit
  def redo(): Unit
  def saveGame(): Unit
  def loadGame(): Unit
  def quit(): Unit
}