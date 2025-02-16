package PlayerAction

import model.playerComponent.PlayerAction.{PlayerAction, PlayerActionState}
import model.playerComponent.Player

case class CanPerformAction(remainingUses: Int) extends PlayerActionState {
  override def performAction(player: Player, action: PlayerAction): Player = {
    if (remainingUses > 1) {
      println(s"${player.name} used ${action.toString}! Remaining: ${remainingUses - 1}")
      player.updateActionState(action, CanPerformAction(remainingUses - 1))
    } else {
      println(s"${player.name} used ${action.toString}! No more uses left!")
      player.updateActionState(action, OutOfActions())
    }
  }
}


