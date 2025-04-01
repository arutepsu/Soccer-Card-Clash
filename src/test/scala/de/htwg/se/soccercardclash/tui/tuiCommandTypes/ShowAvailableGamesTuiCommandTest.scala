package de.htwg.se.soccercardclash.tui.tuiCommandTypes

import de.htwg.se.soccercardclash.controller.IController
import de.htwg.se.soccercardclash.view.tui.IPrompter
import de.htwg.se.soccercardclash.view.tui.tuiCommand.factory.ITuiCommandFactory
import de.htwg.se.soccercardclash.view.tui.tuiCommand.tuiCommandTypes.ShowAvailableGamesTuiCommand
import de.htwg.se.soccercardclash.view.tui.tuiCommand.base.ITuiCommand
import de.htwg.se.soccercardclash.model.gameComponent.IGame
import org.mockito.Mockito._
import org.mockito.ArgumentMatchers.anyInt
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import org.scalatestplus.mockito.MockitoSugar

import java.io.{ByteArrayOutputStream, PrintStream}

class ShowAvailableGamesTuiCommandTest extends AnyFlatSpec with Matchers with MockitoSugar {

  def captureOutput(block: => Unit): String = {
    val out = new ByteArrayOutputStream()
    Console.withOut(new PrintStream(out)) { block }
    out.toString.trim
  }

  "ShowAvailableGamesTuiCommand" should "create LoadSelectedGameTuiCommand when input is valid" in {
    val mockController = mock[IController]
    val mockPrompter = mock[IPrompter]
    val mockFactory = mock[ITuiCommandFactory]

    val command = new ShowAvailableGamesTuiCommand(mockController, mockPrompter, mockFactory)

    val output = captureOutput {
      command.execute(Some("select 2"))
    }

    verify(mockFactory).createLoadSelectedGameTuiCommand(1)
    output should not include "❌"
  }

  it should "print usage error for invalid select format" in {
    val mockController = mock[IController]
    val mockPrompter = mock[IPrompter]
    val mockFactory = mock[ITuiCommandFactory]

    val command = new ShowAvailableGamesTuiCommand(mockController, mockPrompter, mockFactory)

    val output = captureOutput {
      command.execute(Some("select notANumber"))
    }

    output should include("❌ Usage: select <number>")
    verify(mockFactory, never()).createLoadSelectedGameTuiCommand(anyInt())
  }

  it should "show info if a game is already loaded" in {
    val mockController = mock[IController]
    val mockPrompter = mock[IPrompter]
    val mockFactory = mock[ITuiCommandFactory]

    val mockGame = mock[IGame] // ✅ Correct type
    when(mockController.getCurrentGame).thenReturn(mockGame)

    val command = new ShowAvailableGamesTuiCommand(mockController, mockPrompter, mockFactory)

    val output = captureOutput {
      command.execute(None)
    }

    output should include("ℹ️ A game is already loaded.")
    output should include("🎮 Showing available saved games:")
    verify(mockPrompter).showAvailableGames()
  }


  it should "just show games if no game is loaded" in {
    val mockController = mock[IController]
    val mockPrompter = mock[IPrompter]
    val mockFactory = mock[ITuiCommandFactory]

    when(mockController.getCurrentGame).thenReturn(null)

    val command = new ShowAvailableGamesTuiCommand(mockController, mockPrompter, mockFactory)

    val output = captureOutput {
      command.execute(None)
    }

    output should include("🎮 Showing available saved games:")
    output should not include "ℹ️"
    verify(mockPrompter).showAvailableGames()
  }
}
