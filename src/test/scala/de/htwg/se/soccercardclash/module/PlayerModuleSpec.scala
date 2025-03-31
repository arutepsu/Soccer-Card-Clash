package de.htwg.se.soccercardclash.module

import com.google.inject.{AbstractModule, Guice}
import de.htwg.se.soccercardclash.model.playerComponent.factory.*
import de.htwg.se.soccercardclash.model.cardComponent.factory.CardDeserializer
import de.htwg.se.soccercardclash.model.cardComponent.dataStructure.IHandCardsQueueFactory
import de.htwg.se.soccercardclash.model.gameComponent.playingFiledComponent.manager.{IPlayerFieldManager, IPlayerHandManager, PlayerFieldManager, PlayerHandManager}
import de.htwg.se.soccercardclash.model.playerComponent.IPlayer
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.mockito.MockitoSugar

class PlayerModuleSpec extends AnyWordSpec with Matchers with MockitoSugar {

  // Inject missing bindings for test
  class TestOverrides extends AbstractModule {
    override def configure(): Unit = {
      bind(classOf[CardDeserializer]).toInstance(mock[CardDeserializer])
      bind(classOf[IHandCardsQueueFactory]).toInstance(mock[IHandCardsQueueFactory])
    }
  }

  val injector = Guice.createInjector(new PlayerModule, new TestOverrides)

  "PlayerModule" should {
    "bind IPlayerFactory to PlayerFactory" in {
      injector.getInstance(classOf[IPlayerFactory]) should not be null
    }

    "bind IPlayerFieldManager to PlayerFieldManager" in {
      injector.getInstance(classOf[IPlayerFieldManager]) should not be null
    }

    "bind PlayerDeserializer with dependencies" in {
      injector.getInstance(classOf[PlayerDeserializer]) should not be null
    }

    "bind IPlayerHandManager with queue factory" in {
      injector.getInstance(classOf[IPlayerHandManager]) should not be null
    }

    "provide a default player using @Provides method" in {
      val player = injector.getInstance(classOf[IPlayer])
      player.name shouldBe "Player1"
    }
  }
}