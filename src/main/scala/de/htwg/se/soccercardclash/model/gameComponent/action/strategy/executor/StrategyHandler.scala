package de.htwg.se.soccercardclash.model.gameComponent.action.strategy.executor

import de.htwg.se.soccercardclash.model.gameComponent.IGameState
import de.htwg.se.soccercardclash.model.gameComponent.action.strategy.attack.*
import de.htwg.se.soccercardclash.model.gameComponent.action.strategy.boost.revert.*
import de.htwg.se.soccercardclash.model.gameComponent.action.strategy.boost.{DefenderBoostStrategy, GoalkeeperBoostStrategy}
import de.htwg.se.soccercardclash.model.gameComponent.action.strategy.swap.{RegularSwapStrategy, ReverseSwapStrategy}
import de.htwg.se.soccercardclash.model.gameComponent.action.strategy.trigger.IActionStrategy
import de.htwg.se.soccercardclash.util.{GameActionEvent, ObservableEvent}

import scala.reflect.ClassTag

class StrategyHandler[S <: IActionStrategy](using executor: StrategyExecutor[S], ct: ClassTag[S]) extends IActionHandler {
  private var next: Option[IActionHandler] = None

  override def setNext(handler: IActionHandler): IActionHandler = {
    next = Some(handler)
    this
  }


  override def handle(strategy: IActionStrategy, state: IGameState): Option[(Boolean, IGameState, List[ObservableEvent])] = {

    if executor.canHandle(strategy) then
      Some(executor.execute(strategy.asInstanceOf[S], state))
    else {
      next.flatMap(_.handle(strategy, state))
    }
  }

}


object HandlerChainFactory {

  def fullChain(): IActionHandler = {
    val singleAttack = StrategyHandler[SingleAttackStrategy]()
    val doubleAttack = StrategyHandler[DoubleAttackStrategy]()
    val defenderBoost = StrategyHandler[DefenderBoostStrategy]()
    val goalkeeperBoost = StrategyHandler[GoalkeeperBoostStrategy]()
    val regularSwap = StrategyHandler[RegularSwapStrategy]()
    val reverseSwap = StrategyHandler[ReverseSwapStrategy]()

    singleAttack.setNext(doubleAttack)
    doubleAttack.setNext(defenderBoost)
    defenderBoost.setNext(goalkeeperBoost)
    goalkeeperBoost.setNext(regularSwap)
    regularSwap.setNext(reverseSwap)

    singleAttack
  }
}

