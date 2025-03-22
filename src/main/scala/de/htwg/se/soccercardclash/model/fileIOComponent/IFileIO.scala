package de.htwg.se.soccercardclash.model.fileIOComponent

import de.htwg.se.soccercardclash.model.gameComponent.state.IGameState

trait IFileIO {
  def loadGame(fileName: String): IGameState
  def saveGame(gameState: IGameState): Unit
}
