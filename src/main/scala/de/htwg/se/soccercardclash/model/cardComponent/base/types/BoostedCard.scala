package de.htwg.se.soccercardclash.model.cardComponent.base.types

import de.htwg.se.soccercardclash.model.cardComponent.ICard
import de.htwg.se.soccercardclash.model.cardComponent.base.components.Suit.{Clubs, Diamonds, Hearts, Spades, Suit}
import de.htwg.se.soccercardclash.model.cardComponent.base.components.Value.*
import de.htwg.se.soccercardclash.model.cardComponent.base.components.{Suit, Value}
import de.htwg.se.soccercardclash.model.cardComponent.base.Card
import play.api.libs.json.*


import scala.xml.*
class BoostedCard(private val baseCard: RegularCard, var additionalValue: Int = 0)
  extends Card(baseCard.suit) {

  private val originalValue: Value = baseCard.value

  override def value: Value = Value.allValues
    .find(v => Value.valueToInt(originalValue) + additionalValue == Value.valueToInt(v))
    .getOrElse(originalValue)

  override def boost(): ICard = this

  override def revertBoost(): Card = {
    new RegularCard(originalValue, baseCard.suit)
  }
  override def copy(): Card = new BoostedCard(baseCard, additionalValue)

}
