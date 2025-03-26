package de.htwg.se.soccercardclash.model.fileIOComponent.jSONComponent

import de.htwg.se.soccercardclash.controller.IController
import de.htwg.se.soccercardclash.model.gameComponent.factory.*
import de.htwg.se.soccercardclash.model.gameComponent.state.IGameState
import de.htwg.se.soccercardclash.model.playerComponent.playerAction.PlayerActionPolicies
import de.htwg.se.soccercardclash.model.playingFiledComponent.factory.PlayingFieldDeserializer

import play.api.libs.json.*
import java.io.{File, PrintWriter}
import javax.inject.{Inject, Singleton}
import scala.io.Source
import scala.util.{Failure, Success, Try}

@Singleton
class JsonComponent @Inject() (gameDeserializer: GameDeserializer) {

  private val folderPath = "games/"
  private val filePath = folderPath + "game.json"

  private def ensureFolderExists(): Unit = {
    val folder = new File(folderPath)
    if (!folder.exists()) {
      folder.mkdir()
    }
  }

  def load(fileName: String): Option[IGameState] = {
    ensureFolderExists()
    val filePath = s"games/$fileName"

    Try {
      val source = Source.fromFile(filePath).getLines.mkString
      val json = Json.parse(source).as[JsObject]
      gameDeserializer.fromJson(json)
    } match {
      case Success(gameState) => Some(gameState)
      case Failure(_: java.io.FileNotFoundException) => None
      case Failure(exception) =>
        exception.printStackTrace()
        None
    }
  }

  def save(gameState: IGameState): Unit = {
    ensureFolderExists()

    Try {
      val json = gameState.toJson
      val pw = new PrintWriter(new File(filePath))
      pw.write(Json.prettyPrint(json))
      pw.close()
    } match {
      case Success(_) =>
      case Failure(exception) =>
    }
  }
}
