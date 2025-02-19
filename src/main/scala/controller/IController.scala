package controller
import model.cardComponent.cardFactory.DeckFactory
import model.playerComponent.base.Player
import model.playingFiledComponent.PlayingField
import util.{Observable, ObservableEvent, Observer, UndoManager}
import scala.collection.mutable
import scala.io.StdIn.readLine
import scala.util.{Failure, Success, Try}

trait IController extends Observable {
  def getPlayingField: PlayingField
  def getPlayer1: Player
  def getPlayer2: Player
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
  def selectDefenderPosition(): Int
  
}