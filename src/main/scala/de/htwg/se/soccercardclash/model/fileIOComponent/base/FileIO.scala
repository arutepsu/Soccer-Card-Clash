package de.htwg.se.soccercardclash.model.fileIOComponent.base

import de.htwg.se.soccercardclash.model.fileIOComponent.IFileIO
import de.htwg.se.soccercardclash.model.fileIOComponent.jSONComponent.JsonComponent
import de.htwg.se.soccercardclash.model.fileIOComponent.xmlComponent.XmlComponent
import de.htwg.se.soccercardclash.model.gameComponent.state.IGameState

import javax.inject.{Inject, Singleton}
import scala.util.{Failure, Success, Try}

@Singleton
class FileIO @Inject() (
                         jsonComponent: JsonComponent,
                         xmlComponent: XmlComponent
                       ) extends IFileIO {

  override def saveGame(gameState: IGameState): Unit = {
    Try {
      xmlComponent.save(gameState)
      jsonComponent.save(gameState)
    } match {
      case Success(_) =>
      case Failure(_) => throw new RuntimeException(s"❌ FileIO: Error while saving")
    }
  }
  
  override def loadGame(fileName: String): IGameState = {
    Try {
      val gameStateOpt = if (fileName.endsWith(".json")) {
        jsonComponent.load(fileName)
      } else if (fileName.endsWith(".xml")) {
        xmlComponent.load(fileName)
      } else {
        throw new RuntimeException(s"❌ FileIO: Unsupported file format: $fileName")
      }

      gameStateOpt.getOrElse(
        throw new RuntimeException(s"❌ FileIO: Failed to load game: No valid save data found in '$fileName'!")
      )
    } match {
      case Success(gameState) => gameState
      case Failure(exception) =>
        throw new RuntimeException(s"❌ FileIO: Error loading game '$fileName': ${exception.getMessage}", exception)
    }
  }
}
