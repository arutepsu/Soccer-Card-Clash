package de.htwg.se.soccercardclash.model.gameComponent.state.strategy.refillStrategy.base

import de.htwg.se.soccercardclash.model.cardComponent.ICard
import de.htwg.se.soccercardclash.model.cardComponent.dataStructure.IHandCardsQueue
import de.htwg.se.soccercardclash.model.gameComponent.state.components.IDataManager
import de.htwg.se.soccercardclash.model.gameComponent.state.strategy.refillStrategy.IRefillStrategy
import de.htwg.se.soccercardclash.model.playerComponent.IPlayer

import scala.collection.mutable

class StandardRefillStrategy extends IRefillStrategy {

  protected val defenderFieldRefill = new RefillDefenderField()
  protected val fieldRefill = new RefillField()

  override def refillDefenderField(fieldState: IDataManager, defender: IPlayer): IDataManager = {
    defenderFieldRefill.refill(fieldState, defender)
  }

  override def refillField(fieldState: IDataManager, player: IPlayer, hand: IHandCardsQueue): IDataManager = {
    fieldRefill.refill(fieldState, player, hand)
  }
}
