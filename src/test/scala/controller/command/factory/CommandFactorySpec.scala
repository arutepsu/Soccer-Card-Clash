package controller.command.factory

import controller.command.actionCommandTypes.attackActionCommands.{DoubleAttackActionCommand, SingleAttackActionCommand}
import controller.command.actionCommandTypes.boostActionCommands.{BoostDefenderActionCommand, BoostGoalkeeperActionCommand}
import controller.command.actionCommandTypes.gameStateCommands.ResetGameCommand
import controller.command.actionCommandTypes.swapActionCommands.{RegularSwapActionCommand, ReverseSwapActionCommand}
import controller.command.base.workflow.{CreateGameWorkflowCommand, LoadGameWorkflowCommand, QuitWorkflowCommand, SaveGameWorkflowCommand}
import model.gameComponent.IGame
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.mockito.MockitoSugar
import org.mockito.Mockito.*
import controller.IController
import controller.command.memento.factory.IMementoManagerFactory

class CommandFactorySpec extends AnyWordSpec with Matchers with MockitoSugar {

  "CommandFactory" should {
    val mockGame = mock[IGame]
    val mockController = mock[IController]
    val mockMementoManagerFactory = mock[IMementoManagerFactory]
    val commandFactory = new CommandFactory(mockGame, mockController, mockMementoManagerFactory)

    "create a SingleAttackActionCommand with correct parameters" in {
      val command = commandFactory.createSingleAttackCommand(2)
      command shouldBe a[SingleAttackActionCommand]
    }

    "create a DoubleAttackActionCommand with correct parameters" in {
      val command = commandFactory.createDoubleAttackCommand(3)
      command shouldBe a[DoubleAttackActionCommand]
    }

    "create a BoostDefenderActionCommand with correct parameters" in {
      val command = commandFactory.createBoostDefenderCommand(1)
      command shouldBe a[BoostDefenderActionCommand]
    }

    "create a BoostGoalkeeperActionCommand" in {
      val command = commandFactory.createBoostGoalkeeperCommand()
      command shouldBe a[BoostGoalkeeperActionCommand]
    }

    "create a RegularSwapActionCommand with correct parameters" in {
      val command = commandFactory.createRegularSwapCommand(4)
      command shouldBe a[RegularSwapActionCommand]
    }

    "create a ReverseSwapActionCommand" in {
      val command = commandFactory.createReverseSwapCommand()
      command shouldBe a[ReverseSwapActionCommand]
    }

    "create a ResetGameCommand" in {
      val command = commandFactory.createResetGameCommand()
      command shouldBe a[ResetGameCommand]
    }

    "create a CreateGameWorkflowCommand with correct parameters" in {
      val command = commandFactory.createCreateGameCommand(mockGame, "Alice", "Bob")
      command shouldBe a[CreateGameWorkflowCommand]
    }

    "create a QuitWorkflowCommand" in {
      val command = commandFactory.createQuitCommand(mockGame)
      command shouldBe a[QuitWorkflowCommand]
    }

    "create a SaveGameWorkflowCommand" in {
      val command = commandFactory.createSaveGameCommand()
      command shouldBe a[SaveGameWorkflowCommand]
    }

    "create a LoadGameWorkflowCommand with correct parameters" in {
      val command = commandFactory.createLoadGameCommand("savefile.dat")
      command shouldBe a[LoadGameWorkflowCommand]
    }
  }
}
