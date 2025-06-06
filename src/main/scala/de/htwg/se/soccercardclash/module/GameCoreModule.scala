package de.htwg.se.soccercardclash.module

import com.google.inject.{AbstractModule, Scopes}
import de.htwg.se.soccercardclash.model.cardComponent.dataStructure.*
import de.htwg.se.soccercardclash.model.cardComponent.factory.IDeckFactory
import de.htwg.se.soccercardclash.model.fileIOComponent.IFileIO
import de.htwg.se.soccercardclash.model.gameComponent.*
import de.htwg.se.soccercardclash.model.gameComponent.action.manager.*
import de.htwg.se.soccercardclash.model.gameComponent.components.{IGameCardsFactory, IRolesFactory, IScoresFactory}
import de.htwg.se.soccercardclash.model.gameComponent.service.*
import de.htwg.se.soccercardclash.model.playerComponent.factory.IPlayerFactory
import de.htwg.se.soccercardclash.util.{GameContextHolder, IGameContextHolder}

class GameCoreModule extends AbstractModule {
  
  override def configure(): Unit = {
    
    bind(classOf[IGameInitializer])
      .toConstructor(classOf[GameInitializer]
        .getConstructor(
          classOf[IPlayerFactory],
          classOf[IDeckFactory],
          classOf[IGameCardsFactory],
          classOf[IRolesFactory],
          classOf[IScoresFactory]
        ))

    bind(classOf[IGameContextHolder]).to(classOf[GameContextHolder]).in(Scopes.SINGLETON)

    bind(classOf[IGamePersistence])
      .toConstructor(classOf[GamePersistence]
        .getConstructor(classOf[IFileIO]))
    
  }
}

