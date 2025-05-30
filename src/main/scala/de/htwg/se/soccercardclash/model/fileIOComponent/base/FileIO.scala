package de.htwg.se.soccercardclash.model.fileIOComponent.base

import de.htwg.se.soccercardclash.model.fileIOComponent.IFileIO
import de.htwg.se.soccercardclash.model.fileIOComponent.jSONComponent.JsonComponent
import de.htwg.se.soccercardclash.model.fileIOComponent.xmlComponent.XmlComponent
import de.htwg.se.soccercardclash.model.gameComponent.IGameState
import de.htwg.se.soccercardclash.model.gameComponent.base.GameState

import javax.inject.{Inject, Singleton}
import scala.util.{Failure, Success, Try}

@Singleton
class FileIO @Inject()(
                        jsonComponent: JsonComponent,
                        xmlComponent: XmlComponent
                      ) extends IFileIO {

  override def saveGame(gameState: IGameState): Try[Unit] = Try {
    xmlComponent.save(gameState)
    jsonComponent.save(gameState)
  }

  override def loadGame(fileName: String): Try[IGameState] = Try {
    val gameStateOpt = if (fileName.endsWith(".json")) {
      jsonComponent.load(fileName)
    } else if (fileName.endsWith(".xml")) {
      xmlComponent.load(fileName)
    } else {
      throw new IllegalArgumentException(s"Unsupported file format: $fileName")
    }

    gameStateOpt.getOrElse {
      throw new NoSuchElementException(s"No valid save data found in '$fileName'!")
    }
  }
}
