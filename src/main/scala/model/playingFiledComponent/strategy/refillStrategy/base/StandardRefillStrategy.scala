package model.playingFiledComponent.strategy.refillStrategy.base

import model.cardComponent.ICard
import model.playerComponent.IPlayer
import model.playingFiledComponent.manager.IDataManager
import model.playingFiledComponent.strategy.refillStrategy.IRefillStrategy

import scala.collection.mutable

class StandardRefillStrategy extends IRefillStrategy {

  private val defenderFieldRefill = new RefillDefenderField()
  private val fieldRefill = new RefillField()

  override def refillDefenderField(fieldState: IDataManager, defender: IPlayer): Unit = {
    println("refil!!!!!!!!!!!!!!!!!!!!!!!!!!!")
    defenderFieldRefill.refill(fieldState, defender)
  }

  override def refillField(fieldState: IDataManager, player: IPlayer, hand: mutable.Queue[ICard]): Unit = {
    fieldRefill.refill(fieldState, player, hand)
  }
}