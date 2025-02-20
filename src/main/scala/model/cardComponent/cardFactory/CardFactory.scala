package model.cardComponent.cardFactory

import model.cardComponent.base.Suit.Suit
import model.cardComponent.base.Value.Value
import model.cardComponent.base.{BoostedCard, RegularCard, Suit, Value}
import model.cardComponent.ICard
import scala.util.Random
object CardFactory {
  def createCard(value: Value, suit: Suit): ICard =
    RegularCard(value, suit)
  
//  def createCardFromString(valueStr: String, suitStr: String): Option[ICard] = {
//    for {
//      value <- Value.fromString(valueStr)
//      suit  <- Suit.fromString(suitStr)
//    } yield ICard(value, suit)
//  }
  
//  def createCardFromInt(valueInt: Int, suit: Suit): Option[ICard] = {
//    Value.fromInt(valueInt).map(v => Card(v, suit))
//  }
}
