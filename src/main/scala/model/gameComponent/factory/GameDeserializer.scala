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

    val playingField = playingFieldDeserializer.fromXml((xml \ "PlayingField").head.asInstanceOf[Elem])
    println(s"DEBUG: Extracted playingField: $playingField")

    val player1 = playerDeserializer.fromXml((xml \ "Player1").head.asInstanceOf[Elem])
    val player2 = playerDeserializer.fromXml((xml \ "Player2").head.asInstanceOf[Elem])
    println(s"DEBUG: Extracted players - Player1: $player1, Player2: $player2")

    val player1Hand = handCardsQueueDeserializer.fromXml((xml \ "Player1Hand").head.asInstanceOf[Elem])
    val player2Hand = handCardsQueueDeserializer.fromXml((xml \ "Player2Hand").head.asInstanceOf[Elem])
    println(s"DEBUG: Extracted hand cards.")

    val player1Field = (xml \ "Player1Field" \ "Card").map(node => cardDeserializer.fromXml(node.asInstanceOf[Elem])).toList
    val player2Field = (xml \ "Player2Field" \ "Card").map(node => cardDeserializer.fromXml(node.asInstanceOf[Elem])).toList
    println(s"DEBUG: Extracted field cards.")

    val player1Goalkeeper = (xml \ "Player1Goalkeeper").headOption.map(node => cardDeserializer.fromXml(node.asInstanceOf[Elem]))
    val player2Goalkeeper = (xml \ "Player2Goalkeeper").headOption.map(node => cardDeserializer.fromXml(node.asInstanceOf[Elem]))
    println(s"DEBUG: Extracted goalkeepers.")

    val player1Score = (xml \ "@player1Score").text.toInt
    val player2Score = (xml \ "@player2Score").text.toInt
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
    println(s"DEBUG: Entering GameDeserializer.fromJson")
    println(s"DEBUG: Full JSON before parsing: ${Json.prettyPrint(json)}")

    val availableKeys = json.keys.mkString(", ")
    println(s"DEBUG: Available JSON keys: $availableKeys")

    val playingFieldJson = (json \ "playingField").validate[JsObject] match {
      case JsSuccess(jsObj, _) => jsObj
      case JsError(errors) =>
        throw new IllegalArgumentException(s"Invalid JSON format for 'playingField': $errors")
    }

    val playingField = playingFieldDeserializer.fromJson(playingFieldJson)

    val player1Json = (playingFieldJson \ "attacker").validate[JsObject] match {
      case JsSuccess(jsObj, _) => jsObj
      case JsError(errors) =>
        throw new IllegalArgumentException(s"Invalid JSON format for 'attacker': $errors")
    }
    val player1 = playerDeserializer.fromJson(player1Json)

    val player2Json = (playingFieldJson \ "defender").validate[JsObject] match {
      case JsSuccess(jsObj, _) => jsObj
      case JsError(errors) =>
        throw new IllegalArgumentException(s"Invalid JSON format for 'defender': $errors")
    }
    val player2 = playerDeserializer.fromJson(player2Json)


    println(s"DEBUG: Extracted players - Player1: $player1, Player2: $player2")

    // Check if 'player1Hand' exists
    if (!(json \ "player1Hand").isDefined) {
      throw new IllegalArgumentException("ERROR: 'player1Hand' field is missing from JSON!")
    }

    val player1Hand = (json \ "player1Hand").validate[JsArray] match {
      case JsSuccess(jsArray, _) =>
        val queue = handCardsQueueDeserializer.fromJson(Json.obj("cards" -> jsArray))
        println(s"DEBUG: Successfully deserialized player1Hand: $queue")
        queue
      case JsError(errors) =>
        throw new IllegalArgumentException(s"Invalid JSON format for 'player1Hand': $errors")
    }

    val player2Hand = (json \ "player2Hand").validate[JsArray] match {
      case JsSuccess(jsArray, _) =>
        val queue = handCardsQueueDeserializer.fromJson(Json.obj("cards" -> jsArray))
        println(s"DEBUG: Successfully deserialized player2Hand: $queue")
        queue
      case JsError(errors) =>
        throw new IllegalArgumentException(s"Invalid JSON format for 'player2Hand': $errors")
    }

    println(s"DEBUG: Extracted hand cards.")

    val player1Field = (json \ "player1Field").validate[List[JsObject]] match {
      case JsSuccess(jsList, _) => jsList.map(cardDeserializer.fromJson)
      case JsError(errors) => throw new IllegalArgumentException(s"Invalid JSON format for 'player1Field': $errors")
    }

    val player2Field = (json \ "player2Field").validate[List[JsObject]] match {
      case JsSuccess(jsList, _) => jsList.map(cardDeserializer.fromJson)
      case JsError(errors) => throw new IllegalArgumentException(s"Invalid JSON format for 'player2Field': $errors")
    }

    println(s"DEBUG: Extracted field cards.")

    val player1Goalkeeper = (json \ "player1Goalkeeper").asOpt[JsObject].map(cardDeserializer.fromJson)
    val player2Goalkeeper = (json \ "player2Goalkeeper").asOpt[JsObject].map(cardDeserializer.fromJson)

    println(s"DEBUG: Extracted goalkeepers.")

    val scoresJson = (json \ "playingField" \ "scores").validate[JsObject] match {
      case JsSuccess(jsObj, _) => jsObj
      case JsError(errors) =>
        throw new IllegalArgumentException(s"Missing or invalid 'scores' object inside 'playingField': $errors")
    }

    val player1Score = (scoresJson \ "scorePlayer1").validate[Int].getOrElse {
      throw new IllegalArgumentException("Missing or invalid 'scorePlayer1' field in scores")
    }
    val player2Score = (scoresJson \ "scorePlayer2").validate[Int].getOrElse {
      throw new IllegalArgumentException("Missing or invalid 'scorePlayer2' field in scores")
    }


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
