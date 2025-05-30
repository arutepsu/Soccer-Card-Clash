package de.htwg.se.soccercardclash.model.gameComponent.action.strategy.swapStrategy

import de.htwg.se.soccercardclash.model.gameComponent.action.BaseActionHandler
import de.htwg.se.soccercardclash.model.gameComponent.action.strategy.IActionStrategy
import de.htwg.se.soccercardclash.model.gameComponent.IGameState
import de.htwg.se.soccercardclash.model.gameComponent.action.strategy.swapStrategy.base.ReverseSwapStrategy
import de.htwg.se.soccercardclash.util.GameActionEvent
import de.htwg.se.soccercardclash.util.ObservableEvent

class ReverseSwapHandler extends BaseActionHandler {
  override def handle(strategy: IActionStrategy, state: IGameState): Option[(Boolean, IGameState, List[ObservableEvent])] = {
    strategy match {
      case s: ReverseSwapStrategy => Some(s.execute(state))
      case _ => handleNext(strategy, state)
    }
  }
}