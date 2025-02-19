package model.playerComponent.playerAction

import model.playerComponent.playerAction.{PlayerActionPolicies, PlayerActionState}
import model.playerComponent.IPlayer

case class OutOfActions() extends PlayerActionState:
  override def performAction(player: IPlayer, action: PlayerActionPolicies): IPlayer =
    println(s"${player.name} has no more ${action.toString} left!")
    player