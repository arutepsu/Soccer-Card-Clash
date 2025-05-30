package de.htwg.se.soccercardclash.model.gameComponent.action.strategy.executor

import de.htwg.se.soccercardclash.model.gameComponent.IGameState
import de.htwg.se.soccercardclash.model.gameComponent.action.strategy.trigger.attack.*
import de.htwg.se.soccercardclash.model.gameComponent.action.strategy.trigger.boost.revert.*
import de.htwg.se.soccercardclash.model.gameComponent.action.strategy.trigger.boost.{DefenderBoostStrategy, GoalkeeperBoostStrategy}
import de.htwg.se.soccercardclash.model.gameComponent.action.strategy.trigger.swap.{RegularSwapStrategy, ReverseSwapStrategy}
import de.htwg.se.soccercardclash.model.gameComponent.action.strategy.trigger.IActionStrategy
import de.htwg.se.soccercardclash.util.{GameActionEvent, ObservableEvent}

import scala.reflect.ClassTag

trait StrategyExecutor[S <: IActionStrategy] {
  def canHandle(strategy: IActionStrategy): Boolean
  def execute(strategy: S, state: IGameState): (Boolean, IGameState, List[ObservableEvent])
}

object StrategyExecutor {
  inline given derived[S <: IActionStrategy](using ct: ClassTag[S]): StrategyExecutor[S] =
    new StrategyExecutor[S] {
      override def canHandle(strategy: IActionStrategy): Boolean =
        ct.runtimeClass.isInstance(strategy)


      override def execute(strategy: S, state: IGameState): (Boolean, IGameState, List[ObservableEvent]) =
        strategy.execute(state)
    }
}
