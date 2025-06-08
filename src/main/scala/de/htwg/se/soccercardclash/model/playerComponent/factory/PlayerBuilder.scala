package de.htwg.se.soccercardclash.model.playerComponent.factory

import de.htwg.se.soccercardclash.model.playerComponent.ai.strategies.IAIStrategy
import de.htwg.se.soccercardclash.model.playerComponent.base.{AI, Human, Player, PlayerType}
import de.htwg.se.soccercardclash.model.playerComponent.playerAction.{CanPerformAction, OutOfActions, PlayerActionPolicies, PlayerActionState}

case class PlayerBuilder(
                          name: String = "Unnamed",
                          playerType: PlayerType = Human,
                          limits: Map[PlayerActionPolicies, Int] = Map.empty,
                          directStates: Option[Map[PlayerActionPolicies, PlayerActionState]] = None
                        ) {

  def withName(n: String): PlayerBuilder =
    copy(name = n)

  def asHuman(): PlayerBuilder =
    copy(playerType = Human)

  def asAI(strategy: IAIStrategy): PlayerBuilder =
    copy(playerType = AI(strategy))

  def withPolicy(policy: PlayerActionPolicies, maxUses: Int): PlayerBuilder =
    copy(limits = limits.updated(policy, maxUses))

  def withActionStates(states: Map[PlayerActionPolicies, PlayerActionState]): PlayerBuilder =
    copy(directStates = Some(states))

  def withDefaultLimits(): PlayerBuilder =
    copy(limits = PlayerActionPolicies.values.map(p => p -> p.maxUses).toMap)

  def withConvertedLimitsFromStates(
                                     states: Map[PlayerActionPolicies, PlayerActionState]
                                   ): PlayerBuilder = {
    val convertedLimits = states.map {
      case (policy, CanPerformAction(uses)) => policy -> uses
      case (policy, OutOfActions)           => policy -> 0
    }
    copy(limits = convertedLimits)
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
