package de.htwg.se.soccercardclash.model.cardComponent.factory

import de.htwg.se.soccercardclash.model.cardComponent.ICard
import de.htwg.se.soccercardclash.model.cardComponent.base.components.Suit.Suit
import de.htwg.se.soccercardclash.model.cardComponent.base.components.Value.Value
import de.htwg.se.soccercardclash.model.cardComponent.base.components.*
import de.htwg.se.soccercardclash.model.cardComponent.base.types.*
import de.htwg.se.soccercardclash.util.Deserializer
import play.api.libs.json.*

import javax.inject.{Inject, Singleton}
import scala.util.{Failure, Success, Try}
import scala.xml.*


@Singleton
class CardDeserializer @Inject()(val cardFactory: ICardFactory) extends Deserializer[ICard] {

  override def fromXml(xml: Elem): ICard = {
    val rawSuitText = (xml \ "suit").text

    val suitText = rawSuitText.trim

    val cleanSuitText = suitText.replaceAll("\\s+", "")

    val suit = Try(Suit.withName(cleanSuitText))
      .getOrElse(throw new IllegalArgumentException(s"ERROR: Invalid suit value: '$cleanSuitText'"))

    val valueText = (xml \ "value").text.trim

    val value = Value.fromString(valueText)
      .getOrElse(throw new IllegalArgumentException(s"Invalid card value: '$valueText'"))

    val cardType = (xml \ "type").text.trim

    Try {
      cardType match {
        case "Regular" => cardFactory.createCard(value, suit)
        case "Boosted" =>
          val additionalValue = Try((xml \ "additionalValue").text.trim.toInt)
            .getOrElse(throw new IllegalArgumentException("Missing or invalid <additionalValue> for Boosted card"))

          cardFactory.createCard(value, suit) match {
            case regular: RegularCard => BoostedCard(regular, additionalValue)
            case other =>
              throw new IllegalStateException(s"Expected RegularCard, but got: ${other.getClass.getSimpleName}")
          }

        case _ =>
          throw new IllegalArgumentException(s"Unknown card type: $cardType")
      }
    } match {
      case Success(card) => card
      case Failure(exception) => throw new IllegalArgumentException(s"Failed to parse card: ${exception.getMessage}")
    }
  }

  override def fromJson(json: JsObject): ICard = {
    Try {
      val suit = Suit.withName((json \ "suit").as[String])
      val value = Value.fromString((json \ "value").as[String])
        .getOrElse(throw new IllegalArgumentException(s"Invalid card value: ${(json \ "value").as[String]}"))
      val cardType = (json \ "type").as[String]

      cardType match {
        case "Regular" => cardFactory.createCard(value, suit)
        case "Boosted" =>
          val additionalValue = (json \ "additionalValue").asOpt[Int].getOrElse(0)
          val baseCard = cardFactory.createCard(value, suit)
          BoostedCard(baseCard.asInstanceOf[RegularCard], additionalValue)
        case _ => throw new IllegalArgumentException(s"Unknown card type: $cardType")
      }
    } match {
      case Success(card) => card
      case Failure(exception) => throw new IllegalArgumentException(s"Failed to parse card JSON: ${exception.getMessage}")
    }
  }
}
