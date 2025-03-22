package de.htwg.se.soccercardclash.module

import de.htwg.se.soccercardclash.model.playerComponent.factory.*
import de.htwg.se.soccercardclash.model.cardComponent.factory.*
import de.htwg.se.soccercardclash.model.playingFiledComponent.manager.*
import de.htwg.se.soccercardclash.model.cardComponent.dataStructure.*
import de.htwg.se.soccercardclash.model.playerComponent.IPlayer
import com.google.inject.{AbstractModule, Provides}

class PlayerModule extends AbstractModule {
  
  override def configure(): Unit = {
    bind(classOf[IPlayerFactory]).to(classOf[PlayerFactory])
    bind(classOf[IPlayerFieldManager]).to(classOf[PlayerFieldManager])

    bind(classOf[PlayerDeserializer])
      .toConstructor(classOf[PlayerDeserializer]
        .getConstructor(classOf[IPlayerFactory], classOf[CardDeserializer]))

    bind(classOf[IPlayerHandManager])
      .toConstructor(classOf[PlayerHandManager]
        .getConstructor(classOf[IHandCardsQueueFactory]))
  }

  @Provides
  def providePlayer(factory: IPlayerFactory): IPlayer =
    factory.createPlayer("Player1", List.empty)
}
