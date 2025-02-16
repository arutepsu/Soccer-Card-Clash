package model.cardComponent.cardFactory

import model.cardComponent.base.Suit.Suit
import model.cardComponent.base.Value.Value
import model.cardComponent.base.{Card, Suit, Value}

import scala.util.Random
object CardFactory {
  def createCard(value: Value, suit: Suit): Card =
    Card.apply(value, suit) // ✅ Calls the manually defined apply method
  

  /** ✅ Creates a card from a string representation */
  def createCardFromString(valueStr: String, suitStr: String): Option[Card] = {
    for {
      value <- Value.fromString(valueStr)
      suit  <- Suit.fromString(suitStr)
    } yield Card.apply(value, suit)
  }

  /** ✅ Creates a card from an integer value */
  def createCardFromInt(valueInt: Int, suit: Suit): Option[Card] = {
    Value.fromInt(valueInt).map(v => Card.apply(v, suit))
  }
}
