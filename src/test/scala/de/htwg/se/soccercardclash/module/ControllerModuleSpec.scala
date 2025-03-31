package de.htwg.se.soccercardclash.module

import com.google.inject.{AbstractModule, Guice}
import de.htwg.se.soccercardclash.controller.IController
import de.htwg.se.soccercardclash.controller.base.Controller
import de.htwg.se.soccercardclash.controller.command.factory.{CommandFactory, ICommandFactory}
import de.htwg.se.soccercardclash.model.gameComponent.IGame
import de.htwg.se.soccercardclash.util.Observable
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.mockito.Mockito.mock
import de.htwg.se.soccercardclash.model.gameComponent.state.memento.factory.IMementoManagerFactory

class ControllerModuleSpec extends AnyWordSpec with Matchers {

  // Dummy bindings for missing dependencies
  class TestOverrides extends AbstractModule {
    override def configure(): Unit = {
      bind(classOf[IGame]).toInstance(mock(classOf[IGame]))
      bind(classOf[IMementoManagerFactory]).toInstance(mock(classOf[IMementoManagerFactory]))
    }
  }

  "ControllerModule" should {
    val injector = Guice.createInjector(new ControllerModule, new TestOverrides)

    "bind IController to Controller" in {
      val controller = injector.getInstance(classOf[IController])
      controller shouldBe a[Controller]
    }

    "bind Observable to Controller" in {
      val observable = injector.getInstance(classOf[Observable])
      observable shouldBe a[Controller]
    }

    "bind ICommandFactory to CommandFactory" in {
      val factory = injector.getInstance(classOf[ICommandFactory])
      factory shouldBe a[CommandFactory]
    }

    "return same instance for Observable and IController (singleton)" in {
      val controller = injector.getInstance(classOf[IController])
      val observable = injector.getInstance(classOf[Observable])
      controller shouldBe theSameInstanceAs(observable)

    }
  }
}
