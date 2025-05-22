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

    val attackCard = attackerHand.head
    val attackValue = attackCard.value

    val defenderField = dataManager.getPlayerDefenders(defender)

    val defendersIndexed: List[(ICard, Int)] = defenderField.zipWithIndex.collect {
      case (Some(card), idx) => (card, idx)
    }

    val beatable = defendersIndexed.filter { case (defCard, _) =>
      defCard.value < attackValue
    }

    if (beatable.nonEmpty) {
      val chosenDefenderIndex = beatable.minBy(_._1.value)._2
      SingleAttackAIAction(defenderIndex = chosenDefenderIndex)

    } else {
      val fallbackIndexOpt =
        defendersIndexed match {
          case Nil =>
            if (dataManager.getPlayerGoalkeeper(defender).isDefined) Some(-1) else None
          case nonEmpty => Some(nonEmpty.maxBy(_._1.value)._2)
        }

      fallbackIndexOpt match {
        case Some(index) if playerActionManager.canPerform(player, PlayerActionPolicies.DoubleAttack) && attackerHand.size >= 2 =>
          DoubleAttackAIAction(defenderIndex = index)

        case Some(index) =>
          SingleAttackAIAction(defenderIndex = index)

        case None =>
          NoOpAIAction
      }
    }
  }
}