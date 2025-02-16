package model.playingFiledComponent.strategy.refillStrategy
import model.playerComponent.Player
import model.playingFiledComponent.FieldState
import model.cardComponent.base.Card
import scala.collection.mutable

trait RefillStrategy {
  def refillDefenderField(fieldState: FieldState, defender: Player): Unit
  def refillField(fieldState: FieldState, player: Player, hand: mutable.Queue[Card]): Unit
}
