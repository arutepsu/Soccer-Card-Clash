package model.playerComponent.PlayerAction

/** ✅ Enum to represent all player actions */
enum PlayerAction(val maxUses: Int):
  case Boost extends PlayerAction(5)
  case DoubleAttack extends PlayerAction(4)
  case Swap extends PlayerAction(3)
