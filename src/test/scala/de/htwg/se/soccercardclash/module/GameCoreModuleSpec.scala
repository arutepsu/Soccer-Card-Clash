package de.htwg.se.soccercardclash.module

import com.google.inject.{AbstractModule, Guice, Scopes}
import de.htwg.se.soccercardclash.model.cardComponent.dataStructure.*
import de.htwg.se.soccercardclash.model.cardComponent.factory.IDeckFactory
import de.htwg.se.soccercardclash.model.fileIOComponent.IFileIO
import de.htwg.se.soccercardclash.model.gameComponent.*
import de.htwg.se.soccercardclash.model.gameComponent.service.*
import de.htwg.se.soccercardclash.model.gameComponent.components.{IGameCardsFactory, IRolesFactory, IScoresFactory}
import de.htwg.se.soccercardclash.model.gameComponent.action.manager.*
import de.htwg.se.soccercardclash.model.playerComponent.factory.IPlayerFactory
import de.htwg.se.soccercardclash.util.{GameContextHolder, IGameContextHolder}
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.mockito.Mockito.*

class GameCoreModuleSpec extends AnyWordSpec with Matchers {

  "GameCoreModule" should {
    val injector = Guice.createInjector(new GameCoreModule {
      override def configure(): Unit = {
        // Provide mock dependencies for GameInitializer and GamePersistence
        bind(classOf[IPlayerFactory]).toInstance(mock(classOf[IPlayerFactory]))
        bind(classOf[IDeckFactory]).toInstance(mock(classOf[IDeckFactory]))
        bind(classOf[IGameCardsFactory]).toInstance(mock(classOf[IGameCardsFactory]))
        bind(classOf[IRolesFactory]).toInstance(mock(classOf[IRolesFactory]))
        bind(classOf[IScoresFactory]).toInstance(mock(classOf[IScoresFactory]))
        bind(classOf[IFileIO]).toInstance(mock(classOf[IFileIO]))


        super.configure()
      }
    })

    "bind IGameInitializer to GameInitializer with injected factories" in {
      val initializer = injector.getInstance(classOf[IGameInitializer])
      initializer shouldBe a[GameInitializer]
    }

    "bind IGameContextHolder to GameContextHolder as singleton" in {
      val holder1 = injector.getInstance(classOf[IGameContextHolder])
      val holder2 = injector.getInstance(classOf[IGameContextHolder])
      holder1 shouldBe a[GameContextHolder]
      holder1 shouldBe theSameInstanceAs(holder2)
    }

    "bind IGamePersistence to GamePersistence with IFileIO" in {
      val persistence = injector.getInstance(classOf[IGamePersistence])
      persistence shouldBe a[GamePersistence]
    }
  }
}