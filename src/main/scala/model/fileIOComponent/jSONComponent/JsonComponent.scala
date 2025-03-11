package model.fileIOComponent.jSONComponent

import java.io.{File, PrintWriter}
import scala.io.Source
import play.api.libs.json._
import model.gameComponent.factory.GameDeserializer
import model.gameComponent.factory.*


import java.io.{File, PrintWriter}
import javax.inject.{Inject, Singleton}
import scala.io.Source
import play.api.libs.json._

@Singleton
class JsonComponent @Inject() (gameDeserializer: GameDeserializer) {

  private val folderPath = "games/" // ✅ Define the folder for game saves
  private val filePath = folderPath + "game.json" // ✅ Save inside the folder

  // ✅ Ensure the folder exists before saving/loading
  private def ensureFolderExists(): Unit = {
    val folder = new File(folderPath)
    if (!folder.exists()) {
      folder.mkdir() // Create folder if it does not exist
    }
  }

  // ✅ Load Game State from JSON
  def load(fileName: String): Option[IGameState] = {
    ensureFolderExists() // ✅ Ensure folder exists before loading
    val filePath = s"games/$fileName"

    try {
      println(s"📂 JsonComponent: Loading game from JSON file: $filePath")

      val source = Source.fromFile(filePath).getLines.mkString
      println(s"📜 JsonComponent: Loaded JSON content: ${source.take(200)}...") // Prevent excessive logging

      val json = Json.parse(source).as[JsObject]
      println(s"🔍 JsonComponent: Successfully parsed JSON.")

      val gameState = gameDeserializer.fromJson(json) // ✅ Use injected gameDeserializer
      println(s"✅ JsonComponent: Successfully deserialized game state.")

      Some(gameState)
    } catch {
      case _: java.io.FileNotFoundException =>
        println(s"❌ JsonComponent: JSON file '$fileName' not found.")
        None
      case e: Exception =>
        println(s"❌ JsonComponent: ERROR loading JSON file '$fileName': ${e.getMessage}")
        e.printStackTrace()
        None
    }
  }

  // ✅ Save Game State as JSON
  // ✅ Save the latest game state, not an old memento
  def save(gameState: IGameState): Unit = {
    ensureFolderExists() // ✅ Ensure folder exists before saving

    try {
      val json = gameState.toJson // 🔍 Check if this actually reflects the current state
      val pw = new PrintWriter(new File(filePath))
      pw.write(Json.prettyPrint(json))
      pw.close()

      println(s"✅ Game successfully saved to $filePath")
    } catch {
      case e: Exception => println(s"❌ Error saving JSON: ${e.getMessage}")
    }
  }

}
