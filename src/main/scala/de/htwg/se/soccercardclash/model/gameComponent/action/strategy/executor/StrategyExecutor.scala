package de.htwg.se.soccercardclash.model.gameComponent.action.strategy.executor

import de.htwg.se.soccercardclash.model.gameComponent.IGameState
import de.htwg.se.soccercardclash.model.gameComponent.action.strategy.IActionStrategy
import de.htwg.se.soccercardclash.model.gameComponent.action.strategy.attackStrategy.base.*
import de.htwg.se.soccercardclash.model.gameComponent.action.strategy.boostStrategy.base.*
import de.htwg.se.soccercardclash.model.gameComponent.action.strategy.swapStrategy.base.*
import de.htwg.se.soccercardclash.util.{GameActionEvent, ObservableEvent}
import de.htwg.se.soccercardclash.model.gameComponent.action.{BaseActionHandler, IActionHandler}

trait StrategyExecutor[S <: IActionStrategy] {
  def canHandle(strategy: IActionStrategy): Boolean
  def execute(strategy: S, state: IGameState): (Boolean, IGameState, List[ObservableEvent])
}

object StrategyExecutor {
  inline given derived[S <: IActionStrategy]: StrategyExecutor[S] =
    new StrategyExecutor[S] {
      override def canHandle(strategy: IActionStrategy): Boolean =
        strategy.isInstanceOf[S]

      override def execute(strategy: S, state: IGameState): (Boolean, IGameState, List[ObservableEvent]) =
        strategy.execute(state)
    }
}
class StrategyHandler[S <: IActionStrategy](using executor: StrategyExecutor[S]) extends BaseActionHandler {
  override def handle(strategy: IActionStrategy, state: IGameState): Option[(Boolean, IGameState, List[ObservableEvent])] = {
    if executor.canHandle(strategy) then
      val typed = strategy.asInstanceOf[S]
      Some(executor.execute(typed, state))
    else
      handleNext(strategy, state)
  }
}

object HandlerChainFactory {

  def attackChain(): IActionHandler =
    StrategyHandler[SingleAttackStrategy]()
      .setNext(StrategyHandler[DoubleAttackStrategy]())

  def boostChain(): IActionHandler =
    StrategyHandler[DefenderBoostStrategy]()
      .setNext(StrategyHandler[GoalkeeperBoostStrategy]())

  def swapChain(): IActionHandler =
    StrategyHandler[RegularSwapStrategy]()
      .setNext(StrategyHandler[ReverseSwapStrategy]())

  def fullChain(): IActionHandler = {
    val attack = attackChain()
    val boost = boostChain()
    val swap  = swapChain()

    // Compose all
    attack.setNext(boost).setNext(swap)
  }
}

