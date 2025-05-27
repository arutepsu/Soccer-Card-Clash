package de.htwg.se.soccercardclash.model.playerComponent.ai.strategies

import de.htwg.se.soccercardclash.model.cardComponent.ICard
import de.htwg.se.soccercardclash.model.cardComponent.base.components.Value
import de.htwg.se.soccercardclash.model.gameComponent.context.GameContext
import de.htwg.se.soccercardclash.model.playerComponent.IPlayer
import de.htwg.se.soccercardclash.model.playerComponent.playerAction.PlayerActionPolicies
import de.htwg.se.soccercardclash.util.{AIAction, DoubleAttackAIAction, NoOpAIAction, RegularSwapAIAction, SingleAttackAIAction}
import de.htwg.se.soccercardclash.model.gameComponent.action.manager.PlayerActionManager
import de.htwg.se.soccercardclash.model.playerComponent.util.IRandomProvider

import scala.util.Random

class SimpleSwapAIStrategy(random: IRandomProvider) extends IAIStrategy {
  private val playerActionManager = PlayerActionManager()

  override def decideAction(ctx: GameContext, player: IPlayer): AIAction = {
    val state = ctx.state
    val dataManager = state.getGameCards

    if (!playerActionManager.canPerform(player, PlayerActionPolicies.Swap))
      return NoOpAIAction

    val hand = dataManager.getPlayerHand(player).toList
    if (hand.size < 2) return NoOpAIAction

    val firstCard = hand.head
    if (firstCard.value >= Value.Six) return NoOpAIAction

    val targetIndex = random.between(0, hand.size - 1)
    RegularSwapAIAction(index = targetIndex)
  }
}