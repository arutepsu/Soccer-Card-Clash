package de.htwg.se.soccercardclash.model.gameComponent.action.strategy.boostStrategy

import de.htwg.se.soccercardclash.model.gameComponent.action.BaseActionHandler
import de.htwg.se.soccercardclash.model.gameComponent.action.strategy.IActionStrategy
import de.htwg.se.soccercardclash.model.gameComponent.IGameState
import de.htwg.se.soccercardclash.model.gameComponent.action.strategy.boostStrategy.base.GoalkeeperBoostStrategy
import de.htwg.se.soccercardclash.util.GameActionEvent
import de.htwg.se.soccercardclash.util.ObservableEvent

class BoostGoalkeeperHandler extends BaseActionHandler {
  override def handle(strategy: IActionStrategy, state: IGameState): Option[(Boolean, IGameState, List[ObservableEvent])] = {
    strategy match {
      case s: GoalkeeperBoostStrategy => Some(s.execute(state))
      case _ => handleNext(strategy, state)
    }
  }
}