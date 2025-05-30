package de.htwg.se.soccercardclash.model.fileIOComponent.xmlComponent

import de.htwg.se.soccercardclash.model.gameComponent.IGameState
import de.htwg.se.soccercardclash.model.gameComponent.service.*
import de.htwg.se.soccercardclash.model.gameComponent.base.GameState

import java.io.{File, PrintWriter}
import javax.inject.{Inject, Singleton}
import scala.util.{Failure, Success, Try}
import scala.xml.{Elem, PrettyPrinter, XML}

@Singleton
class XmlComponent @Inject()(gameDeserializer: GameDeserializer):

  private val folderPath = "games/"
  private val defaultFile = s"${folderPath}game.xml"

  def load(fileName: String): Option[IGameState] =
    ensureFolderExists()
    val filePath = s"$folderPath$fileName"
    loadXml(filePath).flatMap(parseGameState)

  private def ensureFolderExists(): Unit =
    Option(File(folderPath)).filterNot(_.exists()).foreach(_.mkdir())

  private def loadXml(path: String): Option[Elem] =
    Try(XML.loadFile(path)).toOption.collect { case e: Elem => e }

  private def parseGameState(xml: Elem): Option[IGameState] =
    Try(gameDeserializer.fromXml(xml)).toOption

  def save(gameState: IGameState): Unit =
    ensureFolderExists()
    val xml = gameState.toXml
    writeXml(defaultFile, xml)

  private def writeXml(path: String, xml: Elem): Unit =
    val pretty = PrettyPrinter(120, 4).format(xml)
    Try(PrintWriter(File(path)))
      .foreach(pw =>
        pw.write(pretty)
        pw.close()
      )