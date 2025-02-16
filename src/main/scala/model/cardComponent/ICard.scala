package model.cardComponent


import model.cardComponent.cardBaseImplementation.Value.Value
import model.cardComponent.cardBaseImplementation.Suit.Suit

trait ICard {
  def value: Value
  def suit: Suit
  def additionalValue: Int
  def lastBoostValue: Int
  def wasBoosted: Boolean

  def valueToString(value: Value): String
  def valueToInt: Int
  def compare(card1: ICard, card2: ICard): Int
  def getAdditionalValue: Int
  def setAdditionalValue(boost: Int): ICard
  def updateValue(): Unit
  def revertAdditionalValue(): ICard
  def getBoostingPolicies: Int
  def fileName: String
  def copy(): ICard
}

