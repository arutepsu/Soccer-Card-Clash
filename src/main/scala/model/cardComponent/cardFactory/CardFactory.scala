package model.cardComponent.cardFactory

import model.cardComponent.base.Suit.Suit
import model.cardComponent.base.Value.Value
import model.cardComponent.base.{BoostedCard, RegularCard, Suit, Value}
import model.cardComponent.base.Card
import scala.util.Random
object CardFactory {
  def createCard(value: Value, suit: Suit): Card =
    Card(value, suit)
  
  def createCardFromString(valueStr: String, suitStr: String): Option[Card] = {
    for {
      value <- Value.fromString(valueStr)
      suit  <- Suit.fromString(suitStr)
    } yield Card(value, suit)
  }
  
  def createCardFromInt(valueInt: Int, suit: Suit): Option[Card] = {
    Value.fromInt(valueInt).map(v => Card(v, suit))
  }
}
