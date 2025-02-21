package model.gameComponent

import model.playerComponent.IPlayer
import model.playingFiledComponent.IPlayingField
import model.playingFiledComponent.manager.base.ActionManager
import play.api.libs.json._
import scala.xml._
import scala.util.Try

trait IGame extends Serializable {
  def startGame(player1: String, player2: String): Unit

  def getPlayingField: IPlayingField

  def getPlayer1: IPlayer

  def getPlayer2: IPlayer

  def getActionManager: ActionManager // ✅ Changed from ActionManager to IActionManager

  def selectDefenderPosition(): Int

  def saveGame(): Unit

  def loadGame(): Unit
}
