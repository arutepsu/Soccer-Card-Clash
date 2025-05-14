package de.htwg.se.soccercardclash.module

import com.google.inject.{AbstractModule, Singleton}
import de.htwg.se.soccercardclash.controller.IController
import de.htwg.se.soccercardclash.controller.base.Controller
import de.htwg.se.soccercardclash.controller.command.{CommandFactory, ICommandFactory}
import de.htwg.se.soccercardclash.model.cardComponent.dataStructure.*
import de.htwg.se.soccercardclash.model.gameComponent.service.*
import de.htwg.se.soccercardclash.model.gameComponent.state.manager.*
import de.htwg.se.soccercardclash.util.Observable

class ControllerModule extends AbstractModule {

  override def configure(): Unit = {
    bind(classOf[IController]).to(classOf[Controller]).in(classOf[Singleton])
    bind(classOf[Observable]).to(classOf[IController])
    bind(classOf[ICommandFactory]).to(classOf[CommandFactory])

    bind(classOf[IGameService])
      .toConstructor(classOf[GameService].getConstructor(
        classOf[IGameInitializer],
        classOf[IGamePersistence]
      ))
      .in(classOf[Singleton])

    bind(classOf[IHandCardsQueueFactory]).to(classOf[HandCardsQueueFactory])
    bind(classOf[IActionManager]).to(classOf[ActionManager])
  }
}
