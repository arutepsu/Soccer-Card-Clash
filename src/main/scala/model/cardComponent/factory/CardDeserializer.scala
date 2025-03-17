package model.cardComponent.factory

import model.cardComponent.ICard
import model.cardComponent.base.components.Suit.Suit
import model.cardComponent.base.components.Value.Value
import model.cardComponent.base.components.{Suit, Value}
import model.cardComponent.base.types.*
import model.cardComponent.factory.ICardFactory
import util.Deserializer
import play.api.libs.json.*

import javax.inject.{Inject, Singleton}
import scala.util.Try
import scala.xml.*

@Singleton
class CardDeserializer @Inject() (val cardFactory: ICardFactory) extends Deserializer[ICard]{

  override def fromXml(xml: Elem): ICard = {
    val rawSuitText = (xml \ "suit").text

    val suitText = rawSuitText.trim

    val cleanSuitText = suitText.replaceAll("\\s+", "")

    val suit = try {
      Suit.withName(cleanSuitText)
    } catch {
      case _: NoSuchElementException =>
        throw new IllegalArgumentException(s"ERROR: Invalid suit value: '$cleanSuitText'")
    }

    val valueText = (xml \ "value").text.trim

    val value = Value.fromString(valueText)
      .getOrElse(throw new IllegalArgumentException(s"Invalid card value: '$valueText'"))

    val cardType = (xml \ "type").text.trim

    val card = cardType match {
      case "Regular" => cardFactory.createCard(value, suit)
      case "Boosted" =>
        val additionalValue = Try((xml \ "additionalValue").text.trim.toInt).getOrElse(0)
        val baseCard = cardFactory.createCard(value, suit)
        new BoostedCard(baseCard.asInstanceOf[RegularCard], additionalValue)
      case _ =>
        throw new IllegalArgumentException(s"Unknown card type: $cardType")
    }

    card
  }

  override def fromJson(json: JsObject): ICard = {

    val suit = Suit.withName((json \ "suit").as[String])
    val value = Value.fromString((json \ "value").as[String])
      .getOrElse(throw new IllegalArgumentException(s"Invalid card value: ${(json \ "value").as[String]}"))
    val cardType = (json \ "type").as[String]

    val card = cardType match {
      case "Regular" => cardFactory.createCard(value, suit)
      case "Boosted" =>
        val additionalValue = (json \ "additionalValue").asOpt[Int].getOrElse(0)
        val baseCard = cardFactory.createCard(value, suit)
        new BoostedCard(baseCard.asInstanceOf[RegularCard], additionalValue)
      case _ => throw new IllegalArgumentException(s"Unknown card type: $cardType")
    }
    card
  }
}
