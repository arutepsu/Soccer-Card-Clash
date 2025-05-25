package de.htwg.se.soccercardclash.model.playerComponent.ai.types
import de.htwg.se.soccercardclash.model.gameComponent.action.manager.PlayerActionManager
import de.htwg.se.soccercardclash.util.NoOpAIAction

import scala.util.Random
import de.htwg.se.soccercardclash.model.gameComponent.context.GameContext
import de.htwg.se.soccercardclash.model.playerComponent.IPlayer
import de.htwg.se.soccercardclash.model.playerComponent.ai.strategies.*
import de.htwg.se.soccercardclash.model.playerComponent.playerAction.PlayerActionPolicies
import de.htwg.se.soccercardclash.util.AIAction

class DefendraStrategy(random: Random) extends IAIStrategy {
  private val strategies: Vector[IAIStrategy] = Vector(
    new SimpleAttackAIStrategy,
    new RandomAttackAIStrategy(random),
    new SmartBoostWeakestDefenderAIStrategy(),
    new SimpleSwapAIStrategy(random)
  )
  override def decideAction(ctx: GameContext, player: IPlayer): AIAction = {
    val actionManager = PlayerActionManager()
    val eligible = strategies.filter {
      case _: SmartBoostWeakestDefenderAIStrategy =>
        actionManager.canPerform(player, PlayerActionPolicies.Boost)
      case _: SimpleSwapAIStrategy =>
        actionManager.canPerform(player, PlayerActionPolicies.Swap)
      case _ => true
    }

    if (eligible.isEmpty) NoOpAIAction
    else {
      val maybeOverweight = eligible.flatMap {
        case s: RandomAttackAIStrategy => Seq(s) 
        case s => Seq(s, s)
      }
      maybeOverweight(random.nextInt(maybeOverweight.length)).decideAction(ctx, player)
    }
  }
}
