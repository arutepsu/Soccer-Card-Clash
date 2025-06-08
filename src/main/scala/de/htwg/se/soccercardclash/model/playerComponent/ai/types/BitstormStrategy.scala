package de.htwg.se.soccercardclash.model.playerComponent.ai.types

import de.htwg.se.soccercardclash.model.gameComponent.action.manager.PlayerActionManager
import de.htwg.se.soccercardclash.model.gameComponent.context.GameContext
import de.htwg.se.soccercardclash.model.playerComponent.IPlayer
import de.htwg.se.soccercardclash.model.playerComponent.ai.strategies.{IAIStrategy, SimpleSwapAIStrategy, SmartAggressiveAttackAIStrategy, SmartBoostWeakestDefenderAIStrategy}
import de.htwg.se.soccercardclash.model.playerComponent.playerAction.PlayerActionPolicies
import de.htwg.se.soccercardclash.model.playerComponent.util.IRandomProvider
import de.htwg.se.soccercardclash.util.{AIAction, NoOpAIAction}

class BitstormStrategy(val random: IRandomProvider) extends IAIStrategy {
  protected val strategies: Vector[IAIStrategy] = Vector(
    new SmartAggressiveAttackAIStrategy(),
    new SmartBoostWeakestDefenderAIStrategy(),
    new SimpleSwapAIStrategy(random)
  )

  override def decideAction(ctx: GameContext, player: IPlayer): AIAction = {
    val eligible = strategies
    eligible(random.nextInt(eligible.size)).decideAction(ctx, player)
  }
}
