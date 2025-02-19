package model.playingFiledComponent.strategy.refillStrategy
import model.cardComponent.ICard
import model.playerComponent.IPlayer
import model.playingFiledComponent.FieldState

import scala.collection.mutable

trait RefillStrategy {
  def refillDefenderField(fieldState: FieldState, defender: IPlayer): Unit
  def refillField(fieldState: FieldState, player: IPlayer, hand: mutable.Queue[ICard]): Unit
}
