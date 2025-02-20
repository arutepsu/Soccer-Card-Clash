package model.gameComponent

import model.playerComponent.IPlayer
import model.playingFiledComponent.base.{ActionHandler, PlayingField}

import scala.util.Try
trait IGame {
  def startGame(player1: String, player2: String): Unit
  def getPlayingField: PlayingField
  def getPlayer1: IPlayer
  def getPlayer2: IPlayer
  def getGameManager: ActionHandler
  def selectDefenderPosition(): Int
  def saveGame(): Unit
  def loadGame(): Unit
}
