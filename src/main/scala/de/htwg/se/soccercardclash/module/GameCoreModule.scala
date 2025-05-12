package de.htwg.se.soccercardclash.module

import de.htwg.se.soccercardclash.controller.{GameContextHolder, IGameContextHolder}
import de.htwg.se.soccercardclash.model.gameComponent.service.*
import de.htwg.se.soccercardclash.model.gameComponent.*
import de.htwg.se.soccercardclash.model.cardComponent.dataStructure.*
import de.htwg.se.soccercardclash.model.fileIOComponent.IFileIO
import de.htwg.se.soccercardclash.model.cardComponent.factory.IDeckFactory
import de.htwg.se.soccercardclash.model.gameComponent.state.manager.*
import de.htwg.se.soccercardclash.model.playerComponent.factory.IPlayerFactory
import com.google.inject.{AbstractModule, Scopes}
import de.htwg.se.soccercardclash.model.gameComponent.state.components.{IDataManagerFactory, IRolesFactory, IScoresFactory}
class GameCoreModule extends AbstractModule {
  
  override def configure(): Unit = {
    bind(classOf[IGameInitializer])
      .toConstructor(classOf[GameInitializer]
        .getConstructor(
          classOf[IPlayerFactory],
          classOf[IDeckFactory],
          classOf[IDataManagerFactory],
          classOf[IRolesFactory],
          classOf[IScoresFactory]
        ))


//    bind(classOf[IGameStateFactory])
//      .toConstructor(classOf[GameStateFactory]
//        .getConstructor(classOf[IHandCardsQueueFactory]))
//      .asEagerSingleton()S

    bind(classOf[IGameContextHolder]).to(classOf[GameContextHolder]).in(Scopes.SINGLETON)
//    bind(classOf[IGameStateManager])
//      .toConstructor(classOf[GameStateManager]
//        .getConstructor(classOf[IHandCardsQueueFactory]))

    bind(classOf[IGamePersistence])
      .toConstructor(classOf[GamePersistence]
        .getConstructor(classOf[IFileIO]))

//    bind(classOf[IGame])
//      .toConstructor(classOf[Game].getConstructor(
//        classOf[IGameInitializer],
//        classOf[IGameStateManager],
//        classOf[IGamePersistence],
//      ))
  }
}

