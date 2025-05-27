package de.htwg.se.soccercardclash.model.playerComponent.ai.strategies

import de.htwg.se.soccercardclash.model.cardComponent.ICard
import de.htwg.se.soccercardclash.model.gameComponent.context.GameContext
import de.htwg.se.soccercardclash.model.playerComponent.IPlayer
import de.htwg.se.soccercardclash.model.playerComponent.playerAction.PlayerActionPolicies
import de.htwg.se.soccercardclash.util.{AIAction, DoubleAttackAIAction, NoOpAIAction, SingleAttackAIAction}
import de.htwg.se.soccercardclash.model.gameComponent.action.manager.PlayerActionManager
import de.htwg.se.soccercardclash.model.playerComponent.util.IRandomProvider

import scala.util.Random


class RandomAttackAIStrategy(random: IRandomProvider) extends IAIStrategy {
  override def decideAction(ctx: GameContext, player: IPlayer): AIAction = {
    val state = ctx.state
    val dataManager = state.getGameCards
    val roles = state.getRoles

    val defender = if (roles.attacker == player) roles.defender else roles.attacker
    val attackerHand = dataManager.getPlayerHand(player)

    if (attackerHand.toList.isEmpty)
      return NoOpAIAction

    val defenderField = dataManager.getPlayerDefenders(defender)

    val availableDefenderIndices: List[Int] =
      defenderField.zipWithIndex.collect { case (Some(_), idx) => idx }

    if (availableDefenderIndices.nonEmpty) {
      val randomIndex = availableDefenderIndices(random.nextInt(availableDefenderIndices.size))
      SingleAttackAIAction(defenderIndex = randomIndex)
    } else if (dataManager.getPlayerGoalkeeper(defender).isDefined) {
      SingleAttackAIAction(defenderIndex = -1)
    } else {
      NoOpAIAction
    }
  }

}