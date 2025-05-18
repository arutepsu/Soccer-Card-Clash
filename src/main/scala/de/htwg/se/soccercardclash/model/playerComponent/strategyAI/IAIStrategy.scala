package de.htwg.se.soccercardclash.model.playerComponent.strategyAI

import de.htwg.se.soccercardclash.model.gameComponent.context.GameContext
import de.htwg.se.soccercardclash.model.gameComponent.state.IGameState
import de.htwg.se.soccercardclash.model.playerComponent.IPlayer
import de.htwg.se.soccercardclash.model.playerComponent.base.PlayerType
import de.htwg.se.soccercardclash.util.*
import de.htwg.se.soccercardclash.model.cardComponent.base.components.Value
import de.htwg.se.soccercardclash.model.gameComponent.state.manager.{ActionManager, PlayerActionManager}
import de.htwg.se.soccercardclash.model.playerComponent.playerAction.PlayerActionPolicies

import scala.util.Random

trait IAIStrategy {
  def decideAction(gameState: GameContext, player: IPlayer): aIAction
}

class SimpleAIStrategy extends IAIStrategy {
  override def decideAction(ctx: GameContext, player: IPlayer): aIAction = {
    val state = ctx.state
    val dataManager = state.getDataManager
    val roles = state.getRoles
    val defender = if (roles.attacker == player) roles.defender else roles.attacker

    val attackerHand = dataManager.getPlayerHand(player)
    val defenderField = dataManager.getPlayerDefenders(defender)

    if (attackerHand.toList.isEmpty)
      return NoOpAIAction

    if (!dataManager.allDefendersBeaten(defender))
      SingleAttackAIAction(defenderIndex = 0)
    else if (dataManager.getPlayerGoalkeeper(defender).isDefined)
      SingleAttackAIAction(defenderIndex = -1)
    else
      NoOpAIAction
  }
}

class SmartAIStrategy extends IAIStrategy {
  private val playerActionManager = PlayerActionManager()

  override def decideAction(ctx: GameContext, player: IPlayer): aIAction = {
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
    val defendersIndexed = defenderField.zipWithIndex

    val beatable = defendersIndexed.filter { case (defCard, _) =>
      defCard.value < attackValue
    }

    if (beatable.nonEmpty) {
      val chosenDefenderIndex = beatable.minBy(_._1.value)._2
      SingleAttackAIAction(defenderIndex = chosenDefenderIndex)

    } else {
      val fallbackIndexOpt =
        if (defenderField.nonEmpty)
          Some(defendersIndexed.maxBy(_._1.value)._2)
        else if (dataManager.getPlayerGoalkeeper(defender).isDefined)
          Some(-1)
        else
          None

      fallbackIndexOpt match {
        case Some(fallbackIndex) if playerActionManager.canPerform(player, PlayerActionPolicies.DoubleAttack) &&
          attackerHand.size >= 2 =>
          DoubleAttackAIAction(defenderIndex = fallbackIndex)

        case Some(fallbackIndex) =>
          SingleAttackAIAction(defenderIndex = fallbackIndex)

        case None =>
          NoOpAIAction
      }
    }
  }
}


class RandomAIStrategy(random: Random = new Random()) extends IAIStrategy {
  override def decideAction(ctx: GameContext, player: IPlayer): aIAction = {
    val state = ctx.state
    val dataManager = state.getDataManager
    val roles = state.getRoles

    val defender = if (roles.attacker == player) roles.defender else roles.attacker
    val attackerHand = dataManager.getPlayerHand(player)

    if (attackerHand.toList.isEmpty)
      return NoOpAIAction

    if (!dataManager.allDefendersBeaten(defender)) {
      val defenderField = dataManager.getPlayerDefenders(defender)
      val randomIndex = random.nextInt(defenderField.size)
      SingleAttackAIAction(defenderIndex = randomIndex)
    } else if (dataManager.getPlayerGoalkeeper(defender).isDefined) {
      SingleAttackAIAction(defenderIndex = -1)
    } else {
      NoOpAIAction
    }
  }
}


class MetaAIStrategy(random: Random) extends IAIStrategy {
  // Define the available strategies
  private val strategies: Vector[IAIStrategy] = Vector(
    new SmartAIStrategy(),
    new SimpleAIStrategy(),
    new RandomAIStrategy(random),
  )

  override def decideAction(ctx: GameContext, player: IPlayer): aIAction = {
    val chosenStrategy = strategies(random.nextInt(strategies.length))
    println(f"!!!!!!!chosen: ${chosenStrategy.getClass.toString}")
    chosenStrategy.decideAction(ctx, player)
  }
}
