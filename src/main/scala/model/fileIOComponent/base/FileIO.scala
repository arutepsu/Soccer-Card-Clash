package model.fileIOComponent.base

import model.fileIOComponent.IFileIO
import model.fileIOComponent.jSONComponent.JsonComponent
import model.fileIOComponent.xmlComponent.XmlComponent
import model.gameComponent.factory.IGameState

import javax.inject.{Inject, Singleton}

@Singleton
class FileIO @Inject() (
                         jsonComponent: JsonComponent,
                         xmlComponent: XmlComponent
                       ) extends IFileIO {

  override def saveGame(gameState: IGameState): Unit = {
    try {
      xmlComponent.save(gameState)
      jsonComponent.save(gameState)
    } catch {
      case e: Exception =>
    }
  }

  override def loadGame(fileName: String): IGameState = {

    try {
      val gameStateOpt = if (fileName.endsWith(".json")) {
        jsonComponent.load(fileName)
      } else if (fileName.endsWith(".xml")) {
        xmlComponent.load(fileName)
      } else {
        throw new RuntimeException(s"❌ FileIO: Unsupported file format: $fileName")
      }

      if (gameStateOpt.isEmpty) {
        throw new RuntimeException(s"❌ FileIO: Failed to load game: No valid save data found in '$fileName'!")
      }

      gameStateOpt.get
    } catch {
      case e: Exception =>
        e.printStackTrace()
        throw new RuntimeException(s"❌ FileIO: Error loading game '$fileName': ${e.getMessage}")
    }
  }
}
