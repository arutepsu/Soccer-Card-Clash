package de.htwg.se.soccercardclash.model.gameComponent.action.strategy.executor

import de.htwg.se.soccercardclash.model.gameComponent.IGameState
import de.htwg.se.soccercardclash.model.gameComponent.action.strategy.trigger.attack.*
import de.htwg.se.soccercardclash.model.gameComponent.action.strategy.trigger.boost.revert.*
import de.htwg.se.soccercardclash.model.gameComponent.action.strategy.trigger.boost.{DefenderBoostStrategy, GoalkeeperBoostStrategy}
import de.htwg.se.soccercardclash.model.gameComponent.action.strategy.trigger.swap.{RegularSwapStrategy, ReverseSwapStrategy}
import de.htwg.se.soccercardclash.model.gameComponent.action.strategy.trigger.IActionStrategy
import de.htwg.se.soccercardclash.util.{GameActionEvent, ObservableEvent}

import scala.reflect.ClassTag

class StrategyHandler[S <: IActionStrategy](
                                             val next: Option[IActionHandler] = None
                                           )(using executor: StrategyExecutor[S], ct: ClassTag[S])
  extends IActionHandler {

  override def setNext(handler: IActionHandler): IActionHandler =
    new StrategyHandler[S](Some(handler))

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
    val reverseSwap     = StrategyHandler[ReverseSwapStrategy]()
    val regularSwap     = StrategyHandler[RegularSwapStrategy](Some(reverseSwap))
    val goalkeeperBoost = StrategyHandler[GoalkeeperBoostStrategy](Some(regularSwap))
    val defenderBoost   = StrategyHandler[DefenderBoostStrategy](Some(goalkeeperBoost))
    val doubleAttack    = StrategyHandler[DoubleAttackStrategy](Some(defenderBoost))
    val singleAttack    = StrategyHandler[SingleAttackStrategy](Some(doubleAttack))

    singleAttack
  }
}

