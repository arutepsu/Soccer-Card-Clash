package de.htwg.se.soccercardclash.model.cardComponent.factory

import com.google.inject.{Inject, Singleton}
import de.htwg.se.soccercardclash.model.cardComponent.ICard
import de.htwg.se.soccercardclash.model.cardComponent.base.components.Suit.Suit
import de.htwg.se.soccercardclash.model.cardComponent.base.components.Value.Value
import de.htwg.se.soccercardclash.model.cardComponent.base.components.{Suit, Value}
import de.htwg.se.soccercardclash.model.cardComponent.base.types.{BoostedCard, RegularCard}

import scala.util.Random


@Singleton
class CardFactory @Inject()() extends ICardFactory {
  override def createCard(value: Value, suit: Suit): ICard = {
    new RegularCard(value, suit)
  }
}

trait ICardFactory {
  def createCard(value: Value, suit: Suit): ICard
}