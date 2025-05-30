package de.htwg.se.soccercardclash.module

import com.google.inject.{AbstractModule, Provides, Singleton}
import de.htwg.se.soccercardclash.controller.IController
import de.htwg.se.soccercardclash.controller.base.Controller
import de.htwg.se.soccercardclash.controller.command.{CommandFactory, ICommandFactory}
import de.htwg.se.soccercardclash.model.cardComponent.dataStructure.*
import de.htwg.se.soccercardclash.model.gameComponent.service.*
import de.htwg.se.soccercardclash.model.gameComponent.action.manager.*
import de.htwg.se.soccercardclash.model.gameComponent.action.strategy.executor.HandlerChainFactory
import de.htwg.se.soccercardclash.util.Observable
import de.htwg.se.soccercardclash.model.playerComponent.IPlayer
import de.htwg.se.soccercardclash.model.playerComponent.ai.AIPresetRegistry
import de.htwg.se.soccercardclash.model.playerComponent.factory.IPlayerFactory
import de.htwg.se.soccercardclash.model.playerComponent.util.IRandomProvider

class ControllerModule extends AbstractModule {

  override def configure(): Unit = {
    
    bind(classOf[IController]).to(classOf[Controller]).in(classOf[Singleton])
    
    bind(classOf[Observable]).to(classOf[IController])
    
    bind(classOf[ICommandFactory]).to(classOf[CommandFactory])

    bind(classOf[IGameService])
    .toConstructor(classOf[GameService].getConstructor(
      classOf[IGameInitializer],
      classOf[IGamePersistence],
      classOf[Map[String, IPlayer]]
    ))
    .in(classOf[Singleton])

//    bind(classOf[IActionManager]).to(classOf[ActionManager])
    
  }
  
}