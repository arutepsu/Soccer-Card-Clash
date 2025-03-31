package de.htwg.se.soccercardclash.module

import de.htwg.se.soccercardclash.model.gameComponent.factory.*
import de.htwg.se.soccercardclash.model.gameComponent.state.*
import de.htwg.se.soccercardclash.model.gameComponent.io.*
import de.htwg.se.soccercardclash.model.gameComponent.*
import de.htwg.se.soccercardclash.model.gameComponent.base.Game
import de.htwg.se.soccercardclash.model.cardComponent.dataStructure.*
import de.htwg.se.soccercardclash.model.fileIOComponent.IFileIO
import de.htwg.se.soccercardclash.model.cardComponent.factory.IDeckFactory
import de.htwg.se.soccercardclash.model.gameComponent.playingFiledComponent.factory.*
import de.htwg.se.soccercardclash.model.playerComponent.factory.IPlayerFactory
import com.google.inject.AbstractModule

class GameCoreModule extends AbstractModule {
  
  override def configure(): Unit = {
    bind(classOf[IGameInitializer])
      .toConstructor(classOf[GameInitializer]
        .getConstructor(classOf[IPlayerFactory], classOf[IPlayingFieldFactory], classOf[IDeckFactory]))

    bind(classOf[IGameStateFactory])
      .toConstructor(classOf[GameStateFactory]
        .getConstructor(classOf[IHandCardsQueueFactory]))
      .asEagerSingleton()

    bind(classOf[IGameStateManager])
      .toConstructor(classOf[GameStateManager]
        .getConstructor(classOf[IGameStateFactory], classOf[IHandCardsQueueFactory]))

    bind(classOf[IGamePersistence])
      .toConstructor(classOf[GamePersistence]
        .getConstructor(classOf[IFileIO]))

    bind(classOf[IGame])
      .toConstructor(classOf[Game].getConstructor(
        classOf[IGameInitializer],
        classOf[IGameStateManager],
        classOf[IGamePersistence],
      ))
  }
}

