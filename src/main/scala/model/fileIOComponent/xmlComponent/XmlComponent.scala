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

  private val folderPath = "games/"
  private val filePath = folderPath + "game.xml"
  
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

      val source = XML.loadFile(filePath)

      val gameState = gameDeserializer.fromXml(source.asInstanceOf[Elem])

      Some(gameState)
    } catch {
      case _: java.io.FileNotFoundException =>
        None
      case e: Exception =>
        e.printStackTrace()
        None
    }
  }
  
  def save(gameState: IGameState): Unit = {
    ensureFolderExists()

    try {
      val xml = gameState.toXml
      val prettyPrinter = new PrettyPrinter(120, 4)
      val formattedXml = prettyPrinter.format(xml)

      val pw = new PrintWriter(new File(filePath))
      pw.write(formattedXml)
      pw.close()
      
    } catch {
      case e: Exception => println(s"❌ Error saving XML: ${e.getMessage}")
    }
  }
}
