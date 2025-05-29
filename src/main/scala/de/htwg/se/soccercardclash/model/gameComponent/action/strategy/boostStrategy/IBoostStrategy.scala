package de.htwg.se.soccercardclash.model.gameComponent.action.strategy.boostStrategy

import de.htwg.se.soccercardclash.model.gameComponent.IGameState
import de.htwg.se.soccercardclash.util.ObservableEvent

trait IBoostStrategy {
  def boost(state: IGameState): (Boolean, IGameState, List[ObservableEvent])
}
