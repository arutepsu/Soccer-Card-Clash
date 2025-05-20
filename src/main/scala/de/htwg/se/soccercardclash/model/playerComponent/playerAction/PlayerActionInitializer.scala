package de.htwg.se.soccercardclash.model.playerComponent.playerAction

object PlayerActionInitializer {

  def withDefaults(): Map[PlayerActionPolicies, PlayerActionState] =
    PlayerActionPolicies.values.map { policy =>
      policy -> CanPerformAction(policy.maxUses)
    }.toMap

  def withCustomLimits(overrides: Map[PlayerActionPolicies, Int]): Map[PlayerActionPolicies, PlayerActionState] =
    PlayerActionPolicies.values.map { policy =>
      val uses = overrides.getOrElse(policy, policy.maxUses)
      policy -> (if (uses > 0) CanPerformAction(uses) else OutOfActions)
    }.toMap
}
