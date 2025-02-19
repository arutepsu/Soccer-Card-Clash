package controller
import model.playingFiledComponent.PlayingField
import util.Observable
import util.Observer
import model.playerComponent.Player
import model.cardComponent.cardFactory.DeckFactory
import scala.collection.mutable
import scala.io.StdIn.readLine
import java.io._
import scala.util.{Try, Success, Failure}
import model.playingFiledComponent.PlayingField
import util.{Observable, UndoManager}
import model.playerComponent.Player
import scala.collection.mutable
import model.playingFiledComponent.PlayingField
import util.{Observable, UndoManager, ObservableEvent}
import model.playerComponent.Player
import model.cardComponent.base.Card
import scala.collection.mutable
import scala.util.Try
import java.io._

trait IController extends Observable {
  def getPlayingField: PlayingField
  def getPlayer1: Player
  def getPlayer2: Player
  def setPlayerName(playerIndex: Int, name: String): Unit
  def startGame(): Unit
  def executeSingleAttackCommand(defenderPosition: Int): Unit
  def executeDoubleAttackCommand(defenderPosition: Int): Unit
  def boostDefender(defenderPosition: Int): Unit
  def boostGoalkeeper(): Unit
  def regularSwap(index: Int): Unit
  def circularSwap(index: Int): Unit
  def undo(): Unit
  def redo(): Unit
  def saveGame(filePath: String): Unit
  def loadGame(filePath: String): Try[Unit]
  def selectDefenderPosition(): Int
  
}