package de.htwg.se.soccercardclash.module

import com.google.inject._
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import com.google.inject.{AbstractModule, Provides, Singleton}
import de.htwg.se.soccercardclash.controller.IController
import de.htwg.se.soccercardclash.controller.base.Controller
import de.htwg.se.soccercardclash.controller.command.{CommandFactory, ICommandFactory}
import de.htwg.se.soccercardclash.model.cardComponent.dataStructure.*
import de.htwg.se.soccercardclash.model.gameComponent.service.*
import de.htwg.se.soccercardclash.model.gameComponent.action.manager.*
import de.htwg.se.soccercardclash.util.Observable
import de.htwg.se.soccercardclash.model.playerComponent.IPlayer
import de.htwg.se.soccercardclash.model.playerComponent.ai.AIPresetRegistry
import de.htwg.se.soccercardclash.model.playerComponent.util.IRandomProvider

class ControllerModuleSpec extends AnyWordSpec with Matchers {

  "ControllerModule" should {

    val injector = Guice.createInjector(new TestControllerModule)

    "bind IController to Controller singleton" in {
      val controller1 = injector.getInstance(classOf[IController])
      val controller2 = injector.getInstance(classOf[IController])
      controller1 shouldBe a[Controller]
      controller1 shouldBe theSameInstanceAs(controller2)
    }

    "bind Observable to IController" in {
      val observable = injector.getInstance(classOf[Observable])
      val controller = injector.getInstance(classOf[IController])
      observable shouldBe theSameInstanceAs(controller)
    }

    "bind ICommandFactory to CommandFactory" in {
      val factory = injector.getInstance(classOf[ICommandFactory])
      factory shouldBe a[CommandFactory]
    }

    "bind IGameService to a mock" in {
      val gameService = injector.getInstance(classOf[IGameService])
      gameService should not be null
    }

    "bind IActionManager to ActionManager" in {
      val manager = injector.getInstance(classOf[IActionExecutor])
      manager shouldBe a[ActionExecutor]
    }
  }
}
