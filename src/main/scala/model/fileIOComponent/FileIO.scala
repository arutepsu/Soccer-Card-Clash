package model.fileIOComponent

import model.fileIOComponent.xmlComponent.XmlComponent
import model.fileIOComponent.jSONComponent.JsonComponent
import model.gameComponent.factory.IGameState
import javax.inject.Singleton

@Singleton
class FileIO extends IFileIO {

  override def saveGame(gameState: IGameState): Unit = {
    try {
      XmlComponent.save(gameState)
      JsonComponent.save(gameState)
      println("✅ Game successfully saved in both XML and JSON formats.")
    } catch {
      case e: Exception =>
        println(s"❌ Error saving game: ${e.getMessage}")
    }
  }

  override def loadGame: IGameState = {
    try {
      XmlComponent.load().orElse(JsonComponent.load()).getOrElse {
        throw new RuntimeException("❌ Failed to load game: No valid save data found!")
      }
    } catch {
      case e: Exception =>
        throw new RuntimeException(s"❌ Error loading game: ${e.getMessage}")
    }
  }
}