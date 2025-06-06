package de.htwg.se.soccercardclash.model.playerComponent.factory

import de.htwg.se.soccercardclash.model.playerComponent.ai.strategies.IAIStrategy
import de.htwg.se.soccercardclash.model.playerComponent.base.{AI, Human, Player, PlayerType}
import de.htwg.se.soccercardclash.model.playerComponent.playerAction.{CanPerformAction, OutOfActions, PlayerActionPolicies, PlayerActionState}

class PlayerBuilder {
  private var name: String = "Unnamed"
  private var playerType: PlayerType = Human
  private var limits: Map[PlayerActionPolicies, Int] = Map.empty
  private var directStates: Option[Map[PlayerActionPolicies, PlayerActionState]] = None

  def withName(n: String): PlayerBuilder = {
    name = n
    this
  }

  def asHuman(): PlayerBuilder = {
    playerType = Human
    this
  }

  def asAI(strategy: IAIStrategy): PlayerBuilder = {
    playerType = AI(strategy)
    this
  }

  def withPolicy(policy: PlayerActionPolicies, maxUses: Int): PlayerBuilder = {
    limits = limits.updated(policy, maxUses)
    this
  }

  def withActionStates(states: Map[PlayerActionPolicies, PlayerActionState]): PlayerBuilder = {
    directStates = Some(states)
    this
  }


  def withDefaultLimits(): PlayerBuilder = {
    limits = PlayerActionPolicies.values.map(p => p -> p.maxUses).toMap
    this
  }

  def withConvertedLimitsFromStates(states: Map[PlayerActionPolicies, PlayerActionState]): PlayerBuilder = {
    this.limits = states.map {
      case (policy, CanPerformAction(uses)) => policy -> uses
      case (policy, OutOfActions) => policy -> 0
    }
    this
  }

  def build(): Player = {
    val actionStates = directStates.getOrElse {
      PlayerActionPolicies.values.map { policy =>
        val uses = limits.getOrElse(policy, policy.maxUses)
        policy -> (if (uses > 0) CanPerformAction(uses) else OutOfActions)
      }.toMap
    }

    Player(name, actionStates, playerType)
  }
}

object PlayerBuilder {
  def apply(): PlayerBuilder = new PlayerBuilder()
}
