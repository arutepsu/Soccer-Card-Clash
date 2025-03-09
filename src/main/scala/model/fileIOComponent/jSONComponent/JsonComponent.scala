package model.fileIOComponent.jSONComponent

import java.io.{File, PrintWriter}
import scala.io.Source
import play.api.libs.json._
import model.gameComponent.factory.GameDeserializer
import model.gameComponent.factory.*


object JsonComponent {

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
  def load(): Option[IGameState] = {
    ensureFolderExists() // ✅ Ensure folder exists before loading
    try {
      val source = Source.fromFile(filePath).getLines.mkString
      val json = Json.parse(source)

      println(s"✅ Game loaded from $filePath")
      Some(GameDeserializer.fromJson(json.as[JsObject]))
    } catch {
      case _: java.io.FileNotFoundException =>
        println("❌ JSON file not found.")
        None
      case e: Exception =>
        println(s"❌ Error loading JSON: ${e.getMessage}")
        None
    }
  }

  // ✅ Save Game State as JSON
  def save(gameState: IGameState): Unit = {
    ensureFolderExists() // ✅ Ensure folder exists before saving
    try {
      val pw = new PrintWriter(new File(filePath))
      pw.write(Json.prettyPrint(gameState.toJson))
      pw.close()
      println(s"✅ Game successfully saved to $filePath")
    } catch {
      case e: Exception => println(s"❌ Error saving JSON: ${e.getMessage}")
    }
  }
}
