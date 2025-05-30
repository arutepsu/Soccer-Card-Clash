package de.htwg.se.soccercardclash.model.gameComponent.action.strategy.flow.refillStrategy.base

import com.google.inject.Inject
import de.htwg.se.soccercardclash.model.cardComponent.ICard
import de.htwg.se.soccercardclash.model.cardComponent.dataStructure.IHandCardsQueue
import de.htwg.se.soccercardclash.model.gameComponent.action.strategy.flow.refillStrategy.IRefillStrategy
import de.htwg.se.soccercardclash.model.gameComponent.components.IGameCards
import de.htwg.se.soccercardclash.model.playerComponent.IPlayer

import scala.collection.mutable

class StandardRefillStrategy @Inject()(
                              defenderRefill: IDefenderFieldRefillStrategy,
                              fieldRefill: IFieldRefillStrategy
                            ) extends IRefillStrategy {

  override def refillDefenderField(gameCards: IGameCards, defender: IPlayer): IGameCards = {
    defenderRefill.refill(gameCards, defender)
  }

  override def refillField(gameCards: IGameCards, player: IPlayer, hand: IHandCardsQueue): IGameCards = {
    fieldRefill.refill(gameCards, player, hand)
  }
}
