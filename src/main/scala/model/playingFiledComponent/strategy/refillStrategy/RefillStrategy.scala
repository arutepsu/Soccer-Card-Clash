package model.playingFiledComponent.strategy.refillStrategy
import model.cardComponent.ICard
import model.playerComponent.Player
import model.playingFiledComponent.FieldState

import scala.collection.mutable

trait RefillStrategy {
  def refillDefenderField(fieldState: FieldState, defender: Player): Unit
  def refillField(fieldState: FieldState, player: Player, hand: mutable.Queue[ICard]): Unit
}
