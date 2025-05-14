package de.htwg.se.soccercardclash.model.playerComponent.factory

import de.htwg.se.soccercardclash.model.cardComponent.ICard
import de.htwg.se.soccercardclash.model.cardComponent.factory.CardDeserializer
import de.htwg.se.soccercardclash.model.playerComponent.IPlayer
import de.htwg.se.soccercardclash.model.playerComponent.base.Player
import de.htwg.se.soccercardclash.model.playerComponent.playerAction.{PlayerActionPolicies, PlayerActionState}
import de.htwg.se.soccercardclash.util.Deserializer
import play.api.libs.json.*


import javax.inject.{Inject, Singleton}
import scala.util.{Failure, Success, Try}
import scala.xml.Elem

@Singleton
class PlayerDeserializer @Inject()(
                                    playerFactory: IPlayerFactory,
                                    cardDeserializer: CardDeserializer
                                  ) extends Deserializer[IPlayer] {

  override def fromXml(xml: Elem): IPlayer = {
    Try {
      val name = (xml \ "@name").text.trim
      if (name.isEmpty) {
        throw new IllegalArgumentException("ERROR: Missing 'name' attribute in Player XML.")
      }

      val actionStates = (xml \ "ActionStates" \ "ActionState").map { node =>
        val policyStr = (node \ "@policy").text.trim
        val policy = PlayerActionPolicies.values.find(_.toString == policyStr)
          .getOrElse(throw new IllegalArgumentException(s"Unknown policy: $policyStr"))

        val state = PlayerActionState.fromString(node.text.trim)

        policy -> state
      }.toMap

      playerFactory.createPlayer(name).setActionStates(actionStates)

    } match {
      case Success(player) => player
      case Failure(exception) =>
        throw new RuntimeException(s"Error parsing Player XML: ${exception.getMessage}", exception)
    }
  }

  override def fromJson(json: JsObject): IPlayer = {
    Try {
      val name = (json \ "name").as[String].trim


      val actionStates = (json \ "actionStates").asOpt[Map[String, String]].getOrElse(Map()).map { case (key, value) =>
        val policy = PlayerActionPolicies.values.find(_.toString == key)
          .getOrElse(throw new IllegalArgumentException(s"Unknown policy: $key"))

        val state = PlayerActionState.fromString(value)
        policy -> state
      }

      playerFactory.createPlayer(name).setActionStates(actionStates)

    } match {
      case Success(player) => player
      case Failure(exception) =>
        throw new RuntimeException(s"Error parsing Player JSON: ${exception.getMessage}", exception)
    }
  }
}