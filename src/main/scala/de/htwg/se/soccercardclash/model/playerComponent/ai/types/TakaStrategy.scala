package de.htwg.se.soccercardclash.model.playerComponent.ai.types
import de.htwg.se.soccercardclash.model.gameComponent.action.manager.PlayerActionManager
import de.htwg.se.soccercardclash.model.gameComponent.context.GameContext
import de.htwg.se.soccercardclash.model.playerComponent.IPlayer
import de.htwg.se.soccercardclash.model.playerComponent.ai.strategies.{IAIStrategy, SimpleSwapAIStrategy, SmartAttackAIStrategy, SmartBoostWeakestDefenderAIStrategy}
import de.htwg.se.soccercardclash.model.playerComponent.playerAction.PlayerActionPolicies
import de.htwg.se.soccercardclash.model.playerComponent.util.IRandomProvider
import de.htwg.se.soccercardclash.util.{AIAction, NoOpAIAction}

import scala.util.Random


class TakaStrategy(val random: IRandomProvider) extends IAIStrategy {
  protected val strategies: Vector[IAIStrategy] = Vector(
    new SmartAttackAIStrategy(),
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
    else eligible(random.nextInt(eligible.size)).decideAction(ctx, player)
  }
}
