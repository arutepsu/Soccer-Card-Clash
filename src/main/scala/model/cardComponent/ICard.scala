package model.cardComponent
import model.cardComponent.base.Suit.Suit
import model.cardComponent.base.Value.Value
import model.cardComponent.base.Card

trait ICard {

  def value: Value
  def suit: Suit
  def boost(): Card
  def revertBoost(): Card
  def valueToInt: Int
  def compare(that: ICard): Int
  def fileName: String
  def copy(): Card

}
