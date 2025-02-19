package model.playerComponent.playerAction

import model.playerComponent.playerAction.{PlayerActionPolicies, PlayerActionState}
import model.playerComponent.IPlayer

case class CanPerformAction(remainingUses: Int) extends PlayerActionState:
  override def performAction(player: IPlayer, action: PlayerActionPolicies): IPlayer =
    if (remainingUses > 1) then
      println(s"${player.name} used ${action.toString}! Remaining: ${remainingUses - 1}")
      player.updateActionState(action, CanPerformAction(remainingUses - 1))
    else
      println(s"${player.name} used ${action.toString}! No more uses left!")
      player.updateActionState(action, OutOfActions())


