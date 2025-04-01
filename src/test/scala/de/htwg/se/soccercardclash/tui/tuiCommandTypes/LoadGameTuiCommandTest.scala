package de.htwg.se.soccercardclash.tui.tuiCommandTypes

//import de.htwg.se.soccercardclash.controller.IController
//import de.htwg.se.soccercardclash.util.{Events, Observable, ObservableEvent}
//import de.htwg.se.soccercardclash.view.tui.tuiCommand.tuiCommandTypes.LoadGameTuiCommand
//import org.scalatest.flatspec.AnyFlatSpec
//import org.scalatest.matchers.should.Matchers
//
//import java.io.{ByteArrayOutputStream, PrintStream}
//
//class LoadGameTuiCommandTest extends AnyFlatSpec with Matchers {
//
//  // ✅ Custom test controller (no mocking Observable!)
//  class TestController extends IController with Observable {
//    var loadedFile: Option[String] = None
//    var observerNotified: Boolean = false
//
//    override def loadGame(fileName: String): Unit = {
//      loadedFile = Some(fileName)
//    }
//
//    override def notifyObservers(e: ObservableEvent): Unit = {
//      observerNotified = true
//      super.notifyObservers(e)
//    }
//  }
//
//  // ✅ Capture println output
//  def captureOutput(block: => Unit): String = {
//    val out = new ByteArrayOutputStream()
//    Console.withOut(new PrintStream(out)) { block }
//    out.toString.trim
//  }
//
//  "LoadGameTuiCommand" should "load a game and notify observers on success" in {
//    val testController = new TestController
//    val fileName = "savegame.xml"
//    val command = new LoadGameTuiCommand(testController, fileName)
//
//    val output = captureOutput {
//      command.execute()
//    }
//
//    testController.loadedFile shouldBe Some(fileName)
//    testController.observerNotified shouldBe true
//    output should include(s"✅ Game '$fileName' loaded successfully.")
//  }
//
//  it should "print error message if loadGame throws exception" in {
//    // Subclass that simulates failure
//    class FailingController extends TestController {
//      override def loadGame(fileName: String): Unit =
//        throw new RuntimeException("load failed")
//    }
//
//    val controller = new FailingController
//    val fileName = "broken_save.json"
//    val command = new LoadGameTuiCommand(controller, fileName)
//
//    val output = captureOutput {
//      command.execute()
//    }
//
//    output should include(s"❌ ERROR: Failed to load game '$fileName'")
//    output should include("load failed")
//    controller.observerNotified shouldBe false
//  }
//}
