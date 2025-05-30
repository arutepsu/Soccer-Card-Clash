package de.htwg.se.soccercardclash.model.gameComponent.action.strategy.trigger

import de.htwg.se.soccercardclash.model.gameComponent.IGameState
import de.htwg.se.soccercardclash.util.ObservableEvent

trait IActionStrategy {
  def execute(playingField: IGameState): (Boolean, IGameState, List[ObservableEvent])
}
