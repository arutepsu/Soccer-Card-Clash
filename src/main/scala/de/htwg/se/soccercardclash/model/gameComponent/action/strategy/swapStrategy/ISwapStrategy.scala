package de.htwg.se.soccercardclash.model.gameComponent.action.strategy.swapStrategy

import de.htwg.se.soccercardclash.model.gameComponent.state.IGameState
import de.htwg.se.soccercardclash.util.ObservableEvent

trait ISwapStrategy {
  def swap(state: IGameState): (Boolean, IGameState, List[ObservableEvent])
}
