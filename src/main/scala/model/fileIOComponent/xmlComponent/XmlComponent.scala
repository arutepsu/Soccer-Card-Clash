package model.fileIOComponent.xmlComponent

import model.gameComponent.factory.GameDeserializer
import java.io.{File, PrintWriter}
import scala.xml.{Elem, PrettyPrinter, XML}
import model.gameComponent.factory.*

object XmlComponent {

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
  def load(): Option[IGameState] = {
    ensureFolderExists() // ✅ Ensure folder exists before loading
    try {
      val file = XML.loadFile(filePath)
      if (file == null) {
        println("❌ XML file not found.")
        None
      } else {
        println("✅ Game loaded from XML!")
        Some(GameDeserializer.fromXml((file \\ "GameState").head.asInstanceOf[Elem]))
      }
    } catch {
      case e: Exception =>
        println(s"❌ Error loading XML: ${e.getMessage}")
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
      println(s"✅ Game successfully saved to $filePath")
    } catch {
      case e: Exception => println(s"❌ Error saving XML: ${e.getMessage}")
    }
  }
}
