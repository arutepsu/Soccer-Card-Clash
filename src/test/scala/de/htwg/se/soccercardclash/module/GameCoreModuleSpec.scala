package de.htwg.se.soccercardclash.module

import com.google.inject.{Guice, AbstractModule}
import de.htwg.se.soccercardclash.model.cardComponent.dataStructure.IHandCardsQueueFactory
import de.htwg.se.soccercardclash.model.cardComponent.factory.IDeckFactory
import de.htwg.se.soccercardclash.model.fileIOComponent.IFileIO
import de.htwg.se.soccercardclash.model.gameComponent.service.{IGameInitializer, IGamePersistence}
import de.htwg.se.soccercardclash.model.gameComponent.state.factory.IPlayingFieldFactory
import de.htwg.se.soccercardclash.model.gameComponent.state.IGameStateManager
import de.htwg.se.soccercardclash.model.gameComponent.IGame
import de.htwg.se.soccercardclash.model.playerComponent.factory.IPlayerFactory
import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import org.scalatestplus.mockito.MockitoSugar
import de.htwg.se.soccercardclash.model.gameComponent.state.IGameStateFactory
class GameCoreModuleSpec extends AnyWordSpec with Matchers with MockitoSugar {

  "GameCoreModule" should {
    "bind all core game components correctly" in {
      val overrideModule = new AbstractModule {
        override def configure(): Unit = {
          bind(classOf[IPlayerFactory]).toInstance(mock[IPlayerFactory])
          bind(classOf[IPlayingFieldFactory]).toInstance(mock[IPlayingFieldFactory])
          bind(classOf[IDeckFactory]).toInstance(mock[IDeckFactory])
          bind(classOf[IHandCardsQueueFactory]).toInstance(mock[IHandCardsQueueFactory])
          bind(classOf[IFileIO]).toInstance(mock[IFileIO])
        }
      }

      val injector = Guice.createInjector(new GameCoreModule, overrideModule)

      injector.getInstance(classOf[IGameInitializer]) should not be null
      injector.getInstance(classOf[IGameStateFactory]) should not be null
      injector.getInstance(classOf[IGameStateManager]) should not be null
      injector.getInstance(classOf[IGamePersistence]) should not be null
      injector.getInstance(classOf[IGame]) should not be null
    }
  }
}
