package model.cardComponent.base

import model.cardComponent.base.Suit.Suit
import model.cardComponent.base.Value.Value
import model.cardComponent.ICard

object Card {
  def apply(value: Value, suit: Suit): Card = new RegularCard(value, suit)
}

abstract class Card(val suit: Suit) extends ICard {

  def value: Value

  def revertBoost(): Card

  override def toString: String = s"${value.toString} of ${Suit.suitToString(suit)}"

  override def valueToInt: Int = Value.valueToInt(value)

  override def compare(that: ICard): Int = {
    (this.value, that.value) match {
      case (Value.Two, Value.Ace)  => 1 
      case (Value.Ace, Value.Two)  => -1
      case _ => this.valueToInt.compare(that.valueToInt)
    }
  }

  override def fileName: String = {
    s"${value.toString.toLowerCase.replace(" ", "_")}_of_${Suit.suitToString(suit).toLowerCase}.png"
  }

  override def copy(): Card
}
