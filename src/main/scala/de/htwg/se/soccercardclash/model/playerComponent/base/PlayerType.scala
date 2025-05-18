package de.htwg.se.soccercardclash.model.playerComponent.base
import de.htwg.se.soccercardclash.model.playerComponent.strategyAI.IAIStrategy

sealed trait PlayerType
case object Human extends PlayerType
case class AI(strategy: IAIStrategy) extends PlayerType