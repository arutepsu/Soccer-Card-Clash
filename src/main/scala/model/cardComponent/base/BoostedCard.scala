package model.cardComponent.base

import model.cardComponent.base.Suit.{Clubs, Diamonds, Hearts, Spades, Suit}
import model.cardComponent.base.Value.{Value, *}
import model.cardComponent.base.{RegularCard, Suit, Value, Card}
import model.cardComponent.ICard

class BoostedCard(private val baseCard: RegularCard, var additionalValue: Int = 0)
  extends Card(baseCard.suit) {

  private val originalValue: Value = baseCard.value
  
  override def value: Value = Value.allValues
    .find(v => Value.valueToInt(originalValue) + additionalValue == Value.valueToInt(v))
    .getOrElse(originalValue)

  override def boost(): Card = {
    this
  }

  override def revertBoost(): Card = {
    baseCard.setValue(originalValue)
    baseCard
  }

  override def copy(): Card = new BoostedCard(baseCard, additionalValue)
}
