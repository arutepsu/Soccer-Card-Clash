package model.cardComponent.base.types

import model.cardComponent.ICard
import model.cardComponent.base.components.Suit.{Clubs, Diamonds, Hearts, Spades, Suit}
import model.cardComponent.base.components.Value.*
import model.cardComponent.base.components.{Suit, Value}
import model.cardComponent.base.Card
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
    println(s"♻️ Reverting BoostedCard: $this -> ${new RegularCard(originalValue, baseCard.suit)}")
    new RegularCard(originalValue, baseCard.suit)
  }
  override def copy(): Card = new BoostedCard(baseCard, additionalValue)

}
