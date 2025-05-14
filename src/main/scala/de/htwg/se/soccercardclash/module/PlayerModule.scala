package de.htwg.se.soccercardclash.module

import de.htwg.se.soccercardclash.model.playerComponent.factory.*
import de.htwg.se.soccercardclash.model.cardComponent.factory.*
import de.htwg.se.soccercardclash.model.gameComponent.state.manager.*
import de.htwg.se.soccercardclash.model.cardComponent.dataStructure.*
import de.htwg.se.soccercardclash.model.playerComponent.IPlayer
import com.google.inject.{AbstractModule, Provides}
import de.htwg.se.soccercardclash.model.gameComponent.state.components.{FieldCards, FieldCardsFactory, IFieldCards, IFieldCardsFactory}

class PlayerModule extends AbstractModule {

  override def configure(): Unit = {
    bind(classOf[IPlayerFactory]).to(classOf[PlayerFactory])
    bind(classOf[IFieldCardsFactory]).to(classOf[FieldCardsFactory])

    bind(classOf[PlayerDeserializer])
      .toConstructor(classOf[PlayerDeserializer]
        .getConstructor(classOf[IPlayerFactory], classOf[CardDeserializer]))
    
  }

  @Provides
  def providePlayer(factory: IPlayerFactory): IPlayer =
    factory.createPlayer("Player1")

  @Provides
  def provideEmptyPlayerFieldManager(): IFieldCards = FieldCards()
}