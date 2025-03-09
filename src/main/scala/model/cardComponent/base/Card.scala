package model.cardComponent.base

import model.cardComponent.ICard
import model.cardComponent.base.components.Suit.Suit
import model.cardComponent.base.components.Value.Value
import model.cardComponent.base.components.{Suit, Value}
import model.cardComponent.base.types.*
import play.api.libs.json.{Json, JsObject}
import scala.xml.Elem

object Card {
  def apply(value: Value, suit: Suit): ICard = new RegularCard(value, suit)
}

abstract class Card(val suit: Suit) extends ICard {

  def value: Value

  def revertBoost(): ICard

  override def toString: String = s"${value.toString} of ${Suit.suitToString(suit)}"

  override def compare(that: ICard): Int = {
    (this.value, that.value) match {
      case (Value.Two, Value.Ace) => 1
      case (Value.Ace, Value.Two) => -1
      case _ => this.valueToInt.compare(that.valueToInt)
    }
  }

  override def valueToInt: Int = Value.valueToInt(value)

  override def fileName: String = {
    s"${value.toString.toLowerCase.replace(" ", "_")}_of_${Suit.suitToString(suit).toLowerCase}.png"
  }

  override def copy(): ICard

  override def hashCode(): Int = (value, suit).##

  override def equals(obj: Any): Boolean = obj match {
    case card: ICard => this.value == card.value && this.suit == card.suit
    case _ => false
  }
  
}
