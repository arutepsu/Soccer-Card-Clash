package model.fileIOComponent.xmlComponent

import model.gameComponent.factory.GameDeserializer
import java.io.{File, PrintWriter}
import scala.xml.{Elem, PrettyPrinter, XML}
import model.gameComponent.factory.*

import java.io.{File, PrintWriter}
import javax.inject.{Inject, Singleton}
import scala.xml.{Elem, PrettyPrinter, XML}

@Singleton
class XmlComponent @Inject()(gameDeserializer: GameDeserializer) {

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

      val source = XML.loadFile(filePath) // ✅ Load XML file
      println(s"📜 XmlComponent: Loaded XML content.")

      val gameState = gameDeserializer.fromXml(source.asInstanceOf[Elem]) // ✅ FIXED: No need to extract "GameState"
      println(s"✅ XmlComponent: Successfully deserialized game state.")

      Some(gameState)
    } catch {
      case _: java.io.FileNotFoundException =>
        println(s"❌ XmlComponent: XML file '$fileName' not found.")
        None
      case e: Exception =>
        println(s"❌ XmlComponent: ERROR loading XML file '$fileName': ${e.getMessage}")
        e.printStackTrace()
        None
    }
  }


  // ✅ Save Game State as XML
  // ✅ Save the latest game state, not an old memento
  def save(gameState: IGameState): Unit = {
    ensureFolderExists() // ✅ Ensure folder exists before saving

    try {
      val xml = gameState.toXml // 🔍 Ensure this correctly reflects the current state
      val prettyPrinter = new PrettyPrinter(120, 4)
      val formattedXml = prettyPrinter.format(xml)

      val pw = new PrintWriter(new File(filePath))
      pw.write(formattedXml)
      pw.close()

      println(s"✅ Game successfully saved to $filePath")
    } catch {
      case e: Exception => println(s"❌ Error saving XML: ${e.getMessage}")
    }
  }
}
