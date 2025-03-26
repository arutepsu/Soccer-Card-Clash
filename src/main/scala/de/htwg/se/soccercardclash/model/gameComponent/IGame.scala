package de.htwg.se.soccercardclash.model.gameComponent

import de.htwg.se.soccercardclash.controller.IController
import de.htwg.se.soccercardclash.model.gameComponent.state.memento.base.Memento
import de.htwg.se.soccercardclash.model.gameComponent.state.IGameState
import de.htwg.se.soccercardclash.model.playerComponent.IPlayer
import de.htwg.se.soccercardclash.model.playingFiledComponent.IPlayingField
import de.htwg.se.soccercardclash.model.playingFiledComponent.manager.{ActionManager, IActionManager}

import play.api.libs.json.*

import scala.util.Try
import scala.xml.*

trait IGame extends Serializable {
  def createGame(player1: String, player2: String): Unit
  def getPlayingField: IPlayingField
  def getPlayer1: IPlayer
  def getPlayer2: IPlayer
  def getActionManager: IActionManager
  def saveGame(): Unit
  def loadGame(fileName: String): Unit
  def exit(): Unit
  def reset(): Boolean
  def updateGameState(): Unit
}
