package de.htwg.se.soccercardclash.tui

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import org.mockito.Mockito.*
import de.htwg.se.soccercardclash.controller.IController
import de.htwg.se.soccercardclash.view.tui.tuiCommand.base.ITuiCommand
import de.htwg.se.soccercardclash.view.tui.tuiCommand.*
import de.htwg.se.soccercardclash.view.tui.tuiCommand.factory.*
import de.htwg.se.soccercardclash.view.tui.tuiCommand.tuiCommandTypes.*
import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import org.mockito.Mockito.*
import org.mockito.ArgumentMatchers.any
import org.scalatestplus.mockito.MockitoSugar
import de.htwg.se.soccercardclash.controller.IController
import de.htwg.se.soccercardclash.view.tui.IPrompter

class TuiCommandFactorySpec extends AnyWordSpec with Matchers with MockitoSugar {

  "TuiCommandFactory" should {
    val mockController = mock[IController]
    val mockPrompter = mock[IPrompter]
    val factory: ITuiCommandFactory = new TuiCommandFactory(mockController, mockPrompter)

    "create an AttackTuiCommand" in {
      factory.createSingleAttackTuiCommand() shouldBe a[AttackTuiCommand]
    }

    "create a DoubleAttackTuiCommand" in {
      factory.createDoubleAttackTuiCommand() shouldBe a[DoubleAttackTuiCommand]
    }

    "create a BoostDefenderTuiCommand" in {
      factory.createBoostDefenderTuiCommand() shouldBe a[BoostDefenderTuiCommand]
    }

    "create a BoostGoalkeeperTuiCommand" in {
      factory.createBoostGoalkeeperTuiCommand() shouldBe a[BoostGoalkeeperTuiCommand]
    }

    "create a RegularSwapTuiCommand" in {
      factory.createRegularSwapTuiCommand() shouldBe a[RegularSwapTuiCommand]
    }

    "create a ReverseSwapTuiCommand" in {
      factory.createReverseSwapTuiCommand() shouldBe a[ReverseSwapTuiCommand]
    }

    "create a CreatePlayersNameTuiCommand" in {
      factory.createCreatePlayersNameTuiCommand() shouldBe a[CreatePlayersNameTuiCommand]
    }

    "create a StartGameTuiCommand with given player names" in {
      factory.createStartGameTuiCommand("Alice", "Bob") shouldBe a[StartGameTuiCommand]
    }

    "create a SaveGameTuiCommand" in {
      factory.createSaveGameTuiCommand() shouldBe a[SaveGameTuiCommand]
    }

    "create a LoadGameTuiCommand with given file name" in {
      factory.createLoadGameTuiCommand("game1.json") shouldBe a[LoadGameTuiCommand]
    }

    "create a LoadSelectedGameTuiCommand with given index" in {
      factory.createLoadSelectedGameTuiCommand(2) shouldBe a[LoadSelectedGameTuiCommand]
    }

    "create a ShowAvailableGamesTuiCommand" in {
      factory.createShowGamesTuiCommand() shouldBe a[ShowAvailableGamesTuiCommand]
    }

    "create an Undo WorkflowTuiCommand" in {
      factory.createUndoTuiCommand() shouldBe a[WorkflowTuiCommand]
    }

    "create a Redo WorkflowTuiCommand" in {
      factory.createRedoTuiCommand() shouldBe a[WorkflowTuiCommand]
    }

    "create an Exit WorkflowTuiCommand" in {
      factory.createExitTuiCommand() shouldBe a[WorkflowTuiCommand]
    }
  }
}
