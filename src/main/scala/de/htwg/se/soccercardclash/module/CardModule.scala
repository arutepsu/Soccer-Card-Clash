package de.htwg.se.soccercardclash.module

import com.google.inject.{AbstractModule, Provides}
import de.htwg.se.soccercardclash.model.cardComponent.ICard
import de.htwg.se.soccercardclash.model.cardComponent.base.components.*
import de.htwg.se.soccercardclash.model.cardComponent.factory.*

class CardModule extends AbstractModule {

  override def configure(): Unit = {
    
    bind(classOf[ICardFactory]).to(classOf[CardFactory])
    
    bind(classOf[IDeckFactory]).to(classOf[DeckFactory])
    
    bind(classOf[CardDeserializer])
      .toConstructor(classOf[CardDeserializer]
        .getConstructor(classOf[ICardFactory]))
  }
}
