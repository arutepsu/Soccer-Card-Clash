package model.cardComponent

import play.api.libs.json.{JsObject, Json}
import util.Deserializer
import scala.xml.Elem
import model.cardComponent.base.components.Suit.Suit
import model.cardComponent.base.components.Value.Value
import model.cardComponent.base.components.{Suit, Value}
import model.cardComponent.base.types.{BoostedCard, *}
import model.cardComponent.factory.ICardFactory

import scala.xml.*
import play.api.libs.json.*
import scala.xml.*
import play.api.libs.json.*

import scala.util.Try

import javax.inject.{Inject, Singleton}
import scala.util.Try
import play.api.libs.json.{JsObject, Json}

@Singleton
class CardDeserializer @Inject() (val cardFactory: ICardFactory) extends Deserializer[ICard]{

  override def fromXml(xml: Elem): ICard = {
    println("DEBUG: Entering CardDeserializer.fromXml")

    val suit = Suit.withName((xml \ "suit").text.trim)
    val value = Value.fromString((xml \ "value").text.trim)
      .getOrElse(throw new IllegalArgumentException(s"Invalid card value: ${(xml \ "value").text.trim}"))
    val cardType = (xml \ "type").text.trim

    println(s"DEBUG: Extracted card data - Suit: $suit, Value: $value, Type: $cardType")

    val card = cardType match {
      case "Regular" => cardFactory.createCard(value, suit)
      case "Boosted" =>
        val additionalValue = Try((xml \ "additionalValue").text.trim.toInt).getOrElse(0)
        val baseCard = cardFactory.createCard(value, suit) // Use factory for consistency
        new BoostedCard(baseCard.asInstanceOf[RegularCard], additionalValue)
      case _ => throw new IllegalArgumentException(s"Unknown card type: $cardType")
    }

    println(s"DEBUG: Created card: $card")
    card
  }

  override def fromJson(json: JsObject): ICard = {
    println("DEBUG: Entering CardDeserializer.fromJson")

    val suit = Suit.withName((json \ "suit").as[String])
    val value = Value.fromString((json \ "value").as[String])
      .getOrElse(throw new IllegalArgumentException(s"Invalid card value: ${(json \ "value").as[String]}"))
    val cardType = (json \ "type").as[String]

    println(s"DEBUG: Extracted card data - Suit: $suit, Value: $value, Type: $cardType")

    val card = cardType match {
      case "Regular" => cardFactory.createCard(value, suit)
      case "Boosted" =>
        val additionalValue = (json \ "additionalValue").asOpt[Int].getOrElse(0)
        val baseCard = cardFactory.createCard(value, suit)
        new BoostedCard(baseCard.asInstanceOf[RegularCard], additionalValue)
      case _ => throw new IllegalArgumentException(s"Unknown card type: $cardType")
    }

    println(s"DEBUG: Created card: $card")
    card
  }
}
