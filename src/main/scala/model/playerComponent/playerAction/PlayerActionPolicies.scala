package model.playerComponent.playerAction

enum PlayerActionPolicies(val maxUses: Int):
  case Boost extends PlayerActionPolicies(5)
  case DoubleAttack extends PlayerActionPolicies(4)
  case Swap extends PlayerActionPolicies(3)
