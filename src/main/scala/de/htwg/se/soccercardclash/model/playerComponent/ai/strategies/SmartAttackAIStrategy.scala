package de.htwg.se.soccercardclash.model.playerComponent.ai.strategies

import de.htwg.se.soccercardclash.model.cardComponent.ICard
import de.htwg.se.soccercardclash.model.gameComponent.context.GameContext
import de.htwg.se.soccercardclash.model.playerComponent.IPlayer
import de.htwg.se.soccercardclash.model.playerComponent.playerAction.PlayerActionPolicies
import de.htwg.se.soccercardclash.util.{AIAction, DoubleAttackAIAction, NoOpAIAction, SingleAttackAIAction}
import de.htwg.se.soccercardclash.model.gameComponent.state.manager.PlayerActionManager
import scala.util.Random

class SmartAttackAIStrategy extends IAIStrategy {
  private val playerActionManager = PlayerActionManager()

  override def decideAction(ctx: GameContext, player: IPlayer): AIAction = {
    val state = ctx.state
    val dataManager = state.getGameCards
    val roles = state.getRoles

    if (roles.attacker != player) return NoOpAIAction

    val defender = roles.defender
    val attackerHand = dataManager.getPlayerHand(player).toList
    if (attackerHand.isEmpty) return NoOpAIAction

    val attackCard = attackerHand.last
    val attackValue = attackCard.value

    val defendersIndexed: List[(ICard, Int)] = dataManager.getPlayerDefenders(defender)
      .zipWithIndex.collect { case (Some(card), idx) => (card, idx) }

    val beatableDefenders = defendersIndexed.filter { case (card, _) =>
      card.value < attackValue
    }

    if (beatableDefenders.nonEmpty) {
      val strongestBeatableIndex = beatableDefenders.maxBy(_._1.value)._2
      return SingleAttackAIAction(defenderIndex = strongestBeatableIndex)
    }

    val doubleAttackAvailable =
      playerActionManager.canPerform(player, PlayerActionPolicies.DoubleAttack) &&
        attackerHand.size >= 2

    if (defendersIndexed.nonEmpty && doubleAttackAvailable) {
      val strongestDefenderIndex = defendersIndexed.maxBy(_._1.value)._2
      return DoubleAttackAIAction(defenderIndex = strongestDefenderIndex)
    }

    dataManager.getPlayerGoalkeeper(defender) match {
      case Some(goalkeeper) if attackValue > goalkeeper.value =>
        SingleAttackAIAction(defenderIndex = -1)
      case Some(goalkeeper) if doubleAttackAvailable =>
        DoubleAttackAIAction(defenderIndex = -1)
      case _ =>
        NoOpAIAction
    }
  }
}
