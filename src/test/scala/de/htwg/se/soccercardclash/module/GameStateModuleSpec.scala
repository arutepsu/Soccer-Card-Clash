package de.htwg.se.soccercardclash.module

import com.google.inject.{AbstractModule, Guice}
import de.htwg.se.soccercardclash.model.cardComponent.factory.CardDeserializer
import de.htwg.se.soccercardclash.model.gameComponent.state.*
import de.htwg.se.soccercardclash.model.gameComponent.state.base.*
import de.htwg.se.soccercardclash.model.gameComponent.state.components.{DataManager, IDataManager, IFieldCards, IHandCards}
import de.htwg.se.soccercardclash.model.gameComponent.state.factory.*
import de.htwg.se.soccercardclash.model.gameComponent.state.manager.*
import de.htwg.se.soccercardclash.model.gameComponent.state.strategy.scoringStrategy.base.*
import de.htwg.se.soccercardclash.model.playerComponent.IPlayer
import de.htwg.se.soccercardclash.model.playerComponent.factory.PlayerDeserializer
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.mockito.MockitoSugar

class GameStateModuleSpec extends AnyWordSpec with Matchers with MockitoSugar {

  "PlayingFieldModule" should {
    "bind all required components and construct PlayingFieldDeserializer" in {
      val mockPlayerDeserializer = mock[PlayerDeserializer]
      val mockPlayer = mock[IPlayer]
      val mockHandManager = mock[IHandCards]
      val mockFieldManager = mock[IFieldCards]

      val testModule = new AbstractModule {
        override def configure(): Unit = {
          install(new PlayingFieldModule)

          bind(classOf[PlayerDeserializer]).toInstance(mockPlayerDeserializer)
          bind(classOf[IPlayer]).toInstance(mockPlayer)
          bind(classOf[IHandCards]).toInstance(mockHandManager)
          bind(classOf[IFieldCards]).toInstance(mockFieldManager)
        }
      }

      val injector = Guice.createInjector(testModule)

      // Test a few key bindings
      injector.getInstance(classOf[IGameState]) shouldBe a[GameState]
      injector.getInstance(classOf[IDataManager]) shouldBe a[DataManager]
      injector.getInstance(classOf[PlayingFieldDeserializer]) should not be null
    }
  }
}