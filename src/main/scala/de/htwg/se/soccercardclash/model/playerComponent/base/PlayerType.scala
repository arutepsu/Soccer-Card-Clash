package de.htwg.se.soccercardclash.model.playerComponent.base
import de.htwg.se.soccercardclash.model.playerComponent.strategy.IPlayerStrategy

sealed trait PlayerType
case object Human extends PlayerType
case class AI(strategy: IPlayerStrategy) extends PlayerType