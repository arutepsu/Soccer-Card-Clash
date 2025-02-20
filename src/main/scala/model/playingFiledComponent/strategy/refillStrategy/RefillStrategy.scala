package model.playingFiledComponent.strategy.refillStrategy
import model.cardComponent.ICard
import model.playerComponent.IPlayer
import model.playingFiledComponent.manager.DataManager

import scala.collection.mutable

trait RefillStrategy {
  def refillDefenderField(fieldState: DataManager, defender: IPlayer): Unit
  def refillField(fieldState: DataManager, player: IPlayer, hand: mutable.Queue[ICard]): Unit
}