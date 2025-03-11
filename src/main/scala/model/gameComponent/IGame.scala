package model.gameComponent

import controller.IController
import controller.command.memento.base.Memento
import model.playerComponent.IPlayer
import model.playingFiledComponent.IPlayingField
import model.playingFiledComponent.manager.base.ActionManager
import model.playingFiledComponent.manager.IActionManager
import play.api.libs.json.*

import scala.xml.*
import scala.util.Try

trait IGame extends Serializable {
  def startGame(player1: String, player2: String): Unit

  def getPlayingField: IPlayingField

  def getPlayer1: IPlayer

  def getPlayer2: IPlayer

  def getActionManager: IActionManager
  def selectDefenderPosition(): Int

  def saveGame(): Unit
  def loadGame(fileName: String): Unit
  def exit(): Unit
  def updateGameState(): Unit
}
