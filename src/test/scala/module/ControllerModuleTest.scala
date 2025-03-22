//package module
//
//import com.google.inject.{Guice, Injector}
//import controller.base.Controller
//import org.scalatest.wordspec.AnyWordSpec
//import org.scalatest.matchers.should.Matchers
//import controller.IController
//import controller.command.factory.*
//import model.gameComponent.base.Game
//import model.gameComponent.IGame
//import util.Observable
//import controller.command.memento.factory.*
//
//class ControllerModuleTest extends AnyWordSpec with Matchers {
//
//  "ControllerModule" should {
//
//    "bind IController to Controller in Singleton scope" in {
//      val injector: Injector = Guice.createInjector(new ControllerModule)
//
//      val controller = injector.getInstance(classOf[IController])
//      controller shouldBe a [Controller] // Ensure it's an instance of Controller
//
//      // Singleton check
//      val controllerSingleton = injector.getInstance(classOf[IController])
//      controllerSingleton shouldBe theSameInstanceAs(controller)
//    }
//
//    "bind Observable to Controller in Singleton scope" in {
//      val injector: Injector = Guice.createInjector(new ControllerModule)
//
//      val observable = injector.getInstance(classOf[Observable])
//      observable shouldBe a [Controller] // Ensure it's an instance of Controller
//
//      // Singleton check
//      val observableSingleton = injector.getInstance(classOf[Observable])
//      observableSingleton shouldBe theSameInstanceAs(observable)
//    }
//
//    "bind ICommandFactory to CommandFactory" in {
//      val injector: Injector = Guice.createInjector(new ControllerModule)
//
//      val commandFactory = injector.getInstance(classOf[ICommandFactory])
//      commandFactory shouldBe a [CommandFactory] // Ensure it's an instance of CommandFactory
//    }
//
//    "bind IGame to Game in Singleton scope" in {
//      val injector: Injector = Guice.createInjector(new ControllerModule)
//
//      val game = injector.getInstance(classOf[IGame])
//      game shouldBe a [Game] // Ensure it's an instance of Game
//
//      // Singleton check
//      val gameSingleton = injector.getInstance(classOf[IGame])
//      gameSingleton shouldBe theSameInstanceAs(game)
//    }
//    "bind IMementoManagerFactory to MementoManagerFactory" in {
//      val injector: Injector = Guice.createInjector(new ControllerModule)
//
//      val mementoManagerFactory = injector.getInstance(classOf[IMementoManagerFactory])
//      mementoManagerFactory shouldBe a [MementoManagerFactory] // Ensure it's an instance of MementoManagerFactory
//    }
//  }
//}