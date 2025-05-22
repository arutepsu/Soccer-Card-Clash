package de.htwg.se.soccercardclash.model.playerComponent.ai.strategies

import de.htwg.se.soccercardclash.model.cardComponent.ICard
import de.htwg.se.soccercardclash.model.gameComponent.context.GameContext
import de.htwg.se.soccercardclash.model.playerComponent.IPlayer
import de.htwg.se.soccercardclash.model.playerComponent.playerAction.PlayerActionPolicies
import de.htwg.se.soccercardclash.util.{AIAction, DoubleAttackAIAction, NoOpAIAction, SingleAttackAIAction}
import de.htwg.se.soccercardclash.model.gameComponent.state.manager.PlayerActionManager
import scala.util.Random

class SimpleAttackAIStrategy extends IAIStrategy {
  override def decideAction(ctx: GameContext, player: IPlayer): AIAction = {
    val state = ctx.state
    val dataManager = state.getGameCards
    val roles = state.getRoles
    val defender = if (roles.attacker == player) roles.defender else roles.attacker

    val attackerHand = dataManager.getPlayerHand(player)
    val defenderField = dataManager.getPlayerDefenders(defender)

    if (attackerHand.toList.isEmpty) return NoOpAIAction

    val defenderIndexOpt = defenderField.zipWithIndex.collectFirst {
      case (Some(_), idx) => idx
    }

    defenderIndexOpt match {
      case Some(index) => SingleAttackAIAction(defenderIndex = index)
      case None =>
        if (dataManager.getPlayerGoalkeeper(defender).isDefined)
          SingleAttackAIAction(defenderIndex = -1)
        else
          NoOpAIAction
    }
  }

}
