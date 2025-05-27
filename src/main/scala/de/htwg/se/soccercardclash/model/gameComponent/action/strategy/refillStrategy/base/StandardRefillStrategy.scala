package de.htwg.se.soccercardclash.model.gameComponent.action.strategy.refillStrategy.base

import de.htwg.se.soccercardclash.model.cardComponent.ICard
import de.htwg.se.soccercardclash.model.cardComponent.dataStructure.IHandCardsQueue
import de.htwg.se.soccercardclash.model.gameComponent.action.strategy.refillStrategy.IRefillStrategy
import de.htwg.se.soccercardclash.model.gameComponent.state.components.IGameCards
import de.htwg.se.soccercardclash.model.playerComponent.IPlayer

import scala.collection.mutable

class StandardRefillStrategy extends IRefillStrategy {

  protected val defenderFieldRefill = new RefillDefenderField()
  protected val fieldRefill = new RefillField()

  override def refillDefenderField(gameCards: IGameCards, defender: IPlayer): IGameCards = {
    defenderFieldRefill.refill(gameCards, defender)
  }

  override def refillField(gameCards: IGameCards, player: IPlayer, hand: IHandCardsQueue): IGameCards = {
    fieldRefill.refill(gameCards, player, hand)
  }
}
