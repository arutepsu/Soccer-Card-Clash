package de.htwg.se.soccercardclash.model.gameComponent.action.strategy.attackStrategy

import de.htwg.se.soccercardclash.model.gameComponent.IGameState
import de.htwg.se.soccercardclash.util.ObservableEvent

trait IAttackStrategy {
  def execute(playingField: IGameState): (Boolean, IGameState, List[ObservableEvent])
}
