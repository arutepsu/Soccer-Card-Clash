package model.gameComponent

import model.playerComponent.Player
import model.playingFiledComponent.PlayingField

import scala.util.Try
trait IGame {
  def startGame(): Unit
  def getPlayingField: PlayingField
  def getPlayer1: Player
  def getPlayer2: Player
  def getGameManager: GameManager  // 🔥 Allows Controller to execute commands
  def setPlayerName(playerIndex: Int, name: String): Unit
  def selectDefenderPosition(): Int
  def saveGame(filePath: String): Try[Unit]
  def loadGame(filePath: String): Try[IGame]
}
