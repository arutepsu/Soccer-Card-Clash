package model.gameComponent.factory
import model.gameComponent.IGame
import model.playerComponent.factory.PlayerDeserializer
import model.playingFiledComponent.dataStructure.HandCardsQueueFactory
import util.{Deserializer, Serializable}
import model.playingFiledComponent.dataStructure.{HandCardsQueueDeserializer, IHandCardsQueue}
import play.api.libs.json.*
import model.playerComponent.IPlayer
import scala.xml.*
import model.playingFiledComponent.dataStructure.IHandCardsQueue
import model.cardComponent.ICard
import model.cardComponent.factory.CardDeserializer
import model.playingFiledComponent.IPlayingField
import model.playingFiledComponent.factory.PlayingFieldDeserializer
import play.api.libs.json._
import scala.xml._
import javax.inject.{Singleton, Inject}
import model.playingFiledComponent.dataStructure.IHandCardsQueueFactory
@Singleton
class GameDeserializer @Inject() (
                                   gameStateFactory: IGameStateFactory,
                                   playingFieldDeserializer: PlayingFieldDeserializer,
                                   playerDeserializer: PlayerDeserializer,
                                   handCardsQueueDeserializer: HandCardsQueueDeserializer,
                                   handCardsQueueFactory: IHandCardsQueueFactory,
                                   cardDeserializer: CardDeserializer
                                 ) extends Deserializer[IGameState] {

  override def fromXml(xml: Elem): IGameState = {

    val playingFieldXml = (xml \\ "playingField").headOption
    .map(_.asInstanceOf[Elem])
    .getOrElse {
      throw new IllegalArgumentException("ERROR: Missing 'playingField' element in XML.")
    }

    val playingField = playingFieldDeserializer.fromXml(playingFieldXml)

    val player1Xml = (playingFieldXml \ "Attacker" \ "Player").headOption
    .map(_.asInstanceOf[Elem])
    .getOrElse {
      throw new IllegalArgumentException("ERROR: Missing 'Player' inside <Attacker> in XML.")
    }

    val player2Xml = (playingFieldXml \ "Defender" \ "Player").headOption
    .map(_.asInstanceOf[Elem])
    .getOrElse {
      throw new IllegalArgumentException("ERROR: Missing 'Player' inside <Defender> in XML.")
    }

    val player1 = playerDeserializer.fromXml(player1Xml)
    val player2 = playerDeserializer.fromXml(player2Xml)

    val player1Hand = (xml \ "player1Hand").headOption match {
      case Some(handElem) =>
        val cards = (handElem \ "Card").map(node => cardDeserializer.fromXml(node.asInstanceOf[Elem])).toList
        handCardsQueueFactory.create(cards)
      case None =>
        handCardsQueueFactory.create(Nil)
    }

    val player2Hand = (xml \ "player2Hand").headOption match {
      case Some(handElem) =>
        val cards = (handElem \ "Card").map(node => cardDeserializer.fromXml(node.asInstanceOf[Elem])).toList
        handCardsQueueFactory.create(cards)
      case None =>
        handCardsQueueFactory.create(Nil)
    }

    val player1Field = (xml \ "player1Field" \ "Card").map(node => cardDeserializer.fromXml(node.asInstanceOf[Elem])).toList
    val player2Field = (xml \ "player2Field" \ "Card").map(node => cardDeserializer.fromXml(node.asInstanceOf[Elem])).toList

    val player1GoalkeeperXml = (xml \ "player1Goalkeeper" \ "Card").headOption
    val player2GoalkeeperXml = (xml \ "player2Goalkeeper" \ "Card").headOption

    val player1Goalkeeper = player1GoalkeeperXml.flatMap { node =>
      Some(cardDeserializer.fromXml(node.asInstanceOf[Elem]))
    }

    val player2Goalkeeper = player2GoalkeeperXml.flatMap { node =>
      Some(cardDeserializer.fromXml(node.asInstanceOf[Elem]))
    }

    val player1ScoreXml = (playingFieldXml \ "Scores" \ "PlayerScores" \ "ScorePlayer1").headOption
    val player2ScoreXml = (playingFieldXml \ "Scores" \ "PlayerScores" \ "ScorePlayer2").headOption

    val player1Score = player1ScoreXml.map(_.text.trim.toInt).getOrElse {
      0
    }

    val player2Score = player2ScoreXml.map(_.text.trim.toInt).getOrElse {
      0
    }

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
    val availableKeys = json.keys.mkString(", ")

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

    val player1Hand = (json \ "player1Hand").validateOpt[JsArray].map {
      case Some(jsArray) => handCardsQueueDeserializer.fromJson(Json.obj("cards" -> jsArray))
      case None => handCardsQueueFactory.create(Nil)
    }.get

    val player2Hand = (json \ "player2Hand").validateOpt[JsArray].map {
      case Some(jsArray) => handCardsQueueDeserializer.fromJson(Json.obj("cards" -> jsArray))
      case None => handCardsQueueFactory.create(Nil)
    }.get

    val player1Field = (json \ "player1Field").validate[List[JsObject]].getOrElse(Nil).map(cardDeserializer.fromJson)
    val player2Field = (json \ "player2Field").validate[List[JsObject]].getOrElse(Nil).map(cardDeserializer.fromJson)

    val player1Goalkeeper = (json \ "player1Goalkeeper").validateOpt[JsObject] match {
      case JsSuccess(Some(jsObj), _) => Some(cardDeserializer.fromJson(jsObj))
      case _ => None
    }

    val player2Goalkeeper = (json \ "player2Goalkeeper").validateOpt[JsObject] match {
      case JsSuccess(Some(jsObj), _) => Some(cardDeserializer.fromJson(jsObj))
      case _ => None
    }

    val scoresJsonOpt = (json \ "playingField" \ "scores").validateOpt[JsObject].getOrElse(None)

    val player1Score = scoresJsonOpt.flatMap(js => (js \ "scorePlayer1").asOpt[Int]).getOrElse(0)
    val player2Score = scoresJsonOpt.flatMap(js => (js \ "scorePlayer2").asOpt[Int]).getOrElse(0)

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