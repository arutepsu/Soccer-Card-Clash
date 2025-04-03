package de.htwg.se.soccercardclash.model.gameComponent.playingFiledComponent.strategy.refillStrategy.base

import de.htwg.se.soccercardclash.model.cardComponent.ICard
import de.htwg.se.soccercardclash.model.cardComponent.dataStructure.IHandCardsQueue
import de.htwg.se.soccercardclash.model.playerComponent.IPlayer
import de.htwg.se.soccercardclash.model.gameComponent.playingFiledComponent.manager.IDataManager
import de.htwg.se.soccercardclash.model.gameComponent.playingFiledComponent.strategy.refillStrategy.IRefillStrategy

import scala.collection.mutable

class StandardRefillStrategy extends IRefillStrategy {

  protected val defenderFieldRefill = new RefillDefenderField()
  protected val fieldRefill = new RefillField()

  override def refillDefenderField(fieldState: IDataManager, defender: IPlayer): Unit = {
    defenderFieldRefill.refill(fieldState, defender)
  }

  override def refillField(fieldState: IDataManager, player: IPlayer, hand: IHandCardsQueue): Unit = {
    fieldRefill.refill(fieldState, player, hand)
  }
}