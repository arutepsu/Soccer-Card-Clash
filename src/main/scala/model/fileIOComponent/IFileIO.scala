package model.fileIOComponent
import model.gameComponent.factory.IGameState
trait IFileIO {
  def loadGame(fileName: String): IGameState  // ✅ Change return type to IGameState
  def saveGame(gameState: IGameState): Unit  // ✅ Expect IGameState instead of IGame
}
