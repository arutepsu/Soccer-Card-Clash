package model.cardComponent

import play.api.libs.json.{JsObject, Json}
import util.Deserializer
import scala.xml.Elem
//import com.google.inject.{Inject, Singleton}
import model.cardComponent.base.components.Suit.Suit
import model.cardComponent.base.components.Value.Value
import model.cardComponent.base.components.{Suit, Value}
import model.cardComponent.base.types.{BoostedCard, *}
import model.cardComponent.factory.ICardFactory

import scala.xml.*
import play.api.libs.json.*

import javax.inject.{Inject, Singleton}
import scala.xml.*
import play.api.libs.json.*

import scala.util.Try

import javax.inject.{Inject, Singleton}
import scala.util.Try
import play.api.libs.json.{JsObject, Json}
//import javax.inject.{Inject, Singleton}
@Singleton
object CardDeserializer extends Deserializer[ICard] {
  
  private given cardFactory: ICardFactory = summon[ICardFactory]

  override def fromXml(xml: Elem): ICard = {
    val suit = Suit.withName((xml \ "suit").text.trim)
    val value = Value.fromString((xml \ "value").text.trim)
      .getOrElse(throw new IllegalArgumentException(s"Invalid card value: ${(xml \ "value").text.trim}"))
    val cardType = (xml \ "type").text.trim

    cardType match {
      case "Regular" => cardFactory.createCard(value, suit)
      case "Boosted" =>
        val additionalValue = Try((xml \ "additionalValue").text.trim.toInt).getOrElse(0)
        val baseCard = cardFactory.createCard(value, suit) // Use factory for consistency
        new BoostedCard(baseCard.asInstanceOf[RegularCard], additionalValue)
      case _ => throw new IllegalArgumentException(s"Unknown card type: $cardType")
    }
  }

  override def fromJson(json: JsObject): ICard = {
    val suit = Suit.withName((json \ "suit").as[String])
    val value = Value.fromString((json \ "value").as[String])
      .getOrElse(throw new IllegalArgumentException(s"Invalid card value: ${(json \ "value").as[String]}"))
    val cardType = (json \ "type").as[String]

    cardType match {
      case "Regular" => cardFactory.createCard(value, suit)
      case "Boosted" =>
        val additionalValue = (json \ "additionalValue").asOpt[Int].getOrElse(0)
        val baseCard = cardFactory.createCard(value, suit)
        new BoostedCard(baseCard.asInstanceOf[RegularCard], additionalValue)
      case _ => throw new IllegalArgumentException(s"Unknown card type: $cardType")
    }
  }
}

