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
import javax.inject.{Singleton, Inject}
import model.cardComponent.CardDeserializer

@Singleton
class PlayerDeserializer @Inject() (
                                     playerFactory: IPlayerFactory,
                                     cardDeserializer: CardDeserializer
                                   ) extends Deserializer[IPlayer] {

  override def fromXml(xml: Elem): IPlayer = {
    println("DEBUG: Entering PlayerDeserializer.fromXml")

    val name = (xml \ "@name").text.trim
    if (name.isEmpty) {
      println("❌ ERROR: Missing 'name' attribute in Player XML!")
      throw new IllegalArgumentException("ERROR: Missing 'name' attribute in Player XML.")
    }
    println(s"✅ DEBUG: Extracted name: '$name'")

    val cards = (xml \ "Cards" \ "Card").map { node =>
      val card = cardDeserializer.fromXml(node.asInstanceOf[Elem])
      println(s"✅ DEBUG: Extracted card: $card")
      card
    }.toList

    val actionStates = (xml \ "ActionStates" \ "ActionState").map { node =>
      val policyStr = (node \ "@policy").text.trim
      val policy = PlayerActionPolicies.values.find(_.toString == policyStr)
        .getOrElse(throw new IllegalArgumentException(s"Unknown policy: $policyStr"))

      val state = PlayerActionState.fromString(node.text.trim)
      println(s"✅ DEBUG: Extracted action state: $policy -> $state")

      policy -> state
    }.toMap

    println(s"✅ DEBUG: Extracted actionStates: $actionStates")

    val player = playerFactory.createPlayer(name, cards).setActionStates(actionStates)
    println(s"✅ DEBUG: Created player: $player")


    println(s"✅ DEBUG: Player '${player.name}' registered in PlayerHandManager.")

    player
  }

  override def fromJson(json: JsObject): IPlayer = {
    println("DEBUG: Entering PlayerDeserializer.fromJson")

    val name = (json \ "name").as[String].trim
    println(s"DEBUG: Extracted name: $name")

    val cards = (json \ "cards").asOpt[List[JsObject]].getOrElse(Nil).map(cardDeserializer.fromJson) // ✅ Use injected cardDeserializer
    println(s"DEBUG: Extracted cards: $cards")

    val actionStates = (json \ "actionStates").asOpt[Map[String, String]].getOrElse(Map()).map { case (key, value) =>
      val policy = PlayerActionPolicies.values.find(_.toString == key)
        .getOrElse(throw new IllegalArgumentException(s"Unknown policy: $key"))

      val state = PlayerActionState.fromString(value)
      policy -> state
    }
    println(s"DEBUG: Extracted actionStates: $actionStates")

    val player = playerFactory.createPlayer(name, cards).setActionStates(actionStates)
    println(s"DEBUG: Created player: $player")

    player
  }
}