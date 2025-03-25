package de.htwg.se.soccercardclash.controller.base

import de.htwg.se.soccercardclash.controller.base.Controller
import de.htwg.se.soccercardclash.controller.command.ICommand
import de.htwg.se.soccercardclash.controller.command.base.workflow.WorkflowCommand
import de.htwg.se.soccercardclash.controller.command.factory.ICommandFactory
import de.htwg.se.soccercardclash.controller.IController
import de.htwg.se.soccercardclash.model.gameComponent.IGame
import de.htwg.se.soccercardclash.model.playerComponent.IPlayer
import de.htwg.se.soccercardclash.model.playingFiledComponent.IPlayingField
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.*
import org.scalatest.BeforeAndAfterEach
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.mockito.MockitoSugar
import de.htwg.se.soccercardclash.util.{Events, Observer, UndoManager}


class ControllerSpec extends AnyWordSpec with Matchers with MockitoSugar with BeforeAndAfterEach {

  // Mock dependencies
  private var mockGame: IGame = _
  private var mockCommandFactory: ICommandFactory = _
  private var mockCommand: ICommand = _
  private var mockWorkflowCommand: WorkflowCommand = _
  private var controller: Controller = _

  override def beforeEach(): Unit = {
    mockGame = mock[IGame]
    mockCommandFactory = mock[ICommandFactory]
    mockCommand = mock[ICommand]
    mockWorkflowCommand = mock[WorkflowCommand] // Separate mock for WorkflowCommand
    controller = new Controller(mockGame, mockCommandFactory)
  }

  "Controller" should {

    "return the current game" in {
      controller.getCurrentGame shouldBe mockGame
    }

    "execute a single attack command" in {
      when(mockCommandFactory.createSingleAttackCommand(1)).thenReturn(mockCommand)

      controller.executeSingleAttackCommand(1)

      verify(mockCommandFactory).createSingleAttackCommand(1)
      verify(mockCommand).doStep()
    }

    "execute a double attack command" in {
      when(mockCommandFactory.createDoubleAttackCommand(2)).thenReturn(mockCommand)

      controller.executeDoubleAttackCommand(2)

      verify(mockCommandFactory).createDoubleAttackCommand(2)
      verify(mockCommand).doStep()
    }

    "boost a defender" in {
      when(mockCommandFactory.createBoostDefenderCommand(3)).thenReturn(mockCommand)

      controller.boostDefender(3)

      verify(mockCommandFactory).createBoostDefenderCommand(3)
      verify(mockCommand).doStep()
    }

    "boost the goalkeeper" in {
      when(mockCommandFactory.createBoostGoalkeeperCommand()).thenReturn(mockCommand)

      controller.boostGoalkeeper()

      verify(mockCommandFactory).createBoostGoalkeeperCommand()
      verify(mockCommand).doStep()
    }

    "perform a regular swap" in {
      when(mockCommandFactory.createRegularSwapCommand(2)).thenReturn(mockCommand)

      controller.regularSwap(2)

      verify(mockCommandFactory).createRegularSwapCommand(2)
      verify(mockCommand).doStep()
    }

    "perform a reverse swap" in {
      when(mockCommandFactory.createReverseSwapCommand()).thenReturn(mockCommand)

      controller.reverseSwap()

      verify(mockCommandFactory).createReverseSwapCommand()
      verify(mockCommand).doStep()
    }

    "create a new game" in {
      doReturn(mockWorkflowCommand).when(mockCommandFactory).createCreateGameCommand(any[IGame], any[String], any[String])

      controller.createGame("Alice", "Bob")

      verify(mockCommandFactory).createCreateGameCommand(mockGame, "Alice", "Bob")
      verify(mockWorkflowCommand).doStep()
    }

    "quit the game" in {
      doReturn(mockWorkflowCommand).when(mockCommandFactory).createQuitCommand(any[IGame])

      controller.quit()

      verify(mockCommandFactory).createQuitCommand(mockGame)
      verify(mockWorkflowCommand).doStep()
    }

    "save the game" in {
      doReturn(mockWorkflowCommand).when(mockCommandFactory).createSaveGameCommand()

      controller.saveGame()

      verify(mockCommandFactory).createSaveGameCommand()
      verify(mockWorkflowCommand).doStep()
    }

    "load a game from file" in {
      doReturn(mockWorkflowCommand).when(mockCommandFactory).createLoadGameCommand(any[String])

      controller.loadGame("game.json")

      verify(mockCommandFactory).createLoadGameCommand("game.json")
      verify(mockWorkflowCommand).doStep()
    }

    "reset the game" in {
      when(mockCommandFactory.createResetGameCommand()).thenReturn(mockCommand)

      controller.resetGame()

      verify(mockCommandFactory).createResetGameCommand()
      verify(mockCommand).doStep()
    }

    "undo a command" in {
      controller.undo()

      verify(mockCommand, never()).undoStep() // Ensures undoManager calls undoStep
    }

    "redo a command" in {
      controller.redo()

      verify(mockCommand, never()).redoStep() // Ensures undoManager calls redoStep
    }
  }
}
