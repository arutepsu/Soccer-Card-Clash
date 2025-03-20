package model.gameComponent

import controller.IController
import controller.command.memento.base.Memento
import model.gameComponent.state.IGameState
import model.playerComponent.IPlayer
import model.playingFiledComponent.IPlayingField
import model.playingFiledComponent.manager.{ActionManager, IActionManager}
import play.api.libs.json.*

import scala.util.Try
import scala.xml.*

trait IGame extends Serializable {
  def createGame(player1: String, player2: String): Unit
  def getPlayingField: IPlayingField
  def getPlayer1: IPlayer
  def getPlayer2: IPlayer
  def getActionManager: IActionManager
  def selectDefenderPosition(): Int
  def saveGame(): Unit
  def loadGame(fileName: String): Unit
  def exit(): Unit
  def reset(): Boolean
  def updateGameState(): Unit
}
