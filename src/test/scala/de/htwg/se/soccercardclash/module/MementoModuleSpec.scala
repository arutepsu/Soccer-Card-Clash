package de.htwg.se.soccercardclash.module

import com.google.inject.Guice
import org.scalatest.matchers.should.Matchers
import de.htwg.se.soccercardclash.model.gameComponent.state.memento.factory.*
import de.htwg.se.soccercardclash.model.gameComponent.state.memento.components.*
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.mockito.MockitoSugar
import de.htwg.se.soccercardclash.model.gameComponent.IGame

class MementoModuleSpec extends AnyWordSpec with Matchers with MockitoSugar {

  "MementoModule" should {

    "bind IMementoCreatorFactory to a working factory" in {
      val injector = Guice.createInjector(new MementoModule)
      val factory = injector.getInstance(classOf[IMementoCreatorFactory])
      factory should not be null
    }

    "bind IMementoRestorerFactory to a working factory" in {
      val injector = Guice.createInjector(new MementoModule)
      val factory = injector.getInstance(classOf[IMementoRestorerFactory])
      factory should not be null
    }

    "bind IMementoManagerFactory to MementoManagerFactory as eager singleton" in {
      val injector = Guice.createInjector(new MementoModule)
      val factory = injector.getInstance(classOf[IMementoManagerFactory])

      // Basic type check
      factory shouldBe a[MementoManagerFactory]

      // Optional: Confirm singleton behavior
      val factory2 = injector.getInstance(classOf[IMementoManagerFactory])
      factory should be theSameInstanceAs factory2
    }

    "inject MementoCreator via factory with mocked IGame" in {
      val injector = Guice.createInjector(new MementoModule)
      val creatorFactory = injector.getInstance(classOf[IMementoCreatorFactory])
      val mockGame = mock[IGame]
      val creator = creatorFactory.create(mockGame)
      creator shouldBe a[MementoCreator]
    }

    "inject MementoRestorer via factory with mocked IGame" in {
      val injector = Guice.createInjector(new MementoModule)
      val restorerFactory = injector.getInstance(classOf[IMementoRestorerFactory])
      val mockGame = mock[IGame]
      val restorer = restorerFactory.create(mockGame)
      restorer shouldBe a[MementoRestorer]
    }
  }
}