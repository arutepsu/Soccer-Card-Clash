package de.htwg.se.soccercardclash.model.gameComponent.state.strategy.boostStrategy

import de.htwg.se.soccercardclash.model.gameComponent.state.IGameState
import de.htwg.se.soccercardclash.util.ObservableEvent

trait IBoostStrategy {
  def boost(playingField: IGameState): (Boolean, IGameState, List[ObservableEvent])
}
