package model.gameComponent

import model.playerComponent.IPlayer
import model.playingFiledComponent.IPlayingField
import model.playingFiledComponent.manager.ActionManager

import scala.util.Try

trait IGame {
  def startGame(player1: String, player2: String): Unit

  def getPlayingField: IPlayingField

  def getPlayer1: IPlayer

  def getPlayer2: IPlayer

  def getGameManager: ActionManager

  def selectDefenderPosition(): Int

  def saveGame(): Unit

  def loadGame(): Unit
}
