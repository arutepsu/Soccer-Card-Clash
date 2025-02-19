package model.gameComponent

import model.playerComponent.base.Player
import model.playingFiledComponent.PlayingField

import scala.util.Try
trait IGame {
  def startGame(player1: String, player2: String): Unit
  def getPlayingField: PlayingField
  def getPlayer1: Player
  def getPlayer2: Player
  def getGameManager: GameManager
  def selectDefenderPosition(): Int
  def saveGame(): Unit
  def loadGame(): Unit
}
