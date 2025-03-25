package de.htwg.se.soccercardclash.controller

import de.htwg.se.soccercardclash.controller.command.ICommand
import de.htwg.se.soccercardclash.model.cardComponent.factory.DeckFactory
import de.htwg.se.soccercardclash.model.gameComponent.IGame
import de.htwg.se.soccercardclash.model.playerComponent.IPlayer
import de.htwg.se.soccercardclash.model.playingFiledComponent.IPlayingField
import de.htwg.se.soccercardclash.util.{Events, Observable, ObservableEvent, Observer, UndoManager}

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
  def reverseSwap(): Unit
  def createGame(player1: String, player2: String): Unit
  def quit(): Unit
  def saveGame(): Unit
  def loadGame(fileName: String): Unit
  def resetGame(): Unit
}
