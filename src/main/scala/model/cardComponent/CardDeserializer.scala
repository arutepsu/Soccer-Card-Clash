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
    println(s"DEBUG: Received Card XML:\n$xml") // ✅ Print the full card XML

    // ✅ Extract raw suit text and print it
    val rawSuitText = (xml \ "suit").text
    println(s"DEBUG: Extracted raw suit value: '$rawSuitText'")

    val suitText = rawSuitText.trim
    println(s"DEBUG: Trimmed suit value: '$suitText'")

    if (suitText.isEmpty) {
      println("❌ ERROR: Missing <suit> in this card XML:\n" + xml)
      throw new IllegalArgumentException("ERROR: Missing 'suit' value in XML.")
    }

    val cleanSuitText = suitText.replaceAll("\\s+", "") // ✅ Remove unexpected spaces

    val suit = try {
      Suit.withName(cleanSuitText)
    } catch {
      case _: NoSuchElementException =>
        println(s"❌ ERROR: Invalid suit value after cleanup: '$cleanSuitText' in XML:\n$xml")
        throw new IllegalArgumentException(s"ERROR: Invalid suit value: '$cleanSuitText'")
    }

    // ✅ Extract and validate `<value>`
    val valueText = (xml \ "value").text.trim
    if (valueText.isEmpty) {
      println("❌ ERROR: Missing <value> in this card XML:\n" + xml)
      throw new IllegalArgumentException("ERROR: Missing 'value' for card in XML.")
    }

    val value = Value.fromString(valueText)
      .getOrElse(throw new IllegalArgumentException(s"Invalid card value: '$valueText'"))

    // ✅ Extract and validate `<type>`
    val cardType = (xml \ "type").text.trim
    if (cardType.isEmpty) {
      println("❌ ERROR: Missing <type> in this card XML:\n" + xml)
      throw new IllegalArgumentException("ERROR: Missing 'type' for card in XML.")
    }

    println(s"✅ DEBUG: Extracted card data - Suit: '$suit', Value: '$value', Type: '$cardType'")

    val card = cardType match {
      case "Regular" => cardFactory.createCard(value, suit)
      case "Boosted" =>
        val additionalValue = Try((xml \ "additionalValue").text.trim.toInt).getOrElse(0)
        val baseCard = cardFactory.createCard(value, suit)
        new BoostedCard(baseCard.asInstanceOf[RegularCard], additionalValue)
      case _ =>
        println(s"❌ ERROR: Unknown card type '$cardType' in XML:\n$xml")
        throw new IllegalArgumentException(s"Unknown card type: $cardType")
    }

    println(s"✅ DEBUG: Created card: $card")
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
