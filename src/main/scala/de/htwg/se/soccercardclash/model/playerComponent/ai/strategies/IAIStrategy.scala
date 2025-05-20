package de.htwg.se.soccercardclash.model.playerComponent.ai.strategies

import de.htwg.se.soccercardclash.model.gameComponent.context.GameContext
import de.htwg.se.soccercardclash.model.playerComponent.IPlayer
import de.htwg.se.soccercardclash.util.AIAction

trait IAIStrategy {
  def decideAction(gameState: GameContext, player: IPlayer): AIAction
}
