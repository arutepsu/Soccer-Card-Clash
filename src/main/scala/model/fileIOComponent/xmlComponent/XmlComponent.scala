package model.fileIOComponent.xmlComponent

import model.gameComponent.factory.GameDeserializer
import java.io.{File, PrintWriter}
import scala.xml.{Elem, PrettyPrinter, XML}
import model.gameComponent.factory.*

import java.io.{File, PrintWriter}
import javax.inject.{Inject, Singleton}
import scala.xml.{Elem, PrettyPrinter, XML}

@Singleton
class XmlComponent @Inject() (gameDeserializer: GameDeserializer) {

  private val folderPath = "games/" // ✅ Define the folder for game saves
  private val filePath = folderPath + "game.xml" // ✅ Save inside the folder

  // ✅ Ensure the folder exists before saving/loading
  private def ensureFolderExists(): Unit = {
    val folder = new File(folderPath)
    if (!folder.exists()) {
      folder.mkdir() // Create folder if it does not exist
    }
  }

  // ✅ Load Game State from XML
  def load(fileName: String): Option[IGameState] = {
    ensureFolderExists() // ✅ Ensure folder exists before loading
    val filePath = s"games/$fileName"

    try {
      println(s"📂 XmlComponent: Loading game from XML file: $filePath")

      val file = XML.loadFile(filePath)
      if (file == null) {
        println(s"❌ XML file '$fileName' not found.")
        None
      } else {
        println(s"📜 XmlComponent: Successfully loaded XML.")
        val gameState = gameDeserializer.fromXml((file \\ "GameState").head.asInstanceOf[Elem]) // ✅ Use injected gameDeserializer
        println(s"✅ XmlComponent: Successfully deserialized game state.")

        Some(gameState)
      }
    } catch {
      case e: Exception =>
        println(s"❌ XmlComponent: Error loading XML file '$fileName': ${e.getMessage}")
        None
    }
  }

  // ✅ Save Game State as XML
  def save(gameState: IGameState): Unit = {
    ensureFolderExists() // ✅ Ensure folder exists before saving
    try {
      val pw = new PrintWriter(new File(filePath))
      val prettyPrinter = new PrettyPrinter(120, 4)
      val xml = prettyPrinter.format(gameState.toXml)
      pw.write(xml)
      pw.close()
      println(s"✅ XmlComponent: Game successfully saved to $filePath")
    } catch {
      case e: Exception => println(s"❌ XmlComponent: Error saving XML: ${e.getMessage}")
    }
  }
}
