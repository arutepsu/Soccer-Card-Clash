package de.htwg.se.soccercardclash.module

import com.google.inject.{AbstractModule, Provides, Singleton}
import de.htwg.se.soccercardclash.model.cardComponent.dataStructure.*
import de.htwg.se.soccercardclash.model.cardComponent.factory.*
import de.htwg.se.soccercardclash.model.gameComponent.action.manager.*
import de.htwg.se.soccercardclash.model.gameComponent.components.{FieldCards, FieldCardsFactory, IFieldCards, IFieldCardsFactory}
import de.htwg.se.soccercardclash.model.playerComponent.IPlayer
import de.htwg.se.soccercardclash.model.playerComponent.ai.AIPresetRegistry
import de.htwg.se.soccercardclash.model.playerComponent.factory.*
import de.htwg.se.soccercardclash.model.playerComponent.util.IRandomProvider

class PlayerModule extends AbstractModule {

  override def configure(): Unit = {
    
    bind(classOf[IFieldCardsFactory]).to(classOf[FieldCardsFactory])

    bind(classOf[PlayerDeserializer])
      .toConstructor(classOf[PlayerDeserializer]
        .getConstructor(classOf[CardDeserializer], classOf[Map[String, IRandomProvider]]))
    
  }

  @Provides
  @Singleton
  def provideAiRoster(
                       randomProviders: Map[String, IRandomProvider]
                     ): Map[String, IPlayer] = {
    AIPresetRegistry.registerCoreAIs(randomProviders)
  }
}