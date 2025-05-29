package de.htwg.se.soccercardclash.model.fileIOComponent.jSONComponent

import de.htwg.se.soccercardclash.controller.IController
import de.htwg.se.soccercardclash.model.gameComponent.service.*
import de.htwg.se.soccercardclash.model.gameComponent.state.IGameState
import de.htwg.se.soccercardclash.model.gameComponent.state.base.GameState
import de.htwg.se.soccercardclash.model.playerComponent.playerAction.PlayerActionPolicies
import play.api.libs.json.*

import java.io.{File, PrintWriter}
import javax.inject.{Inject, Singleton}
import scala.io.Source
import scala.util.{Failure, Success, Try}

@Singleton
class JsonComponent @Inject()(gameDeserializer: GameDeserializer):

  private val folderPath = "games/"
  private val defaultFile = s"${folderPath}game.json"

  def load(fileName: String): Option[IGameState] =
    ensureFolderExists()
    val filePath = s"$folderPath$fileName"
    readJsonFromFile(filePath).flatMap(parseGameState)

  private def readJsonFromFile(path: String): Option[JsObject] =
    Try(Source.fromFile(path).getLines().mkString)
      .map(Json.parse(_).as[JsObject])
      .toOption

  private def parseGameState(json: JsObject): Option[IGameState] =
    Try(gameDeserializer.fromJson(json)).toOption

  def save(gameState: IGameState): Unit =
    ensureFolderExists()
    writeJsonToFile(defaultFile, gameState.toJson)

  private def ensureFolderExists(): Unit =
    Option(File(folderPath)).filterNot(_.exists()).foreach(_.mkdir())

  private def writeJsonToFile(path: String, json: JsValue): Unit =
    Try(PrintWriter(File(path)))
      .foreach(pw =>
        pw.write(Json.prettyPrint(json))
        pw.close()
      )