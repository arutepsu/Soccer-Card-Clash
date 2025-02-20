package model.playerComponent.playerAction

import model.playerComponent.IPlayer
import model.playerComponent.playerAction.{PlayerActionPolicies, PlayerActionState}

case class OutOfActions() extends PlayerActionState:
  override def performAction(player: IPlayer, action: PlayerActionPolicies): IPlayer =
    player