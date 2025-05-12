package de.htwg.se.soccercardclash.model.fileIOComponent

import de.htwg.se.soccercardclash.model.gameComponent.state.IGameState

import scala.util.Try
trait IFileIO {
  def saveGame(gameState: IGameState): Try[Unit]
  def loadGame(fileName: String): Try[IGameState]
}
