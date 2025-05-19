package de.htwg.se.soccercardclash.model.playerComponent.ai

import de.htwg.se.soccercardclash.model.cardComponent.ICard
import de.htwg.se.soccercardclash.model.gameComponent.context.GameContext
import de.htwg.se.soccercardclash.model.playerComponent.IPlayer
import de.htwg.se.soccercardclash.model.playerComponent.playerAction.PlayerActionPolicies
import de.htwg.se.soccercardclash.util.{AIAction, DoubleAttackAIAction, NoOpAIAction, SingleAttackAIAction}
import de.htwg.se.soccercardclash.model.gameComponent.state.manager.PlayerActionManager
import scala.util.Random

class SimpleAttackAIStrategy extends IAIStrategy {
  override def decideAction(ctx: GameContext, player: IPlayer): AIAction = {
    val state = ctx.state
    val dataManager = state.getDataManager
    val roles = state.getRoles
    val defender = if (roles.attacker == player) roles.defender else roles.attacker

    val attackerHand = dataManager.getPlayerHand(player)
    val defenderField = dataManager.getPlayerDefenders(defender)

    if (attackerHand.toList.isEmpty) return NoOpAIAction

    val defenderIndexOpt = defenderField.zipWithIndex.collectFirst {
      case (Some(_), idx) => idx
    }

    defenderIndexOpt match {
      case Some(index) => SingleAttackAIAction(defenderIndex = index)
      case None =>
        if (dataManager.getPlayerGoalkeeper(defender).isDefined)
          SingleAttackAIAction(defenderIndex = -1)
        else
          NoOpAIAction
    }
  }

}

class SmartAttackAIStrategy extends IAIStrategy {
  private val playerActionManager = PlayerActionManager()

  override def decideAction(ctx: GameContext, player: IPlayer): AIAction = {
    val state = ctx.state
    val dataManager = state.getDataManager
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

class RandomAttackAIStrategy(random: Random = new Random()) extends IAIStrategy {
  override def decideAction(ctx: GameContext, player: IPlayer): AIAction = {
    val state = ctx.state
    val dataManager = state.getDataManager
    val roles = state.getRoles

    val defender = if (roles.attacker == player) roles.defender else roles.attacker
    val attackerHand = dataManager.getPlayerHand(player)

    if (attackerHand.toList.isEmpty)
      return NoOpAIAction

    val defenderField = dataManager.getPlayerDefenders(defender)

    val availableDefenderIndices: List[Int] =
      defenderField.zipWithIndex.collect { case (Some(_), idx) => idx }

    if (availableDefenderIndices.nonEmpty) {
      val randomIndex = availableDefenderIndices(random.nextInt(availableDefenderIndices.size))
      SingleAttackAIAction(defenderIndex = randomIndex)
    } else if (dataManager.getPlayerGoalkeeper(defender).isDefined) {
      SingleAttackAIAction(defenderIndex = -1)
    } else {
      NoOpAIAction
    }
  }

}