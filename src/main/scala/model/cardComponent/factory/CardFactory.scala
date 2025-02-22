package model.cardComponent.factory

import model.cardComponent.ICard
import model.cardComponent.base.components.Suit.Suit
import model.cardComponent.base.components.Value.Value
import scala.util.Random
import com.google.inject.{Inject, Singleton}
import model.cardComponent.base.components.{Suit, Value}
import model.cardComponent.base.types.{BoostedCard, RegularCard}

@Singleton
class CardFactory @Inject() () extends ICardFactory {
  override def createCard(value: Value, suit: Suit): ICard = {
    new RegularCard(value, suit)
  }
}
trait ICardFactory {
  def createCard(value: Value, suit: Suit): ICard
}