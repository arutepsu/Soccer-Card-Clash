package de.htwg.se.soccercardclash.model.gameComponent.action.manager

import com.google.inject.Inject
import de.htwg.se.soccercardclash.model.cardComponent.factory.DeckFactory
import de.htwg.se.soccercardclash.model.gameComponent.IGameState
import de.htwg.se.soccercardclash.model.gameComponent.action.manager.IActionExecutor
import de.htwg.se.soccercardclash.model.gameComponent.action.strategy.trigger.attack.{DoubleAttackStrategy, SingleAttackStrategy}
import de.htwg.se.soccercardclash.model.gameComponent.action.strategy.trigger.boost.*
import de.htwg.se.soccercardclash.model.gameComponent.action.strategy.executor.{HandlerChainFactory, IActionHandler}
import de.htwg.se.soccercardclash.model.gameComponent.action.strategy.trigger.swap.*
import de.htwg.se.soccercardclash.model.gameComponent.action.strategy.trigger.IActionStrategy
import de.htwg.se.soccercardclash.model.gameComponent.base.GameState
import de.htwg.se.soccercardclash.model.gameComponent.components.*
import de.htwg.se.soccercardclash.util.ObservableEvent
import play.api.libs.json.*
import play.api.libs.json.util.*

import scala.collection.mutable.ListBuffer
import scala.xml.*

class ActionExecutor @Inject() extends IActionExecutor {

  private val handlerChain: IActionHandler = HandlerChainFactory.fullChain()

  override def execute(strategy: IActionStrategy, state: IGameState): (Boolean, IGameState, List[ObservableEvent]) = {
    handlerChain.handle(strategy, state).getOrElse {
      (false, state, List.empty)
    }
  }

}
trait IActionExecutor {
  def execute(strategy: IActionStrategy, state: IGameState): (Boolean, IGameState, List[ObservableEvent])
}
