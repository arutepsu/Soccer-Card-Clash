package de.htwg.se.soccercardclash.model.playerComponent.ai.types
import de.htwg.se.soccercardclash.model.gameComponent.state.manager.PlayerActionManager
import de.htwg.se.soccercardclash.util.NoOpAIAction

import scala.util.Random
import de.htwg.se.soccercardclash.model.gameComponent.context.GameContext
import de.htwg.se.soccercardclash.model.playerComponent.IPlayer
import de.htwg.se.soccercardclash.model.playerComponent.ai.strategies.{IAIStrategy, SmartAggressiveAttackAIStrategy}
import de.htwg.se.soccercardclash.model.playerComponent.playerAction.PlayerActionPolicies
import de.htwg.se.soccercardclash.util.AIAction
import de.htwg.se.soccercardclash.model.playerComponent.ai.strategies.{SimpleSwapAIStrategy, SmartBoostWeakestDefenderAIStrategy}

class BitstormStrategy(random: Random) extends IAIStrategy {
  private val strategies: Vector[IAIStrategy] = Vector(
    new SmartAggressiveAttackAIStrategy(),
    new SmartBoostWeakestDefenderAIStrategy(),
    new SimpleSwapAIStrategy(random)
  )

  override def decideAction(ctx: GameContext, player: IPlayer): AIAction = {
    val eligible = strategies
    eligible(random.nextInt(eligible.size)).decideAction(ctx, player)
  }
}
