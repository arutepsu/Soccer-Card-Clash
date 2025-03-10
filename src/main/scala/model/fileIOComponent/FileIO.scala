package model.fileIOComponent

import model.fileIOComponent.xmlComponent.XmlComponent
import model.fileIOComponent.jSONComponent.JsonComponent
import model.gameComponent.factory.IGameState
import javax.inject.Singleton

import javax.inject.{Inject, Singleton}

@Singleton
class FileIO @Inject() (
                         jsonComponent: JsonComponent,
                         xmlComponent: XmlComponent
                       ) extends IFileIO {

  override def saveGame(gameState: IGameState): Unit = {
    try {
      xmlComponent.save(gameState)  // ✅ Use injected XmlComponent
      jsonComponent.save(gameState) // ✅ Use injected JsonComponent
      println("✅ FileIO: Game successfully saved in both XML and JSON formats.")
    } catch {
      case e: Exception =>
        println(s"❌ FileIO: Error saving game: ${e.getMessage}")
    }
  }

  override def loadGame(fileName: String): IGameState = {
    println(s"📂 FileIO: Attempting to load game: $fileName")

    try {
      val gameStateOpt = if (fileName.endsWith(".json")) {
        println(s"🔍 FileIO: Loading JSON file: $fileName")
        jsonComponent.load(fileName) // ✅ Use injected JsonComponent
      } else if (fileName.endsWith(".xml")) {
        println(s"🔍 FileIO: Loading XML file: $fileName")
        xmlComponent.load(fileName)  // ✅ Use injected XmlComponent
      } else {
        throw new RuntimeException(s"❌ FileIO: Unsupported file format: $fileName")
      }

      if (gameStateOpt.isEmpty) {
        throw new RuntimeException(s"❌ FileIO: Failed to load game: No valid save data found in '$fileName'!")
      }

      println(s"✅ FileIO: Successfully loaded game from $fileName")

      gameStateOpt.get
    } catch {
      case e: Exception =>
        println(s"❌ FileIO: ERROR loading game '$fileName': ${e.getMessage}")
        e.printStackTrace()
        throw new RuntimeException(s"❌ FileIO: Error loading game '$fileName': ${e.getMessage}")
    }
  }
}
