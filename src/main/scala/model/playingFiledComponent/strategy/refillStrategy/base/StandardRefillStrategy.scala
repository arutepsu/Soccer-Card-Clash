package model.playingFiledComponent.strategy.refillStrategy.base

import model.cardComponent.ICard
import model.playerComponent.IPlayer
import model.playingFiledComponent.manager.base.DataManager
import model.playingFiledComponent.strategy.refillStrategy.IRefillStrategy

import scala.collection.mutable

class StandardRefillStrategy extends IRefillStrategy {

  private val defenderFieldRefill = new RefillDefenderField()
  private val fieldRefill = new RefillField()

  override def refillDefenderField(fieldState: DataManager, defender: IPlayer): Unit = {
    defenderFieldRefill.refill(fieldState, defender)
  }

  override def refillField(fieldState: DataManager, player: IPlayer, hand: mutable.Queue[ICard]): Unit = {
    fieldRefill.refill(fieldState, player, hand)
  }
}