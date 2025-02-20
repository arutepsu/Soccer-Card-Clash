package model.gameComponent

import model.playerComponent.IPlayer
import model.playingFiledComponent.IPlayingField
import model.playingFiledComponent.manager.ActionManager
import play.api.libs.json._
import scala.xml._
import scala.util.Try

trait IGame extends Serializable{
  def startGame(player1: String, player2: String): Unit

  def getPlayingField: IPlayingField

  def getPlayer1: IPlayer

  def getPlayer2: IPlayer

  def getGameManager: ActionManager

  def selectDefenderPosition(): Int

  def saveGame(): Unit

  def loadGame(): Unit

  override def toJson: JsObject = Json.obj(
    "player1" -> getPlayer1.toJson,
    "player2" -> getPlayer2.toJson,
    "playingField" -> getPlayingField.toJson,
    "actions" -> getGameManager.toJson
  )

  override def toXml: Elem =
    <Game>
      <Player1>
        {getPlayer1.toXml}
      </Player1>
      <Player2>
        {getPlayer2.toXml}
      </Player2>
      <PlayingField>
        {getPlayingField.toXml}
      </PlayingField>
      <Actions>
        {getGameManager.toXml}
      </Actions>
    </Game>
}
