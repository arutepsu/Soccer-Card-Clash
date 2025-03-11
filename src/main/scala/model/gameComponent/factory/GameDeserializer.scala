package model.gameComponent.factory
import model.cardComponent.CardDeserializer
import model.gameComponent.IGame
import model.playerComponent.factory.PlayerDeserializer
import model.playingFiledComponent.PlayingFieldDeserializer
import model.playingFiledComponent.dataStructure.HandCardsQueueFactory
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
import javax.inject.{Singleton, Inject}
@Singleton
class GameDeserializer @Inject() (
                                   gameStateFactory: IGameStateFactory,
                                   playingFieldDeserializer: PlayingFieldDeserializer,
                                   playerDeserializer: PlayerDeserializer,
                                   handCardsQueueDeserializer: HandCardsQueueDeserializer,
                                   cardDeserializer: CardDeserializer
                                 ) extends Deserializer[IGameState] {

  override def fromXml(xml: Elem): IGameState = {
    println("DEBUG: Entering GameDeserializer.fromXml")

    val playingField = (xml \ "PlayingField").headOption.map(_.asInstanceOf[Elem])
      .map(playingFieldDeserializer.fromXml)
      .getOrElse(throw new IllegalArgumentException("ERROR: Missing 'PlayingField' element in XML."))

    val player1 = (xml \ "Player1").headOption.map(_.asInstanceOf[Elem])
      .map(playerDeserializer.fromXml)
      .getOrElse(throw new IllegalArgumentException("ERROR: Missing 'Player1' element in XML."))

    val player2 = (xml \ "Player2").headOption.map(_.asInstanceOf[Elem])
      .map(playerDeserializer.fromXml)
      .getOrElse(throw new IllegalArgumentException("ERROR: Missing 'Player2' element in XML."))

    println(s"DEBUG: Extracted players - Player1: $player1, Player2: $player2")

    val player1Hand = (xml \ "Player1Hand").headOption.map(_.asInstanceOf[Elem])
    .map(handCardsQueueDeserializer.fromXml)
    .getOrElse(HandCardsQueueFactory.create(Nil))  // ✅ Use Factory

    val player2Hand = (xml \ "Player2Hand").headOption.map(_.asInstanceOf[Elem])
    .map(handCardsQueueDeserializer.fromXml)
    .getOrElse(HandCardsQueueFactory.create(Nil))  // ✅ Use Factory

    val player1Field = (xml \ "Player1Field" \ "Card").map(node => cardDeserializer.fromXml(node.asInstanceOf[Elem])).toList
    val player2Field = (xml \ "Player2Field" \ "Card").map(node => cardDeserializer.fromXml(node.asInstanceOf[Elem])).toList

    println(s"DEBUG: Extracted field cards.")

    val player1Goalkeeper = (xml \ "Player1Goalkeeper").headOption.map(node => cardDeserializer.fromXml(node.asInstanceOf[Elem]))
    val player2Goalkeeper = (xml \ "Player2Goalkeeper").headOption.map(node => cardDeserializer.fromXml(node.asInstanceOf[Elem]))

    println(s"DEBUG: Extracted goalkeepers.")

    val player1Score = (xml \ "@player1Score").text.toIntOption.getOrElse(0)
    val player2Score = (xml \ "@player2Score").text.toIntOption.getOrElse(0)

    println(s"DEBUG: Extracted scores - Player1: $player1Score, Player2: $player2Score")

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
    println("DEBUG: Entering GameDeserializer.fromJson")
    println(s"DEBUG: Full JSON before parsing: ${Json.prettyPrint(json)}")

    val availableKeys = json.keys.mkString(", ")
    println(s"DEBUG: Available JSON keys: $availableKeys")

    val playingFieldJsonOpt = (json \ "playingField").validateOpt[JsObject].getOrElse(None)
    val playingFieldJson = playingFieldJsonOpt.getOrElse(
      throw new IllegalArgumentException("ERROR: Missing or invalid 'playingField' in JSON.")
    )

    val playingField = playingFieldDeserializer.fromJson(playingFieldJson)

    val player1Json = (playingFieldJsonOpt.flatMap(js => (js \ "attacker").asOpt[JsObject])).getOrElse(
      throw new IllegalArgumentException("ERROR: Missing or invalid 'attacker' in JSON.")
    )
    val player1 = playerDeserializer.fromJson(player1Json)

    val player2Json = (playingFieldJsonOpt.flatMap(js => (js \ "defender").asOpt[JsObject])).getOrElse(
      throw new IllegalArgumentException("ERROR: Missing or invalid 'defender' in JSON.")
    )
    val player2 = playerDeserializer.fromJson(player2Json)

    println(s"DEBUG: Extracted players - Player1: $player1, Player2: $player2")

    // ✅ Fix: Deserialize hand cards from JSON instead of XML
    val player1Hand = (json \ "player1Hand").validateOpt[JsArray].map {
      case Some(jsArray) => handCardsQueueDeserializer.fromJson(Json.obj("cards" -> jsArray))
      case None => HandCardsQueueFactory.create(Nil)
    }.get

    val player2Hand = (json \ "player2Hand").validateOpt[JsArray].map {
      case Some(jsArray) => handCardsQueueDeserializer.fromJson(Json.obj("cards" -> jsArray))
      case None => HandCardsQueueFactory.create(Nil)
    }.get

    println("DEBUG: Extracted hand cards.")

    val player1Field = (json \ "player1Field").validate[List[JsObject]].getOrElse(Nil).map(cardDeserializer.fromJson)
    val player2Field = (json \ "player2Field").validate[List[JsObject]].getOrElse(Nil).map(cardDeserializer.fromJson)

    println("DEBUG: Extracted field cards.")

    val player1Goalkeeper = (json \ "player1Goalkeeper").validateOpt[JsObject] match {
      case JsSuccess(Some(jsObj), _) => Some(cardDeserializer.fromJson(jsObj))
      case _ => None
    }

    val player2Goalkeeper = (json \ "player2Goalkeeper").validateOpt[JsObject] match {
      case JsSuccess(Some(jsObj), _) => Some(cardDeserializer.fromJson(jsObj))
      case _ => None
    }


    println("DEBUG: Extracted goalkeepers.")

    val scoresJsonOpt = (json \ "playingField" \ "scores").validateOpt[JsObject].getOrElse(None)

    val player1Score = scoresJsonOpt.flatMap(js => (js \ "scorePlayer1").asOpt[Int]).getOrElse(0)
    val player2Score = scoresJsonOpt.flatMap(js => (js \ "scorePlayer2").asOpt[Int]).getOrElse(0)


    println(s"DEBUG: Extracted scores - Player1: $player1Score, Player2: $player2Score")

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