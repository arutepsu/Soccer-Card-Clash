package model.fileIOComponent.xmlComponent

import model.gameComponent.factory.*
import model.gameComponent.state.IGameState

import java.io.{File, PrintWriter}
import javax.inject.{Inject, Singleton}
import scala.util.{Failure, Success, Try}
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

    Try {
      val source = XML.loadFile(filePath)
      gameDeserializer.fromXml(source.asInstanceOf[Elem])
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
      val xml = gameState.toXml
      val prettyPrinter = new PrettyPrinter(120, 4)
      val formattedXml = prettyPrinter.format(xml)

      val pw = new PrintWriter(new File(filePath))
      pw.write(formattedXml)
      pw.close()
    } match {
      case Success(_) =>
      case Failure(exception) =>
    }
  }
}
