package util

import model.playerComponent.IPlayer

trait GameEvent
case class AttackEvent(attacker: IPlayer, defender: IPlayer) extends GameEvent
case class SwapEvent(player: IPlayer) extends GameEvent
case class BoostEvent(player: IPlayer) extends GameEvent
