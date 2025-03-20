package model.fileIOComponent

import model.gameComponent.state.IGameState
trait IFileIO {
  def loadGame(fileName: String): IGameState
  def saveGame(gameState: IGameState): Unit
}
