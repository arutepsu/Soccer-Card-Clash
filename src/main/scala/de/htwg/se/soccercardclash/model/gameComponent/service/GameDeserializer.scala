package de.htwg.se.soccercardclash.model.gameComponent.service

import com.google.inject.{Inject, Singleton}
import de.htwg.se.soccercardclash.model.cardComponent.ICard
import de.htwg.se.soccercardclash.model.cardComponent.dataStructure.*
import de.htwg.se.soccercardclash.model.cardComponent.factory.CardDeserializer
import de.htwg.se.soccercardclash.model.gameComponent.IGameState
import de.htwg.se.soccercardclash.model.gameComponent.action.manager.*
import de.htwg.se.soccercardclash.model.gameComponent.base.GameState
import de.htwg.se.soccercardclash.model.gameComponent.components.*
import de.htwg.se.soccercardclash.model.playerComponent.IPlayer
import de.htwg.se.soccercardclash.model.playerComponent.factory.PlayerDeserializer
import de.htwg.se.soccercardclash.model.playerComponent.playerAction.*
import de.htwg.se.soccercardclash.util.{Deserializer, Serializable}
import play.api.libs.json.*

import scala.xml.*

@Singleton
class GameDeserializer @Inject()(
                                  playerDeserializer: PlayerDeserializer,
                                  cardDeserializer: CardDeserializer,
                                  handCardsQueueFactory: IHandCardsQueueFactory,
                                  handCardsFactory: IHandCardsFactory,
                                  fieldCardsFactory: IFieldCardsFactory,
                                  gameCardsFactory: IGameCardsFactory,
                                  rolesFactory: IRolesFactory,
                                  scoresFactory: IScoresFactory
                                ) extends Deserializer[IGameState] {

  override def fromXml(xml: Elem): IGameState = {
    def extractInner(tag: String): Elem =
      (xml \ tag).headOption
        .flatMap(_.child.collect { case e: Elem => e }.headOption)
        .getOrElse(throw new IllegalArgumentException(s"Missing <$tag> inner element"))

    val attacker = playerDeserializer.fromXml(extractInner("attacker"))
    val defender = playerDeserializer.fromXml(extractInner("defender"))

    val player1HandCards = (xml \ "attackerHand").headOption
      .map(_ \ "Card")
      .getOrElse(NodeSeq.Empty)
      .collect { case e: Elem => cardDeserializer.fromXml(e) }
      .toList

    val player2HandCards = (xml \ "defenderHand").headOption
      .map(_ \ "Card")
      .getOrElse(NodeSeq.Empty)
      .collect { case e: Elem => cardDeserializer.fromXml(e) }
      .toList

    val player1Defenders: List[Option[ICard]] =
      (xml \ "attackerField" \ "Card").map {
        case e: Elem =>
          val isNil = e.attributes.asAttrMap.get("xsi:nil").contains("true")
          if (isNil || e.child.isEmpty) {
            None
          } else {
            Some(cardDeserializer.fromXml(e))
          }
        case _ => None
      }.toList.padTo(3, None)

    val player2Defenders: List[Option[ICard]] =
      (xml \ "defenderField" \ "Card").map {
        case e: Elem =>
          val isNil = e.attributes.asAttrMap.get("xsi:nil").contains("true")
          if (isNil || e.child.isEmpty) {
            None
          } else {
            Some(cardDeserializer.fromXml(e))
          }
        case _ => None
      }.toList.padTo(3, None)

    val player1Goalkeeper = (xml \ "attackerGoalkeeper" \ "Card").headOption.collect { case e: Elem => cardDeserializer.fromXml(e) }
    val player2Goalkeeper = (xml \ "defenderGoalkeeper" \ "Card").headOption.collect { case e: Elem => cardDeserializer.fromXml(e) }

    val player1Score = (xml \ "attackerScore").headOption.map(_.text.trim.toInt).getOrElse(0)
    val player2Score = (xml \ "defenderScore").headOption.map(_.text.trim.toInt).getOrElse(0)

    val gameCards = gameCardsFactory.createFromData(
      attacker = attacker,
      attackerHand = player1HandCards,
      defender = defender,
      defenderHand = player2HandCards,
      attackerDefenders = player1Defenders,
      defenderDefenders = player2Defenders,
      attackerGoalkeeper = player1Goalkeeper,
      defenderGoalkeeper = player2Goalkeeper
    )

    val scores = scoresFactory.createWithScores(Map(
      attacker -> player1Score,
      defender -> player2Score
    ))

    val roles = rolesFactory.create(attacker, defender)

    GameState(gameCards, roles, scores)
  }

  override def fromJson(json: JsObject): IGameState = {

    val attackerJson = (json \ "attacker").as[JsObject]
    val defenderJson = (json \ "defender").as[JsObject]

    val attacker = playerDeserializer.fromJson(attackerJson)
    val defender = playerDeserializer.fromJson(defenderJson)

    val player1HandCards = (json \ "attackerHand").asOpt[List[JsObject]].getOrElse(Nil).map(cardDeserializer.fromJson)
    val player2HandCards = (json \ "defenderHand").asOpt[List[JsObject]].getOrElse(Nil).map(cardDeserializer.fromJson)

    val player1Defenders: List[Option[ICard]] =
      (json \ "attackerField").asOpt[List[JsValue]].getOrElse(Nil)
        .map {
          case JsNull => None
          case obj: JsObject => Some(cardDeserializer.fromJson(obj))
          case other => throw new IllegalArgumentException(s"Invalid card JSON: $other")
        }
        .padTo(3, None)

    val player2Defenders: List[Option[ICard]] =
      (json \ "defenderField").asOpt[List[JsValue]].getOrElse(Nil)
        .map {
          case JsNull => None
          case obj: JsObject => Some(cardDeserializer.fromJson(obj))
          case other => throw new IllegalArgumentException(s"Invalid card JSON: $other")
        }
        .padTo(3, None)


    val player1Goalkeeper = (json \ "attackerGoalkeeper").asOpt[JsObject].map(cardDeserializer.fromJson)
    val player2Goalkeeper = (json \ "defenderGoalkeeper").asOpt[JsObject].map(cardDeserializer.fromJson)

    val player1Score = (json \ "attackerScore").asOpt[Int].getOrElse(0)
    val player2Score = (json \ "defenderScore").asOpt[Int].getOrElse(0)

    val gameCards = gameCardsFactory.createFromData(
      attacker,
      player1HandCards,
      defender,
      player2HandCards,
      player1Defenders,
      player2Defenders,
      player1Goalkeeper,
      player2Goalkeeper
    )

    val scores = scoresFactory.createWithScores(Map(
      attacker -> player1Score,
      defender -> player2Score
    ))

    val roles = rolesFactory.create(attacker, defender)

    GameState(gameCards, roles, scores)
  }

}
