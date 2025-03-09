package model.gameComponent.factory
import model.cardComponent.CardDeserializer
import model.gameComponent.IGame
import model.playerComponent.factory.PlayerDeserializer
import model.playingFiledComponent.PlayingFieldDeserializer
//import model.playerComponent.factory.PlayerDeserializer
import util.{Deserializer, Serializable}
import model.playingFiledComponent.dataStructure.{HandCardsQueueDeserializer, IHandCardsQueue}
import play.api.libs.json.*
import model.playerComponent.IPlayer
import scala.xml.*
import model.playingFiledComponent.dataStructure.IHandCardsQueue
import model.cardComponent.ICard
import model.playingFiledComponent.IPlayingField
import play.api.libs.json._
import scala.xml._

object GameDeserializer extends Deserializer[IGameState] {
  private given gameStateFactory: IGameStateFactory = summon[IGameStateFactory]

  override def fromXml(xml: Elem): IGameState = {
    val playingField = PlayingFieldDeserializer.fromXml((xml \ "PlayingField").head.asInstanceOf[Elem])
    val player1 = PlayerDeserializer.fromXml((xml \ "Player1").head.asInstanceOf[Elem])
    val player2 = PlayerDeserializer.fromXml((xml \ "Player2").head.asInstanceOf[Elem])

    val player1Hand = HandCardsQueueDeserializer.fromXml((xml \ "Player1Hand").head.asInstanceOf[Elem])
    val player2Hand = HandCardsQueueDeserializer.fromXml((xml \ "Player2Hand").head.asInstanceOf[Elem])

    val player1Field = (xml \ "Player1Field" \ "Card").map(node => CardDeserializer.fromXml(node.asInstanceOf[Elem])).toList
    val player2Field = (xml \ "Player2Field" \ "Card").map(node => CardDeserializer.fromXml(node.asInstanceOf[Elem])).toList

    val player1Goalkeeper = (xml \ "Player1Goalkeeper").headOption.map(node => CardDeserializer.fromXml(node.asInstanceOf[Elem]))
    val player2Goalkeeper = (xml \ "Player2Goalkeeper").headOption.map(node => CardDeserializer.fromXml(node.asInstanceOf[Elem]))

    val player1Score = (xml \ "@player1Score").text.toInt
    val player2Score = (xml \ "@player2Score").text.toInt

    gameStateFactory.create(
      playingField,
      player1,
      player2,
      player1Hand,
      player2Hand,
      player1Field,
      player2Field,
      player1Goalkeeper,
      player2Goalkeeper,
      player1Score,
      player2Score
    )
  }

  override def fromJson(json: JsObject): IGameState = {
    val playingField = PlayingFieldDeserializer.fromJson((json \ "playingField").as[JsObject])
    val player1 = PlayerDeserializer.fromJson((json \ "player1").as[JsObject])
    val player2 = PlayerDeserializer.fromJson((json \ "player2").as[JsObject])

    val player1Hand = HandCardsQueueDeserializer.fromJson((json \ "player1Hand").as[JsObject])
    val player2Hand = HandCardsQueueDeserializer.fromJson((json \ "player2Hand").as[JsObject])

    val player1Field = (json \ "player1Field").as[List[JsObject]].map(CardDeserializer.fromJson)
    val player2Field = (json \ "player2Field").as[List[JsObject]].map(CardDeserializer.fromJson)

    val player1Goalkeeper = (json \ "player1Goalkeeper").asOpt[JsObject].map(CardDeserializer.fromJson)
    val player2Goalkeeper = (json \ "player2Goalkeeper").asOpt[JsObject].map(CardDeserializer.fromJson)

    val player1Score = (json \ "player1Score").as[Int]
    val player2Score = (json \ "player2Score").as[Int]

    gameStateFactory.create(
      playingField,
      player1,
      player2,
      player1Hand,
      player2Hand,
      player1Field,
      player2Field,
      player1Goalkeeper,
      player2Goalkeeper,
      player1Score,
      player2Score
    )
  }
}

