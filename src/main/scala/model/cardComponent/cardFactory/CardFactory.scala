package model.cardComponent.cardFactory

import model.cardComponent.ICard
import model.cardComponent.base.Suit.Suit
import model.cardComponent.base.Value.Value
import model.cardComponent.base.{BoostedCard, RegularCard, Suit, Value}

import scala.util.Random
object CardFactory {
  def createCard(value: Value, suit: Suit): ICard =
    RegularCard(value, suit)
  
}
