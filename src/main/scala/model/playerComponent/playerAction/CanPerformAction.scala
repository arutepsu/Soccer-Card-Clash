package model.playerComponent.playerAction

import model.playerComponent.IPlayer
import model.playerComponent.playerAction.{PlayerActionPolicies, PlayerActionState}

case class CanPerformAction(remainingUses: Int) extends PlayerActionState:
  override def performAction(player: IPlayer, action: PlayerActionPolicies): IPlayer =
    if (remainingUses > 1) then
      player.updateActionState(action, CanPerformAction(remainingUses - 1))
    else
      player.updateActionState(action, OutOfActions())


