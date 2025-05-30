package de.htwg.se.soccercardclash.model.gameComponent.action.strategy.attackStrategy

import de.htwg.se.soccercardclash.model.gameComponent.action.BaseActionHandler
import de.htwg.se.soccercardclash.model.gameComponent.action.strategy.IActionStrategy
import de.htwg.se.soccercardclash.model.gameComponent.IGameState
import de.htwg.se.soccercardclash.model.gameComponent.action.strategy.attackStrategy.base.SingleAttackStrategy
import de.htwg.se.soccercardclash.util.GameActionEvent
import de.htwg.se.soccercardclash.util.ObservableEvent

class SingleAttackHandler extends BaseActionHandler {
  override def handle(strategy: IActionStrategy, state: IGameState): Option[(Boolean, IGameState, List[ObservableEvent])] = {
    strategy match {
      case s: SingleAttackStrategy => Some(s.execute(state))
      case _ => handleNext(strategy, state)
    }
  }
}