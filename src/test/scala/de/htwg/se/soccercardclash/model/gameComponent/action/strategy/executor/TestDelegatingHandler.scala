package de.htwg.se.soccercardclash.model.gameComponent.action.strategy.executor

import de.htwg.se.soccercardclash.model.gameComponent.IGameState
import de.htwg.se.soccercardclash.model.gameComponent.action.strategy.trigger.IActionStrategy
import de.htwg.se.soccercardclash.util.{GameActionEvent, ObservableEvent}

class TestDelegatingHandler extends ActionHandler {
  override def handle(strategy: IActionStrategy, state: IGameState): Option[(Boolean, IGameState, List[ObservableEvent])] =
    handleNext(strategy, state)
}
