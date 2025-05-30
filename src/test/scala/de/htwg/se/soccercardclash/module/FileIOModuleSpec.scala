package de.htwg.se.soccercardclash.module

import com.google.inject.{AbstractModule, Guice, Singleton}
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
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.mockito.MockitoSugar
import org.mockito.Mockito.*

class FileIOModuleSpec extends AnyWordSpec with Matchers {

  "FileIOModule" should {
    val injector = Guice.createInjector(new FileIOModule {
      override def configure(): Unit = {
        // Provide mocked or dummy implementations for required constructors
        bind(classOf[PlayerDeserializer]).toInstance(mock(classOf[PlayerDeserializer]))
        bind(classOf[CardDeserializer]).toInstance(mock(classOf[CardDeserializer]))
        bind(classOf[IHandCardsQueueFactory]).toInstance(mock(classOf[IHandCardsQueueFactory]))
        bind(classOf[IHandCardsFactory]).toInstance(mock(classOf[IHandCardsFactory]))
        bind(classOf[IFieldCardsFactory]).toInstance(mock(classOf[IFieldCardsFactory]))
        bind(classOf[IGameCardsFactory]).toInstance(mock(classOf[IGameCardsFactory]))
        bind(classOf[IRolesFactory]).toInstance(mock(classOf[IRolesFactory]))
        bind(classOf[IScoresFactory]).toInstance(mock(classOf[IScoresFactory]))

        super.configure()
      }
    })

    "bind IFileIO to FileIO eagerly as singleton" in {
      val fileIO1 = injector.getInstance(classOf[IFileIO])
      val fileIO2 = injector.getInstance(classOf[IFileIO])
      fileIO1 shouldBe a[FileIO]
      fileIO1 shouldBe theSameInstanceAs(fileIO2)
    }

    "provide GameDeserializer with all its dependencies" in {
      val deserializer = injector.getInstance(classOf[GameDeserializer])
      deserializer should not be null
    }

    "provide JsonComponent and XmlComponent as singletons" in {
      val json1 = injector.getInstance(classOf[JsonComponent])
      val json2 = injector.getInstance(classOf[JsonComponent])
      json1 shouldBe theSameInstanceAs(json2)

      val xml1 = injector.getInstance(classOf[XmlComponent])
      val xml2 = injector.getInstance(classOf[XmlComponent])
      xml1 shouldBe theSameInstanceAs(xml2)
    }
  }
}
