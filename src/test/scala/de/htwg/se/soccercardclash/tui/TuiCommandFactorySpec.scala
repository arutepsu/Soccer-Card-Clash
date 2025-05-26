package de.htwg.se.soccercardclash.tui
import de.htwg.se.soccercardclash.controller.IController
import de.htwg.se.soccercardclash.controller.command.actionCommandTypes.boostActionCommands.BoostDefenderActionCommand
import de.htwg.se.soccercardclash.util.IGameContextHolder
import de.htwg.se.soccercardclash.view.tui.IPrompter
import de.htwg.se.soccercardclash.view.tui.tuiCommand.factory.*
import de.htwg.se.soccercardclash.view.tui.tuiCommand.base.ITuiCommand
import de.htwg.se.soccercardclash.view.tui.tuiCommand.tuiCommandTypes.*
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import org.mockito.Mockito.*
import org.scalatest.wordspec.AnyWordSpec

class TuiCommandFactorySpec extends AnyWordSpec with Matchers {

  "TuiCommandFactory" should {
    val controller = mock(classOf[IController])
    val context = mock(classOf[IGameContextHolder])
    val prompter = mock(classOf[IPrompter])
    val factory = new TuiCommandFactory(controller, context, prompter)


    "create CreatePlayersNameTuiCommand" in {
      val cmd = factory.createCreatePlayersNameTuiCommand()
      cmd shouldBe a[CreatePlayersNameTuiCommand]
    }
    "create StartGameTuiCommand with names" in {
      val cmd = factory.createStartGameTuiCommand("Alice", "Bob")
      cmd shouldBe a[StartGameTuiCommand]
    }

    "create StartGameWithAITuiCommand with names" in {
      val cmd = factory.createStartGameWithAITuiCommand("Human", "AI")
      cmd shouldBe a[StartGameTuiCommandWithAI]
    }
    "create UndoTuiCommand" in {
      val cmd = factory.createUndoTuiCommand()
      cmd shouldBe a[WorkflowTuiCommand]
    }

    "create RedoTuiCommand" in {
      val cmd = factory.createRedoTuiCommand()
      cmd shouldBe a[WorkflowTuiCommand]
    }

    "create ExitTuiCommand" in {
      val cmd = factory.createExitTuiCommand()
      cmd shouldBe a[WorkflowTuiCommand]
    }
    "create LoadGameTuiCommand" in {
      val cmd = factory.createLoadGameTuiCommand("save1.xml")
      cmd shouldBe a[LoadGameTuiCommand]
    }

    "create LoadSelectedGameTuiCommand" in {
      val cmd = factory.createLoadSelectedGameTuiCommand(1)
      cmd shouldBe a[LoadSelectedGameTuiCommand]
    }

    "create ShowAvailableGamesTuiCommand" in {
      val cmd = factory.createShowGamesTuiCommand()
      cmd shouldBe a[ShowAvailableGamesTuiCommand]
    }

    "create SaveGameTuiCommand" in {
      val cmd = factory.createSaveGameTuiCommand()
      cmd shouldBe a[SaveGameTuiCommand]
    }
    "create AttackTuiCommand" in {
      factory.createSingleAttackTuiCommand() shouldBe a[AttackTuiCommand]
    }

    "create DoubleAttackTuiCommand" in {
      factory.createDoubleAttackTuiCommand() shouldBe a[DoubleAttackTuiCommand]
    }

    "create BoostDefenderTuiCommand" in {
      factory.createBoostDefenderTuiCommand() shouldBe a[BoostDefenderTuiCommand]
    }

    "create BoostGoalkeeperTuiCommand" in {
      factory.createBoostGoalkeeperTuiCommand() shouldBe a[BoostGoalkeeperTuiCommand]
    }

    "create RegularSwapTuiCommand" in {
      factory.createRegularSwapTuiCommand() shouldBe a[RegularSwapTuiCommand]
    }

    "create ReverseSwapTuiCommand" in {
      factory.createReverseSwapTuiCommand() shouldBe a[ReverseSwapTuiCommand]
    }
  }
}