package model.cardComponent
import model.cardComponent.base.Suit.Suit
import model.cardComponent.base.Value.Value

trait ICard {
  def value: Value
  def suit: Suit
  def boost(): ICard
  def revertBoost(): ICard
  def valueToInt: Int
  def compare(that: ICard): Int
  def fileName: String
  def copy(): ICard
  def hashCode(): Int
  def equals(obj: Any): Boolean
}
