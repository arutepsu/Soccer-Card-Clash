package de.htwg.se.soccercardclash.model.playerComponent.ai.types
import de.htwg.se.soccercardclash.model.gameComponent.state.manager.PlayerActionManager
import de.htwg.se.soccercardclash.util.NoOpAIAction
import scala.util.Random
import de.htwg.se.soccercardclash.model.gameComponent.context.GameContext
import de.htwg.se.soccercardclash.model.playerComponent.IPlayer
import de.htwg.se.soccercardclash.model.playerComponent.ai.strategies.IAIStrategy
import de.htwg.se.soccercardclash.model.playerComponent.playerAction.PlayerActionPolicies
import de.htwg.se.soccercardclash.util.AIAction
import de.htwg.se.soccercardclash.model.playerComponent.ai.strategies.{SimpleSwapAIStrategy, SmartAttackAIStrategy, SmartBoostWeakestDefenderAIStrategy}


class TakaStrategy(random: Random) extends IAIStrategy {
  private val strategies: Vector[IAIStrategy] = Vector(
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
