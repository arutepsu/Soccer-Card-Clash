package de.htwg.se.soccercardclash.model.playerComponent.ai.strategies

import de.htwg.se.soccercardclash.model.cardComponent.ICard
import de.htwg.se.soccercardclash.model.cardComponent.base.components.Value
import de.htwg.se.soccercardclash.model.cardComponent.base.types.BoostedCard
import de.htwg.se.soccercardclash.model.gameComponent.context.GameContext
import de.htwg.se.soccercardclash.model.gameComponent.action.manager.PlayerActionManager
import de.htwg.se.soccercardclash.model.playerComponent.IPlayer
import de.htwg.se.soccercardclash.model.playerComponent.ai.strategies.IAIStrategy
import de.htwg.se.soccercardclash.model.playerComponent.playerAction.PlayerActionPolicies
import de.htwg.se.soccercardclash.util.{AIAction, DoubleAttackAIAction, NoOpAIAction, SingleAttackAIAction}

import scala.util.Random
import de.htwg.se.soccercardclash.util.*

class SmartAggressiveAttackAIStrategy extends IAIStrategy {
  private val playerActionManager = PlayerActionManager()

  override def decideAction(ctx: GameContext, player: IPlayer): AIAction = {
    val state = ctx.state
    val dataManager = state.getGameCards
    val roles = state.getRoles

    if (roles.attacker != player) return NoOpAIAction

    val hand = dataManager.getPlayerHand(player).toList
    if (hand.isEmpty) return NoOpAIAction

    val defender = roles.defender
    val defenderField = dataManager.getPlayerDefenders(defender)

    val defendersIndexed = defenderField.zipWithIndex.collect {
      case (Some(card), idx) => (card, idx)
    }

    val canDouble = playerActionManager.canPerform(player, PlayerActionPolicies.DoubleAttack) && hand.size >= 2

    val lastCard = hand.last
    val lastTwo = hand.takeRight(2)
    val totalDoubleValue = lastTwo.map(c => Value.valueToInt(c.value)).sum

    // Try Double Attack first
    val beatableDouble = defendersIndexed.filter {
      case (defCard, _) => totalDoubleValue > Value.valueToInt(defCard.value)
    }

    if (canDouble && beatableDouble.nonEmpty) {
      val (_, targetIdx) = beatableDouble.maxBy(t => Value.valueToInt(t._1.value))
      return DoubleAttackAIAction(targetIdx)
    }

    // Try Single Attack
    val beatableSingle = defendersIndexed.filter {
      case (defCard, _) => Value.valueToInt(lastCard.value) > Value.valueToInt(defCard.value)
    }

    if (beatableSingle.nonEmpty) {
      val (_, targetIdx) = beatableSingle.maxBy(t => Value.valueToInt(t._1.value))
      return SingleAttackAIAction(targetIdx)
    }

    // Boost fallback
    if (playerActionManager.canPerform(player, PlayerActionPolicies.Boost)) {
      val gkOpt = dataManager.getPlayerGoalkeeper(defender)
      if (gkOpt.exists(c => Value.valueToInt(c.value) < 10 && !c.isInstanceOf[BoostedCard])) {
        return BoostAIAction(-1, GoalkeeperZone)
      }

      val weakDefOpt = defendersIndexed.find {
        case (c, _) => Value.valueToInt(c.value) < 6 && !c.isInstanceOf[BoostedCard]
      }

      weakDefOpt.foreach { case (_, idx) =>
        return BoostAIAction(idx, DefenderZone)
      }
    }

    // Fallback: attack strongest defender
    val fallbackIdx = defendersIndexed
      .sortBy(t => -Value.valueToInt(t._1.value))
      .headOption.map(_._2).getOrElse(-1)

    if (canDouble) DoubleAttackAIAction(fallbackIdx)
    else SingleAttackAIAction(fallbackIdx)
  }
}
