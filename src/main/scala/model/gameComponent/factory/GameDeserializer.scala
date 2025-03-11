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
    println(s"DEBUG: Full XML received:\n$xml") // ✅ Print full XML before parsing

    // ✅ Use `\\` to search globally for <playingField>
    // ✅ Use `\\` to search globally for <playingField>
    val playingFieldXml = (xml \\ "playingField").headOption
    .map(_.asInstanceOf[Elem])
    .getOrElse {
      println("❌ ERROR: Missing <playingField> in XML!")
      throw new IllegalArgumentException("ERROR: Missing 'playingField' element in XML.")
    }

    println(s"✅ DEBUG: Extracted playingFieldXml: $playingFieldXml")

    val playingField = playingFieldDeserializer.fromXml(playingFieldXml)

    // ✅ Ensure <Attacker> and <Defender> are correctly extracted
    val player1Xml = (playingFieldXml \ "Attacker" \ "Player").headOption
    .map(_.asInstanceOf[Elem])
    .getOrElse {
      println("❌ ERROR: Missing <Player> inside <Attacker> in XML!")
      throw new IllegalArgumentException("ERROR: Missing 'Player' inside <Attacker> in XML.")
    }

    val player2Xml = (playingFieldXml \ "Defender" \ "Player").headOption
    .map(_.asInstanceOf[Elem])
    .getOrElse {
      println("❌ ERROR: Missing <Player> inside <Defender> in XML!")
      throw new IllegalArgumentException("ERROR: Missing 'Player' inside <Defender> in XML.")
    }

    println(s"✅ DEBUG: Extracted Player1 XML:\n$player1Xml")
    println(s"✅ DEBUG: Extracted Player2 XML:\n$player2Xml")

    val player1 = playerDeserializer.fromXml(player1Xml)
    val player2 = playerDeserializer.fromXml(player2Xml)


    println(s"DEBUG: Extracted players - Player1: $player1, Player2: $player2")

    // Deserialize player hands
    // ✅ Extract cards from inside <player1Hand>
    val player1Hand = (xml \ "player1Hand").headOption match {
      case Some(handElem) =>
        val cards = (handElem \ "Card").map(node => cardDeserializer.fromXml(node.asInstanceOf[Elem])).toList
        HandCardsQueueFactory.create(cards)
      case None =>
        println("⚠️ WARNING: Missing <player1Hand> in XML, creating an empty hand.")
        HandCardsQueueFactory.create(Nil)
    }

    // ✅ Extract cards from inside <player2Hand>
    val player2Hand = (xml \ "player2Hand").headOption match {
      case Some(handElem) =>
        val cards = (handElem \ "Card").map(node => cardDeserializer.fromXml(node.asInstanceOf[Elem])).toList
        HandCardsQueueFactory.create(cards)
      case None =>
        println("⚠️ WARNING: Missing <player2Hand> in XML, creating an empty hand.")
        HandCardsQueueFactory.create(Nil)
    }


    println("DEBUG: Extracted hand cards.")

    // Deserialize field cards
    val player1Field = (xml \ "player1Field" \ "Card").map(node => cardDeserializer.fromXml(node.asInstanceOf[Elem])).toList
    val player2Field = (xml \ "player2Field" \ "Card").map(node => cardDeserializer.fromXml(node.asInstanceOf[Elem])).toList

    println("DEBUG: Extracted field cards.")

    // Deserialize goalkeepers
    val player1GoalkeeperXml = (xml \ "player1Goalkeeper" \ "Card").headOption
    val player2GoalkeeperXml = (xml \ "player2Goalkeeper" \ "Card").headOption

    println(s"DEBUG: Player1Goalkeeper XML:\n$player1GoalkeeperXml")
    println(s"DEBUG: Player2Goalkeeper XML:\n$player2GoalkeeperXml")

    // ✅ Extract <Card> from inside <player1Goalkeeper> before deserializing
    val player1Goalkeeper = player1GoalkeeperXml.flatMap { node =>
      Some(cardDeserializer.fromXml(node.asInstanceOf[Elem])) // ✅ Correctly passing <Card>
    }

    val player2Goalkeeper = player2GoalkeeperXml.flatMap { node =>
      Some(cardDeserializer.fromXml(node.asInstanceOf[Elem])) // ✅ Correctly passing <Card>
    }

    println("DEBUG: Extracted goalkeepers.")

    // ✅ Extract and print scores from <playingField>
    // Extract scores from correct XML path
    val player1ScoreXml = (playingFieldXml \ "Scores" \ "PlayerScores" \ "ScorePlayer1").headOption
    val player2ScoreXml = (playingFieldXml \ "Scores" \ "PlayerScores" \ "ScorePlayer2").headOption

    println(s"DEBUG: Player1Score XML:\n$player1ScoreXml")
    println(s"DEBUG: Player2Score XML:\n$player2ScoreXml")

    val player1Score = player1ScoreXml.map(_.text.trim.toInt).getOrElse {
      println("⚠️ WARNING: Missing <ScorePlayer1> in XML, defaulting to 0.")
      0
    }

    val player2Score = player2ScoreXml.map(_.text.trim.toInt).getOrElse {
      println("⚠️ WARNING: Missing <ScorePlayer2> in XML, defaulting to 0.")
      0
    }

    println(s"✅ DEBUG: Extracted Player1Score = $player1Score, Player2Score = $player2Score")


    println(s"✅ DEBUG: Extracted Player1Score = $player1Score, Player2Score = $player2Score")


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