package de.htwg.se.soccercardclash.module


import com.google.inject.*
import com.google.inject.util.Modules
import de.htwg.se.soccercardclash.model.cardComponent.dataStructure.*
import de.htwg.se.soccercardclash.model.cardComponent.factory.CardDeserializer
import de.htwg.se.soccercardclash.model.fileIOComponent.jSONComponent.JsonComponent
import de.htwg.se.soccercardclash.model.fileIOComponent.xmlComponent.XmlComponent
import de.htwg.se.soccercardclash.model.gameComponent.service.{GameDeserializer, GameInitializer}
import de.htwg.se.soccercardclash.model.gameComponent.state.IGameStateFactory
import de.htwg.se.soccercardclash.model.gameComponent.state.factory.PlayingFieldDeserializer
import de.htwg.se.soccercardclash.model.playerComponent.factory.PlayerDeserializer
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.mockito.MockitoSugar

class SerializationModuleSpec extends AnyWordSpec with Matchers with MockitoSugar {

  "SerializationModule" should {

    "bind GameDeserializer, JsonComponent, and XmlComponent successfully" in {
      val overrideModule = new AbstractModule {
        override def configure(): Unit = {
          bind(classOf[IGameStateFactory]).toInstance(mock[IGameStateFactory])
          bind(classOf[PlayingFieldDeserializer]).toInstance(mock[PlayingFieldDeserializer])
          bind(classOf[PlayerDeserializer]).toInstance(mock[PlayerDeserializer])
          bind(classOf[HandCardsQueueDeserializer]).toInstance(mock[HandCardsQueueDeserializer])
          bind(classOf[IHandCardsQueueFactory]).toInstance(mock[IHandCardsQueueFactory])
          bind(classOf[CardDeserializer]).toInstance(mock[CardDeserializer])
        }
      }

      val injector = Guice.createInjector(
        (Modules.`override`(new SerializationModule)).`with`(overrideModule)
      )

      val deserializer = injector.getInstance(classOf[GameDeserializer])
      val jsonComponent = injector.getInstance(classOf[JsonComponent])
      val xmlComponent = injector.getInstance(classOf[XmlComponent])

      deserializer should not be null
      jsonComponent should not be null
      xmlComponent should not be null
    }
  }
}