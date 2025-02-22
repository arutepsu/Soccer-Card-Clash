package model.playingFiledComponent.strategy.refillStrategy
import model.cardComponent.ICard
import model.playerComponent.IPlayer
import model.playingFiledComponent.manager.IDataManager

import scala.collection.mutable

trait IRefillStrategy {
  def refillDefenderField(fieldState: IDataManager, defender: IPlayer): Unit
  def refillField(fieldState: IDataManager, player: IPlayer, hand: mutable.Queue[ICard]): Unit
}