package model.fileIOComponent.jSONComponent

import controller.IController
import controller.command.memento.base.Memento

import java.io.{File, PrintWriter}
import scala.io.Source
import play.api.libs.json.*
import model.gameComponent.factory.GameDeserializer
import model.gameComponent.factory.*
import model.playerComponent.playerAction.PlayerActionPolicies
import model.playingFiledComponent.factory.PlayingFieldDeserializer

import java.io.{File, PrintWriter}
import javax.inject.{Inject, Singleton}
import scala.io.Source
import play.api.libs.json.*

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

    try {

      val source = Source.fromFile(filePath).getLines.mkString

      val json = Json.parse(source).as[JsObject]

      val gameState = gameDeserializer.fromJson(json)

      Some(gameState)
    } catch {
      case _: java.io.FileNotFoundException =>
        None
      case e: Exception =>
        println(s"❌ JsonComponent: ERROR loading JSON file '$fileName': ${e.getMessage}")
        e.printStackTrace()
        None
    }
  }

  def save(gameState: IGameState): Unit = {
    ensureFolderExists()

    try {
      val json = gameState.toJson
      val pw = new PrintWriter(new File(filePath))
      pw.write(Json.prettyPrint(json))
      pw.close()

    } catch {
      case e: Exception => println(s"❌ Error saving JSON: ${e.getMessage}")
    }
  }

}
