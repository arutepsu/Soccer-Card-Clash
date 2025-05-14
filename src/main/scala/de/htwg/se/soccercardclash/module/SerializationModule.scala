package de.htwg.se.soccercardclash.module

import com.google.inject.{AbstractModule, Singleton}
import de.htwg.se.soccercardclash.model.cardComponent.dataStructure.*
import de.htwg.se.soccercardclash.model.cardComponent.factory.CardDeserializer
import de.htwg.se.soccercardclash.model.fileIOComponent.jSONComponent.JsonComponent
import de.htwg.se.soccercardclash.model.fileIOComponent.xmlComponent.XmlComponent
import de.htwg.se.soccercardclash.model.gameComponent.service.*
import de.htwg.se.soccercardclash.model.gameComponent.state.components.*
import de.htwg.se.soccercardclash.model.gameComponent.state.manager.*
import de.htwg.se.soccercardclash.model.playerComponent.factory.PlayerDeserializer

class SerializationModule extends AbstractModule {

  override def configure(): Unit = {
    bind(classOf[GameDeserializer])
      .toConstructor(classOf[GameDeserializer].getConstructor(
        classOf[PlayerDeserializer],
        classOf[CardDeserializer],
        classOf[IHandCardsQueueFactory],
        classOf[IHandCardsFactory],
        classOf[IFieldCardsFactory],
        classOf[IDataManagerFactory],
        classOf[IRolesFactory],
        classOf[IScoresFactory]
      ))


    bind(classOf[JsonComponent])
      .toConstructor(classOf[JsonComponent].getConstructor(classOf[GameDeserializer]))
      .in(classOf[Singleton])

    bind(classOf[XmlComponent])
      .toConstructor(classOf[XmlComponent].getConstructor(classOf[GameDeserializer]))
      .in(classOf[Singleton])
  }
}
