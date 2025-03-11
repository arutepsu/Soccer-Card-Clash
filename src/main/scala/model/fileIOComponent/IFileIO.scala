package model.fileIOComponent
import model.gameComponent.factory.IGameState
trait IFileIO {
  def loadGame(fileName: String): IGameState
  def saveGame(gameState: IGameState): Unit
}
