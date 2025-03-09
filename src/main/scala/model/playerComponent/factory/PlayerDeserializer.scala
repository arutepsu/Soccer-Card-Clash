package model.playerComponent.factory

import play.api.libs.json.{JsObject, Json}
import scala.xml.Elem
//import com.google.inject.{Inject, Singleton}
import model.playerComponent.IPlayer
import model.playerComponent.base.Player
import model.cardComponent.{ICard}
import util.Deserializer
import model.playerComponent.playerAction.{PlayerActionPolicies, PlayerActionState}
import play.api.libs.json._
import javax.inject.Singleton
import model.cardComponent.CardDeserializer

@Singleton
object PlayerDeserializer extends Deserializer[IPlayer] {

  private given playerFactory: IPlayerFactory = summon[IPlayerFactory]

  override def fromXml(xml: Elem): IPlayer = {
    val name = (xml \ "@name").text.trim
    val cards = (xml \ "Cards" \ "Card").map { node =>
      CardDeserializer.fromXml(node.asInstanceOf[Elem])
    }.toList

    val actionStates = (xml \ "ActionStates" \ "ActionState").map { node =>
      val policy = PlayerActionPolicies.values.find(_.toString == (node \ "@policy").text.trim)
        .getOrElse(throw new IllegalArgumentException(s"Unknown policy: ${(node \ "@policy").text.trim}"))

      val state = PlayerActionState.fromString(node.text.trim)

      policy -> state
    }.toMap

    playerFactory.createPlayer(name, cards).setActionStates(actionStates)
  }

  override def fromJson(json: JsObject): IPlayer = {
    val name = (json \ "name").as[String].trim
    val cards = (json \ "cards").asOpt[List[JsObject]].getOrElse(Nil).map(CardDeserializer.fromJson)

    val actionStates = (json \ "actionStates").asOpt[Map[String, String]].getOrElse(Map()).map { case (key, value) =>
      val policy = PlayerActionPolicies.values.find(_.toString == key)
        .getOrElse(throw new IllegalArgumentException(s"Unknown policy: $key"))

      val state = PlayerActionState.fromString(value)

      policy -> state
    }

    playerFactory.createPlayer(name, cards).setActionStates(actionStates)
  }
}
