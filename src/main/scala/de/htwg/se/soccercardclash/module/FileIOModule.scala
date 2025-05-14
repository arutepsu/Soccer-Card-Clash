package de.htwg.se.soccercardclash.module

import com.google.inject.{AbstractModule, Singleton}
import de.htwg.se.soccercardclash.model.fileIOComponent.IFileIO
import de.htwg.se.soccercardclash.model.fileIOComponent.base.FileIO
import de.htwg.se.soccercardclash.model.fileIOComponent.jSONComponent.JsonComponent
import de.htwg.se.soccercardclash.model.fileIOComponent.xmlComponent.XmlComponent


class FileIOModule extends AbstractModule {

  override def configure(): Unit = {
    bind(classOf[IFileIO]).to(classOf[FileIO]).asEagerSingleton()

    bind(classOf[FileIO])
      .toConstructor(classOf[FileIO].getConstructor(classOf[JsonComponent], classOf[XmlComponent]))
      .in(classOf[Singleton])
  }
}
