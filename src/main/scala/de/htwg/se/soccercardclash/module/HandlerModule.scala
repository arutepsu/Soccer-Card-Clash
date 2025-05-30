package de.htwg.se.soccercardclash.module

import com.google.inject.{AbstractModule, Provides, Singleton}
import de.htwg.se.soccercardclash.model.gameComponent.IGameState
import de.htwg.se.soccercardclash.model.gameComponent.action.manager.*
import de.htwg.se.soccercardclash.model.gameComponent.action.strategy.attack.*
import de.htwg.se.soccercardclash.model.gameComponent.action.strategy.boost.revert.*
import de.htwg.se.soccercardclash.model.gameComponent.action.strategy.executor.{ActionHandler, HandlerChainFactory, IActionHandler}
import de.htwg.se.soccercardclash.model.gameComponent.action.strategy.swap.*
import de.htwg.se.soccercardclash.model.gameComponent.action.strategy.trigger.IActionStrategy
import de.htwg.se.soccercardclash.util.{GameActionEvent, ObservableEvent}


class HandlerModule extends AbstractModule {
  override def configure(): Unit = {}

//  @Provides
//  @Singleton
//  def provideHandlerChain(): IActionHandler = HandlerChainFactory.fullChain()

  @Provides
  @Singleton
  def provideActionManager(): IActionManager =
    new ActionManager
}
