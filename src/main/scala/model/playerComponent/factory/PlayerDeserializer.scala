package model.playerComponent.factory

import play.api.libs.json.{JsObject, Json}
import scala.xml.Elem
import com.google.inject.{Inject, Singleton}
import model.playerComponent.IPlayer
import model.playerComponent.base.Player
import model.cardComponent.{ICard, CardDeserializer}
import model.playerComponent.playerAction.{PlayerActionPolicies, PlayerActionState}

@Singleton
class PlayerDeserializer @Inject()(cardDeserializer: CardDeserializer, playerFactory: IPlayerFactory) {

  /** Deserialize a list of players from XML */
  def playerListFromXml(xml: Elem): List[IPlayer] = {
    (xml \ "Player").collect { case e: Elem => fromXml(e) }.toList
  }

  /** Deserialize a single Player from XML */
  def fromXml(xml: Elem): IPlayer = {
    val name = (xml \ "name").text
    val cards = (xml \ "cards" \ "Card").collect { case e: Elem => cardDeserializer.fromXml(e) }.toList

    val player = playerFactory.createPlayer(name, cards) // Create player without action states

    var actionStates: Map[PlayerActionPolicies, PlayerActionState] =
      (xml \ "actionStates" \ "action").collect { case actionNode: Elem =>
        val policy = PlayerActionPolicies.fromString((actionNode \ "policy").text)
          .getOrElse(throw new IllegalArgumentException(s"Invalid policy: ${(actionNode \ "policy").text}"))
        val state = PlayerActionState.fromString((actionNode \ "state").text)
        policy -> state
      }.toMap

    player match {
      case p: Player => p.copy(actionStates = actionStates) // ✅ Safe casting
      case _ => throw new IllegalStateException("Deserialization failed: PlayerFactory returned an unexpected type")
    }
  }

  /** Deserialize a single Player from JSON */
  def fromJson(json: JsObject): IPlayer = {
    val name = (json \ "name").as[String]
    val cards = (json \ "cards").as[List[JsObject]].map(cardDeserializer.fromJson)

    val player = playerFactory.createPlayer(name, cards) // Create player without action states

    var actionStates: Map[PlayerActionPolicies, PlayerActionState] =
      (json \ "actionStates").as[Map[String, String]].map {
        case (policyStr, stateStr) =>
          val policy = PlayerActionPolicies.fromString(policyStr)
            .getOrElse(throw new IllegalArgumentException(s"Invalid policy: $policyStr"))
          val state = PlayerActionState.fromString(stateStr)
          policy -> state
      }

    player match {
      case p: Player => p.copy(actionStates = actionStates) // ✅ Safe casting
      case _ => throw new IllegalStateException("Deserialization failed: PlayerFactory returned an unexpected type")
    }
  }
}
