package de.htwg.se.soccercardclash.module

import com.google.inject.{AbstractModule, Guice}
import de.htwg.se.soccercardclash.model.cardComponent.factory.CardDeserializer
import de.htwg.se.soccercardclash.model.gameComponent.playingFiledComponent.*
import de.htwg.se.soccercardclash.model.gameComponent.playingFiledComponent.base.*
import de.htwg.se.soccercardclash.model.gameComponent.playingFiledComponent.factory.*
import de.htwg.se.soccercardclash.model.gameComponent.playingFiledComponent.manager.*
import de.htwg.se.soccercardclash.model.gameComponent.playingFiledComponent.strategy.scoringStrategy.base.*
import de.htwg.se.soccercardclash.model.playerComponent.IPlayer
import de.htwg.se.soccercardclash.model.playerComponent.factory.PlayerDeserializer
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.mockito.MockitoSugar

class PlayingFieldModuleSpec extends AnyWordSpec with Matchers with MockitoSugar {

  "PlayingFieldModule" should {
    "bind all required components and construct PlayingFieldDeserializer" in {
      val mockPlayerDeserializer = mock[PlayerDeserializer]
      val mockPlayer = mock[IPlayer]
      val mockHandManager = mock[IPlayerHandManager]
      val mockFieldManager = mock[IPlayerFieldManager]

      val testModule = new AbstractModule {
        override def configure(): Unit = {
          install(new PlayingFieldModule)

          bind(classOf[PlayerDeserializer]).toInstance(mockPlayerDeserializer)
          bind(classOf[IPlayer]).toInstance(mockPlayer)
          bind(classOf[IPlayerHandManager]).toInstance(mockHandManager)
          bind(classOf[IPlayerFieldManager]).toInstance(mockFieldManager)
        }
      }

      val injector = Guice.createInjector(testModule)

      // Test a few key bindings
      injector.getInstance(classOf[IPlayingField]) shouldBe a[PlayingField]
      injector.getInstance(classOf[IDataManager]) shouldBe a[DataManager]
      injector.getInstance(classOf[PlayingFieldDeserializer]) should not be null
    }
  }
}