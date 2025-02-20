package model.cardComponent.base

import model.cardComponent.ICard
import model.cardComponent.base.Suit.{Clubs, Diamonds, Hearts, Spades, Suit}
import model.cardComponent.base.Value.{Value, *}
import model.cardComponent.base.{Card, RegularCard, Suit, Value}

class BoostedCard(private val baseCard: RegularCard, var additionalValue: Int = 0)
  extends Card(baseCard.suit) {

  private val originalValue: Value = baseCard.value

  override def value: Value = Value.allValues
    .find(v => Value.valueToInt(originalValue) + additionalValue == Value.valueToInt(v))
    .getOrElse(originalValue)

  override def boost(): ICard = this

  override def revertBoost(): Card = new RegularCard(originalValue, baseCard.suit)

  override def copy(): Card = new BoostedCard(baseCard, additionalValue)
}
