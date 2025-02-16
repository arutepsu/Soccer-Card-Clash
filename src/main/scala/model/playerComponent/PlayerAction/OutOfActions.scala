package model.playerComponent.PlayerAction

import model.playerComponent.PlayerAction.{PlayerAction, PlayerActionState}
import model.playerComponent.Player

case class OutOfActions() extends PlayerActionState:
  override def performAction(player: Player, action: PlayerAction): Player =
    println(s"${player.name} has no more ${action.toString} left!")
    player