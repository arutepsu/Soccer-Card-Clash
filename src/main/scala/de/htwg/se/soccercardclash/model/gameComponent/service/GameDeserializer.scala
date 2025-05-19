package de.htwg.se.soccercardclash.model.gameComponent.service

import com.google.inject.{Inject, Singleton}
import de.htwg.se.soccercardclash.model.cardComponent.ICard
import de.htwg.se.soccercardclash.model.cardComponent.factory.CardDeserializer
import de.htwg.se.soccercardclash.model.playerComponent.IPlayer
import de.htwg.se.soccercardclash.model.playerComponent.factory.PlayerDeserializer
import de.htwg.se.soccercardclash.model.playerComponent.playerAction.*
import de.htwg.se.soccercardclash.model.gameComponent.state.IGameState
import de.htwg.se.soccercardclash.model.cardComponent.dataStructure.*
import de.htwg.se.soccercardclash.model.gameComponent.state.base.GameState
import de.htwg.se.soccercardclash.model.gameComponent.state.components.{IDataManagerFactory, IFieldCardsFactory, IHandCardsFactory, IRolesFactory, IScoresFactory}
import de.htwg.se.soccercardclash.model.gameComponent.state.manager.*
import de.htwg.se.soccercardclash.util.{Deserializer, Serializable}
import play.api.libs.json.*

import scala.xml.*

@Singleton
class GameDeserializer @Inject() (
                                   playerDeserializer: PlayerDeserializer,
                                   cardDeserializer: CardDeserializer,
                                   handCardsQueueFactory: IHandCardsQueueFactory,
                                   handManagerFactory: IHandCardsFactory,
                                   fieldManagerFactory: IFieldCardsFactory,
                                   dataManagerFactory: IDataManagerFactory,
                                   rolesFactory: IRolesFactory,
                                   scoresFactory: IScoresFactory
                                 ) extends Deserializer[IGameState] {

  override def fromXml(xml: Elem): IGameState = {
    def extractInner(tag: String): Elem =
      (xml \ tag).headOption
        .flatMap(_.child.collectFirst { case e: Elem => e })
        .getOrElse(throw new IllegalArgumentException(s"Missing <$tag> inner element"))

    val attacker = playerDeserializer.fromXml(extractInner("attacker"))
    val defender = playerDeserializer.fromXml(extractInner("defender"))

    val player1HandCards = (xml \ "player1Hand").headOption
      .map(_ \ "Card")
      .getOrElse(NodeSeq.Empty)
      .collect { case e: Elem => cardDeserializer.fromXml(e) }
      .toList

    val player2HandCards = (xml \ "player2Hand").headOption
      .map(_ \ "Card")
      .getOrElse(NodeSeq.Empty)
      .collect { case e: Elem => cardDeserializer.fromXml(e) }
      .toList

    val player1Defenders: List[Option[ICard]] =
      (xml \ "player1Field" \ "Card").collect { case e: Elem => Some(cardDeserializer.fromXml(e)) }.toList.padTo(3, None)

    val player2Defenders: List[Option[ICard]] =
      (xml \ "player2Field" \ "Card").collect { case e: Elem => Some(cardDeserializer.fromXml(e)) }.toList.padTo(3, None)


    val player1Goalkeeper = (xml \ "player1Goalkeeper" \ "Card").headOption.collect { case e: Elem => cardDeserializer.fromXml(e) }
    val player2Goalkeeper = (xml \ "player2Goalkeeper" \ "Card").headOption.collect { case e: Elem => cardDeserializer.fromXml(e) }

    val player1Score = (xml \ "player1Score").headOption.map(_.text.trim.toInt).getOrElse(0)
    val player2Score = (xml \ "player2Score").headOption.map(_.text.trim.toInt).getOrElse(0)

    val dataManager = dataManagerFactory.createFromData(
      player1 = attacker,
      player1Hand = player1HandCards,
      player2 = defender,
      player2Hand = player2HandCards,
      player1Defenders = player1Defenders,
      player2Defenders = player2Defenders,
      player1Goalkeeper = player1Goalkeeper,
      player2Goalkeeper = player2Goalkeeper
    )

    val scores = scoresFactory.create(attacker, defender)
      .setScorePlayer1(player1Score)
      .setScorePlayer2(player2Score)

    val roles = rolesFactory.create(attacker, defender)

    GameState(dataManager, roles, scores)
  }

  override def fromJson(json: JsObject): IGameState = {

    val attackerJson = (json \ "attacker").as[JsObject]
    val defenderJson = (json \ "defender").as[JsObject]

    val attacker = playerDeserializer.fromJson(attackerJson)
    val defender = playerDeserializer.fromJson(defenderJson)

    val player1HandCards = (json \ "player1Hand").asOpt[List[JsObject]].getOrElse(Nil).map(cardDeserializer.fromJson)
    val player2HandCards = (json \ "player2Hand").asOpt[List[JsObject]].getOrElse(Nil).map(cardDeserializer.fromJson)

    val player1Defenders: List[Option[ICard]] =
      (json \ "player1Field").asOpt[List[JsObject]].getOrElse(Nil)
        .map(obj => Some(cardDeserializer.fromJson(obj)))
        .padTo(3, None)

    val player2Defenders: List[Option[ICard]] =
      (json \ "player2Field").asOpt[List[JsObject]].getOrElse(Nil)
        .map(obj => Some(cardDeserializer.fromJson(obj)))
        .padTo(3, None)

    val player1Goalkeeper = (json \ "player1Goalkeeper").asOpt[JsObject].map(cardDeserializer.fromJson)
    val player2Goalkeeper = (json \ "player2Goalkeeper").asOpt[JsObject].map(cardDeserializer.fromJson)

    val player1Score = (json \ "player1Score").asOpt[Int].getOrElse(0)
    val player2Score = (json \ "player2Score").asOpt[Int].getOrElse(0)

    val dataManager = dataManagerFactory.createFromData(
      attacker,
      player1HandCards,
      defender,
      player2HandCards,
      player1Defenders,
      player2Defenders,
      player1Goalkeeper,
      player2Goalkeeper
    )

    val scores = scoresFactory.create(attacker, defender)
      .setScorePlayer1(player1Score)
      .setScorePlayer2(player2Score)

    val roles = rolesFactory.create(attacker, defender)

    GameState(dataManager, roles, scores)
  }

}
