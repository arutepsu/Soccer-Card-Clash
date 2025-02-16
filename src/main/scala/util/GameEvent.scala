package util

import model.playerComponent.Player

trait GameEvent
case class AttackEvent(attacker: Player, defender: Player) extends GameEvent
case class SwapEvent(player: Player) extends GameEvent
case class BoostEvent(player: Player) extends GameEvent
