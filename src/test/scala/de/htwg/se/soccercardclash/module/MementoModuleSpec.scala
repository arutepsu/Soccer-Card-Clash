package de.htwg.se.soccercardclash.module

import com.google.inject.Guice
import org.scalatest.matchers.should.Matchers
import de.htwg.se.soccercardclash.model.gameComponent.state.memento.factory.*
import de.htwg.se.soccercardclash.model.gameComponent.state.memento.components.*
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.mockito.MockitoSugar
import de.htwg.se.soccercardclash.model.gameComponent.IGame
import de.htwg.se.soccercardclash.model.cardComponent.dataStructure.*
class MementoModuleSpec extends AnyWordSpec with Matchers with MockitoSugar {

  class TestMementoModule extends MementoModule {
    override def configure(): Unit = {
      // Bind the factory dependency
      bind(classOf[IHandCardsQueueFactory]).toInstance(mock[IHandCardsQueueFactory])
      super.configure()
    }
  }

  "MementoModule" should {

    "bind IMementoCreatorFactory to a working factory" in {
      val injector = Guice.createInjector(new TestMementoModule)
      val factory = injector.getInstance(classOf[IMementoCreatorFactory])
      factory should not be null
    }

    "bind IMementoRestorerFactory to a working factory" in {
      val injector = Guice.createInjector(new TestMementoModule)
      val factory = injector.getInstance(classOf[IMementoRestorerFactory])
      factory should not be null
    }

    "bind IMementoManagerFactory to MementoManagerFactory as eager singleton" in {
      val injector = Guice.createInjector(new TestMementoModule)
      val factory = injector.getInstance(classOf[IMementoManagerFactory])
      factory shouldBe a[MementoManagerFactory]

      val factory2 = injector.getInstance(classOf[IMementoManagerFactory])
      factory should be theSameInstanceAs factory2
    }

    "inject MementoCreator via factory with mocked IGame" in {
      val injector = Guice.createInjector(new TestMementoModule)
      val creatorFactory = injector.getInstance(classOf[IMementoCreatorFactory])
      val mockGame = mock[IGame]
      val creator = creatorFactory.create(mockGame)
      creator shouldBe a[MementoCreator]
    }

    "inject MementoRestorer via factory with mocked IGame" in {
      val injector = Guice.createInjector(new TestMementoModule)
      val restorerFactory = injector.getInstance(classOf[IMementoRestorerFactory])
      val mockGame = mock[IGame]
      val restorer = restorerFactory.create(mockGame)
      restorer shouldBe a[MementoRestorer]
    }
  }
}
