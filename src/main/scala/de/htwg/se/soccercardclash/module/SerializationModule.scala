package de.htwg.se.soccercardclash.module

import de.htwg.se.soccercardclash.model.fileIOComponent.jSONComponent.JsonComponent
import de.htwg.se.soccercardclash.model.fileIOComponent.xmlComponent.XmlComponent
import de.htwg.se.soccercardclash.model.gameComponent.factory.*
import de.htwg.se.soccercardclash.model.gameComponent.state.IGameStateFactory
import de.htwg.se.soccercardclash.model.playingFiledComponent.factory.PlayingFieldDeserializer
import de.htwg.se.soccercardclash.model.playerComponent.factory.PlayerDeserializer
import de.htwg.se.soccercardclash.model.cardComponent.dataStructure.*
import de.htwg.se.soccercardclash.model.cardComponent.factory.CardDeserializer
import com.google.inject.{AbstractModule, Singleton}

class SerializationModule extends AbstractModule {
  
  override def configure(): Unit = {
    bind(classOf[GameDeserializer])
      .toConstructor(classOf[GameDeserializer].getConstructor(
        classOf[IGameStateFactory],
        classOf[PlayingFieldDeserializer],
        classOf[PlayerDeserializer],
        classOf[HandCardsQueueDeserializer],
        classOf[IHandCardsQueueFactory],
        classOf[CardDeserializer]
      ))

    bind(classOf[JsonComponent])
      .toConstructor(classOf[JsonComponent].getConstructor(classOf[GameDeserializer]))
      .in(classOf[Singleton])

    bind(classOf[XmlComponent])
      .toConstructor(classOf[XmlComponent].getConstructor(classOf[GameDeserializer]))
      .in(classOf[Singleton])
  }
}
