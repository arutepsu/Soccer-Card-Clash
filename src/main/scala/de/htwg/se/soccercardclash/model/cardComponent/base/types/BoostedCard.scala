package de.htwg.se.soccercardclash.model.cardComponent.base.types

import de.htwg.se.soccercardclash.model.cardComponent.base.components.Value.Value
import de.htwg.se.soccercardclash.model.cardComponent.base.components.{Value, *}
import Suit.Suit
import Value.Value
import de.htwg.se.soccercardclash.model.cardComponent.ICard
import play.api.libs.json.*

import scala.xml.*

case class BoostedCard(baseCard: RegularCard, additionalValue: Int = 0) extends ICard {
  override val suit: Suit = baseCard.suit
  private val originalValue: Value = baseCard.value

  override def value: Value = {
    val boosted = Value.valueToInt(originalValue) + additionalValue
    Value.fromInt(boosted).getOrElse(originalValue)
  }

  override def boost(): ICard = this

  override def revertBoost(): ICard = baseCard

}