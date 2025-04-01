package de.htwg.se.soccercardclash.tui.tuiCommandTypes

import de.htwg.se.soccercardclash.controller.IController
import de.htwg.se.soccercardclash.util.{Events, Observable}
import de.htwg.se.soccercardclash.view.tui.tuiCommand.tuiCommandTypes.CreatePlayersNameTuiCommand
import org.mockito.Mockito.*
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import org.scalatestplus.mockito.MockitoSugar

import java.io.{ByteArrayOutputStream, PrintStream}
//class CreatePlayersNameTuiCommandTest extends AnyFlatSpec with Matchers with MockitoSugar {
//
//  trait TestController extends IController with de.htwg.se.soccercardclash.util.Observable
//
//  def captureOutput(block: => Unit): String = {
//    val out = new ByteArrayOutputStream()
//    Console.withOut(new PrintStream(out)) { block }
//    out.toString.trim
//  }
//
//  "CreatePlayersNameTuiCommand" should "notify observers on execute" in {
//    val mockController = mock(classOf[TestController])
//    val command = new CreatePlayersNameTuiCommand(mockController)
//
//    command.execute()
//    verify(mockController).notifyObservers(Events.CreatePlayers)
//  }
//
//  it should "handle valid player name input and start game" in {
//    val mockController = mock(classOf[TestController])
//    val command = new CreatePlayersNameTuiCommand(mockController)
//
//    command.execute()
//
//    val output = captureOutput {
//      val result = command.handlePlayerNames("Alice Bob")
//      result shouldBe true
//    }
//
//    output should include("✅ Players set: Alice & Bob")
//    verify(mockController, times(2)).notifyObservers(Events.CreatePlayers)
//  }
//
//  it should "reject input if not waiting for names" in {
//    val mockController = mock(classOf[TestController])
//    val command = new CreatePlayersNameTuiCommand(mockController)
//
//    val output = captureOutput {
//      val result = command.handlePlayerNames("Alice Bob")
//      result shouldBe false
//    }
//
//    output should not include "✅ Players set"
//    verify(mockController, never()).notifyObservers(Events.CreatePlayers)
//  }
//
//  it should "reject invalid input format" in {
//    val mockController = mock(classOf[TestController])
//    val command = new CreatePlayersNameTuiCommand(mockController)
//
//    command.execute()
//
//    val output = captureOutput {
//      val result = command.handlePlayerNames("OnlyOneName")
//      result shouldBe false
//    }
//
//    output should include("❌ Invalid format")
//    verify(mockController, times(1)).notifyObservers(Events.CreatePlayers)
//  }
//}