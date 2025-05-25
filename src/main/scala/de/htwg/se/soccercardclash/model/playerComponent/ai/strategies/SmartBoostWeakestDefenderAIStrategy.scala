package de.htwg.se.soccercardclash.model.playerComponent.ai.strategies

import de.htwg.se.soccercardclash.model.cardComponent.ICard
import de.htwg.se.soccercardclash.model.cardComponent.base.components.Value
import de.htwg.se.soccercardclash.model.cardComponent.base.types.BoostedCard
import de.htwg.se.soccercardclash.model.gameComponent.context.GameContext
import de.htwg.se.soccercardclash.model.gameComponent.action.manager.PlayerActionManager
import de.htwg.se.soccercardclash.model.playerComponent.IPlayer
import de.htwg.se.soccercardclash.model.playerComponent.ai.strategies.IAIStrategy
import de.htwg.se.soccercardclash.model.playerComponent.playerAction.PlayerActionPolicies
import de.htwg.se.soccercardclash.util.*

import scala.util.Random

class SmartBoostWeakestDefenderAIStrategy() extends IAIStrategy {
  private val playerActionManager = PlayerActionManager()

  override def decideAction(ctx: GameContext, player: IPlayer): AIAction = {
    val state = ctx.state
    val dataManager = state.getGameCards

    if (!playerActionManager.canPerform(player, PlayerActionPolicies.Boost))
      return NoOpAIAction

    val defenders = dataManager.getPlayerDefenders(player)

    val unboostedDefenders: Seq[(Int, ICard)] = defenders.zipWithIndex.collect {
      case (Some(card), idx) if !card.isInstanceOf[BoostedCard] => (idx, card)
    }

    val unboostedGoalkeeper: Option[(Int, ICard)] =
      dataManager.getPlayerGoalkeeper(player) match {
        case Some(card) if !card.isInstanceOf[BoostedCard] => Some((-1, card))
        case _ => None
      }

    val allUnboosted: Seq[(Int, ICard)] = unboostedDefenders ++ unboostedGoalkeeper

    allUnboosted.minByOption(_._2.value) match {
      case Some((index, _)) =>
        val zone = if (index == -1) GoalkeeperZone else DefenderZone
        BoostAIAction(cardIndex = index, zone = zone)

      case None =>
        NoOpAIAction
    }
  }
}