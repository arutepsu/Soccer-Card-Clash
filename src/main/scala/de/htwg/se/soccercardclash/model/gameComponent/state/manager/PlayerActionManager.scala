package de.htwg.se.soccercardclash.model.gameComponent.state.manager

import de.htwg.se.soccercardclash.model.playerComponent.IPlayer
import de.htwg.se.soccercardclash.model.playerComponent.playerAction.{CanPerformAction, PlayerActionPolicies}

class PlayerActionManager extends IPlayerActionManager {

  override def performAction(player: IPlayer, action: PlayerActionPolicies): IPlayer = {
    player.getActionStates.get(action) match {
      case Some(state) => state.performAction(player, action)
      case None => player
    }
  }

  override def canPerform(player: IPlayer, action: PlayerActionPolicies): Boolean = {
    player.getActionStates.get(action) match {
      case Some(CanPerformAction(remaining)) if remaining > 0 => true
      case _ => false
    }
  }

  override def resetAllActions(player: IPlayer): IPlayer = {
    val resetStates = PlayerActionPolicies.values.map { policy =>
      policy -> CanPerformAction(policy.maxUses)
    }.toMap
    player.setActionStates(resetStates)
  }
}

trait IPlayerActionManager {

  def performAction(player: IPlayer, action: PlayerActionPolicies): IPlayer

  def canPerform(player: IPlayer, action: PlayerActionPolicies): Boolean

  def resetAllActions(player: IPlayer): IPlayer
}
