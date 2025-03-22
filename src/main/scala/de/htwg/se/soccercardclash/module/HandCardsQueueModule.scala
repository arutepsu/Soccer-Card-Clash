package de.htwg.se.soccercardclash.module

import com.google.inject.AbstractModule
import de.htwg.se.soccercardclash.model.cardComponent.dataStructure.*
import de.htwg.se.soccercardclash.model.cardComponent.factory.*

class HandCardsQueueModule extends AbstractModule {
  
  override def configure(): Unit = {
    bind(classOf[IHandCardsQueueFactory]).to(classOf[HandCardsQueueFactory])

    bind(classOf[HandCardsQueueDeserializer])
      .toConstructor(classOf[HandCardsQueueDeserializer]
        .getConstructor(classOf[CardDeserializer], classOf[IHandCardsQueueFactory]))
  }
}
