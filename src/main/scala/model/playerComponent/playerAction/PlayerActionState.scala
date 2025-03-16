package model.playerComponent.playerAction

import model.playerComponent.IPlayer
import model.playerComponent.playerAction.PlayerActionPolicies

sealed trait PlayerActionState {
  def performAction(player: IPlayer, action: PlayerActionPolicies): IPlayer
}

enum PlayerActionPolicies(val maxUses: Int) {
  case Boost extends PlayerActionPolicies(2)
  case DoubleAttack extends PlayerActionPolicies(1)
  case Swap extends PlayerActionPolicies(1)
}

object PlayerActionPolicies {
  def fromString(s: String): Option[PlayerActionPolicies] =
    values.find(_.toString == s)
}

case class CanPerformAction(remainingUses: Int) extends PlayerActionState {
  override def performAction(player: IPlayer, action: PlayerActionPolicies): IPlayer =
    if (remainingUses > 1)
      player.updateActionState(action, CanPerformAction(remainingUses - 1))
    else
      player.updateActionState(action, OutOfActions)
}

case object OutOfActions extends PlayerActionState {
  override def performAction(player: IPlayer, action: PlayerActionPolicies): IPlayer = player
}

object PlayerActionState {
  def fromString(s: String): PlayerActionState = s match {
    case s"CanPerformAction($uses)" if uses.forall(_.isDigit) => CanPerformAction(uses.toInt)
    case "OutOfActions" => OutOfActions
    case _ => throw new IllegalArgumentException(s"Invalid action state: $s")
  }
}
