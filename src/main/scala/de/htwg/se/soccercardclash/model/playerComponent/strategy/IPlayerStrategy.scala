package de.htwg.se.soccercardclash.model.playerComponent.strategy

import de.htwg.se.soccercardclash.model.gameComponent.context.GameContext
import de.htwg.se.soccercardclash.model.gameComponent.state.IGameState
import de.htwg.se.soccercardclash.model.playerComponent.IPlayer
import de.htwg.se.soccercardclash.model.playerComponent.base.PlayerType
import de.htwg.se.soccercardclash.util.*
import de.htwg.se.soccercardclash.model.cardComponent.base.components.Value
import de.htwg.se.soccercardclash.model.gameComponent.state.manager.{ActionManager, PlayerActionManager}
import de.htwg.se.soccercardclash.model.playerComponent.playerAction.PlayerActionPolicies

import scala.util.Random

trait IPlayerStrategy {
  def decideAction(gameState: GameContext, player: IPlayer): PlayerAction
}

class SimpleAIStrategy extends IPlayerStrategy {
  override def decideAction(ctx: GameContext, player: IPlayer): PlayerAction = {
    val state = ctx.state
    val dataManager = state.getDataManager
    val roles = state.getRoles
    val defender = if (roles.attacker == player) roles.defender else roles.attacker

    val attackerHand = dataManager.getPlayerHand(player)
    val defenderField = dataManager.getPlayerDefenders(defender)

    if (attackerHand.toList.isEmpty)
      return NoOpAction

    if (!dataManager.allDefendersBeaten(defender))
      SingleAttackAction(defenderIndex = 0)
    else if (dataManager.getPlayerGoalkeeper(defender).isDefined)
      SingleAttackAction(defenderIndex = -1)
    else
      NoOpAction
  }
}

class SmartAIStrategy extends IPlayerStrategy {
  private val playerActionManager = PlayerActionManager()

  override def decideAction(ctx: GameContext, player: IPlayer): PlayerAction = {
    val state = ctx.state
    val dataManager = state.getDataManager
    val roles = state.getRoles

    if (roles.attacker != player) return NoOpAction

    val defender = roles.defender
    val attackerHand = dataManager.getPlayerHand(player).toList
    if (attackerHand.isEmpty) return NoOpAction

    val attackCard = attackerHand.head
    val attackValue = attackCard.value

    val defenderField = dataManager.getPlayerDefenders(defender)
    val defendersIndexed = defenderField.zipWithIndex

    val beatable = defendersIndexed.filter { case (defCard, _) =>
      defCard.value < attackValue
    }

    if (beatable.nonEmpty) {
      val chosenDefenderIndex = beatable.minBy(_._1.value)._2
      SingleAttackAction(defenderIndex = chosenDefenderIndex)

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
          DoubleAttackAction(defenderIndex = fallbackIndex)

        case Some(fallbackIndex) =>
          SingleAttackAction(defenderIndex = fallbackIndex)

        case None =>
          NoOpAction
      }
    }
  }
}


class RandomAIStrategy(random: Random = new Random()) extends IPlayerStrategy {
  override def decideAction(ctx: GameContext, player: IPlayer): PlayerAction = {
    val state = ctx.state
    val dataManager = state.getDataManager
    val roles = state.getRoles

    val defender = if (roles.attacker == player) roles.defender else roles.attacker
    val attackerHand = dataManager.getPlayerHand(player)

    if (attackerHand.toList.isEmpty)
      return NoOpAction

    if (!dataManager.allDefendersBeaten(defender)) {
      val defenderField = dataManager.getPlayerDefenders(defender)
      val randomIndex = random.nextInt(defenderField.size)
      SingleAttackAction(defenderIndex = randomIndex)
    } else if (dataManager.getPlayerGoalkeeper(defender).isDefined) {
      SingleAttackAction(defenderIndex = -1)
    } else {
      NoOpAction
    }
  }
}


class MetaAIStrategy(random: Random) extends IPlayerStrategy {
  // Define the available strategies
  private val strategies: Vector[IPlayerStrategy] = Vector(
    new SmartAIStrategy(),
    new SimpleAIStrategy(),
    new RandomAIStrategy(random),
  )

  override def decideAction(ctx: GameContext, player: IPlayer): PlayerAction = {
    val chosenStrategy = strategies(random.nextInt(strategies.length))
    println(f"!!!!!!!chosen: ${chosenStrategy.getClass.toString}")
    chosenStrategy.decideAction(ctx, player)
  }
}
