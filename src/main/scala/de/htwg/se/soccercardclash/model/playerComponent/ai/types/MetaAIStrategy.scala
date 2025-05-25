package de.htwg.se.soccercardclash.model.playerComponent.ai.types

import de.htwg.se.soccercardclash.model.gameComponent.context.GameContext
import de.htwg.se.soccercardclash.model.gameComponent.action.manager.PlayerActionManager
import de.htwg.se.soccercardclash.model.playerComponent.IPlayer
import de.htwg.se.soccercardclash.model.playerComponent.ai.strategies.SmartBoostWeakestDefenderAIStrategy
import de.htwg.se.soccercardclash.model.playerComponent.ai.strategies.IAIStrategy
import de.htwg.se.soccercardclash.model.playerComponent.playerAction.PlayerActionPolicies
import de.htwg.se.soccercardclash.util.{AIAction, NoOpAIAction}
import de.htwg.se.soccercardclash.model.playerComponent.ai.strategies.*
import scala.util.Random


class MetaAIStrategy(random: Random) extends IAIStrategy {
  private val strategies: Vector[IAIStrategy] = Vector(
    new SmartAttackAIStrategy(),
    new SimpleAttackAIStrategy(),
    new RandomAttackAIStrategy(random),
    new SmartBoostWeakestDefenderAIStrategy(),
    new SimpleSwapAIStrategy(random),
    new SmartAggressiveAttackAIStrategy()
  )

  override def decideAction(ctx: GameContext, player: IPlayer): AIAction = {
    val state = ctx.state
    val actionManager = PlayerActionManager()

    val eligibleStrategies = strategies.filter {
      case _: SmartBoostWeakestDefenderAIStrategy =>
        actionManager.canPerform(player, PlayerActionPolicies.Boost)

      case _: SimpleSwapAIStrategy =>
        actionManager.canPerform(player, PlayerActionPolicies.Swap)

      case _ => true
    }

    if (eligibleStrategies.isEmpty)
      return NoOpAIAction

    val weightedStrategies = eligibleStrategies.flatMap {
      case s: SmartBoostWeakestDefenderAIStrategy => Seq(s)
      case s: SimpleSwapAIStrategy => Seq(s)
      case s => Seq(s, s)
    }

    val chosenStrategy = weightedStrategies(random.nextInt(weightedStrategies.length))
    chosenStrategy.decideAction(ctx, player)
  }

}
