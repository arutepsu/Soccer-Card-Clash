package model.cardComponent

import play.api.libs.json.{JsObject, Json}
import scala.xml.Elem
import com.google.inject.{Inject, Singleton}
import model.cardComponent.base.components.Suit.Suit
import model.cardComponent.base.components.Value.Value
import model.cardComponent.base.components.{Suit, Value}
import model.cardComponent.base.types.*
import model.cardComponent.factory.ICardFactory

@Singleton
class CardDeserializer @Inject()(cardFactory: ICardFactory) {

  /** Deserialize a list of cards from XML */
  def cardListFromXml(xml: Elem): List[ICard] = {
    (xml \ "Card").flatMap(node => node.headOption.collect {
      case e: Elem => fromXml(e)
    }).toList
  }

  /** Deserialize a single card from XML */
  def fromXml(xml: Elem): ICard = {
    val suit = Suit.withName((xml \ "suit").text) // ✅ Works with Enumeration
    val value = Value.fromString((xml \ "value").text).getOrElse(
      throw new IllegalArgumentException(s"Invalid value: ${(xml \ "value").text}")
    ) // ✅ Fix for Value case objects

    val isBoosted = (xml \ "boosted").headOption.exists(_.text.toBoolean)
    val additionalValue = (xml \ "additionalValue").headOption.map(_.text.toInt).getOrElse(0)

    val baseCard = cardFactory.createCard(value, suit)

    if (isBoosted) new BoostedCard(baseCard.asInstanceOf[RegularCard], additionalValue)
    else baseCard
  }

  /** Deserialize a single card from JSON */
  def fromJson(json: JsObject): ICard = {
    val suit = Suit.withName((json \ "suit").as[String]) // ✅ Works with Enumeration
    val value = Value.fromString((json \ "value").as[String]).getOrElse(
      throw new IllegalArgumentException(s"Invalid value: ${(json \ "value").as[String]}")
    ) // ✅ Fix for Value case objects

    val isBoosted = (json \ "boosted").asOpt[Boolean].getOrElse(false)
    val additionalValue = (json \ "additionalValue").asOpt[Int].getOrElse(0)

    val baseCard = cardFactory.createCard(value, suit)

    if (isBoosted) new BoostedCard(baseCard.asInstanceOf[RegularCard], additionalValue)
    else baseCard
  }
}