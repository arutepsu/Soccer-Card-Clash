package de.htwg.se.soccercardclash.module

import com.google.inject.{AbstractModule, Singleton}
import de.htwg.se.soccercardclash.model.cardComponent.dataStructure.*
import de.htwg.se.soccercardclash.model.cardComponent.factory.CardDeserializer
import de.htwg.se.soccercardclash.model.fileIOComponent.IFileIO
import de.htwg.se.soccercardclash.model.fileIOComponent.base.FileIO
import de.htwg.se.soccercardclash.model.fileIOComponent.jSONComponent.JsonComponent
import de.htwg.se.soccercardclash.model.fileIOComponent.xmlComponent.XmlComponent
import de.htwg.se.soccercardclash.model.gameComponent.action.manager.*
import de.htwg.se.soccercardclash.model.gameComponent.service.*
import de.htwg.se.soccercardclash.model.gameComponent.components.*
import de.htwg.se.soccercardclash.model.playerComponent.factory.PlayerDeserializer


class FileIOModule extends AbstractModule {

  override def configure(): Unit = {
    
    bind(classOf[IFileIO]).to(classOf[FileIO]).asEagerSingleton()

    bind(classOf[FileIO])
      .toConstructor(classOf[FileIO].getConstructor(classOf[JsonComponent], classOf[XmlComponent]))
      .in(classOf[Singleton])

    bind(classOf[GameDeserializer])
      .toConstructor(classOf[GameDeserializer].getConstructor(
        classOf[PlayerDeserializer],
        classOf[CardDeserializer],
        classOf[IHandCardsQueueFactory],
        classOf[IHandCardsFactory],
        classOf[IFieldCardsFactory],
        classOf[IGameCardsFactory],
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
