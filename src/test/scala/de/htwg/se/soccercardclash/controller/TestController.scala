package de.htwg.se.soccercardclash.controller

import de.htwg.se.soccercardclash.util.{Events, ObservableEvent}
import de.htwg.se.soccercardclash.model.gameComponent.IGame
import de.htwg.se.soccercardclash.controller.command.ICommand

class TestController extends IController {
  override def getCurrentGame: IGame = null
  override def executeCommand(command: ICommand, event: Events): Unit = {}
  override def undo(): Unit = {}
  override def redo(): Unit = {}
  override def executeSingleAttackCommand(defenderPosition: Int): Unit = {}
  override def executeDoubleAttackCommand(defenderPosition: Int): Unit = {}
  override def boostDefender(defenderPosition: Int): Unit = {}
  override def boostGoalkeeper(): Unit = {}
  override def regularSwap(index: Int): Unit = {}
  override def reverseSwap(): Unit = {}
  override def createGame(player1: String, player2: String): Unit = {}
  override def quit(): Unit = {}
  override def saveGame(): Unit = {}
  override def loadGame(fileName: String): Unit = {}
  override def resetGame(): Unit = {}

  // You get add/remove/notifyObservers from IController (which extends Observable)
  override def notifyObservers(e: ObservableEvent): Unit = super.notifyObservers(e)
}
