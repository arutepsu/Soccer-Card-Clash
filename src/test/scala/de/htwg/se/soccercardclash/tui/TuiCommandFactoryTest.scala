package de.htwg.se.soccercardclash.tui

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import org.mockito.Mockito._
import de.htwg.se.soccercardclash.controller.IController
import de.htwg.se.soccercardclash.view.tui.tuiCommand.base.ITuiCommand
import de.htwg.se.soccercardclash.view.tui.tuiCommand.factory.TuiCommandFactory
import de.htwg.se.soccercardclash.view.tui.tuiCommand.tuiCommandTypes.*

class TuiCommandFactoryTest extends AnyFlatSpec with Matchers {

  "TuiCommandFactory" should "create AttackTuiCommand" in {
    val controller = mock(classOf[IController])
    val factory = new TuiCommandFactory(controller)

    val cmd = factory.createAttackTuiCommand()

    cmd shouldBe a [AttackTuiCommand]
  }

  it should "create DoubleAttackTuiCommand" in {
    val controller = mock(classOf[IController])
    val factory = new TuiCommandFactory(controller)

    val cmd = factory.createDoubleAttackTuiCommand()

    cmd shouldBe a [DoubleAttackTuiCommand]
  }

  it should "create BoostTuiCommand" in {
    val controller = mock(classOf[IController])
    val factory = new TuiCommandFactory(controller)

    val cmd = factory.createBoostDefenderTuiCommand()

    cmd shouldBe a [BoostTuiCommand]
  }

  it should "create RegularSwapTuiCommand" in {
    val controller = mock(classOf[IController])
    val factory = new TuiCommandFactory(controller)

    val cmd = factory.createRegularSwapTuiCommand()

    cmd shouldBe a [RegularSwapTuiCommand]
  }

  it should "create CreatePlayersNameTuiCommand" in {
    val controller = mock(classOf[IController])
    val factory = new TuiCommandFactory(controller)

    val cmd = factory.createCreatePlayersNameTuiCommand()

    cmd shouldBe a [CreatePlayersNameTuiCommand]
  }

  it should "create StartGameTuiCommand with given names" in {
    val controller = mock(classOf[IController])
    val factory = new TuiCommandFactory(controller)

    val cmd = factory.createStartGameTuiCommand("Alice", "Bob")

    cmd shouldBe a [StartGameTuiCommand]
  }

  it should "create SaveGameTuiCommand" in {
    val controller = mock(classOf[IController])
    val factory = new TuiCommandFactory(controller)

    val cmd = factory.createSaveGameTuiCommand()

    cmd shouldBe a [SaveGameTuiCommand]
  }

  it should "create LoadGameTuiCommand with file name" in {
    val controller = mock(classOf[IController])
    val factory = new TuiCommandFactory(controller)

    val cmd = factory.createLoadGameTuiCommand("save1.xml")

    cmd shouldBe a [LoadGameTuiCommand]
  }

  it should "create UndoTuiCommand as WorkflowTuiCommand" in {
    val controller = mock(classOf[IController])
    val factory = new TuiCommandFactory(controller)

    val cmd = factory.createUndoTuiCommand()

    cmd shouldBe a [WorkflowTuiCommand]
  }

  it should "create RedoTuiCommand as WorkflowTuiCommand" in {
    val controller = mock(classOf[IController])
    val factory = new TuiCommandFactory(controller)

    val cmd = factory.createRedoTuiCommand()

    cmd shouldBe a [WorkflowTuiCommand]
  }

  it should "create ExitTuiCommand as WorkflowTuiCommand" in {
    val controller = mock(classOf[IController])
    val factory = new TuiCommandFactory(controller)

    val cmd = factory.createExitTuiCommand()

    cmd shouldBe a [WorkflowTuiCommand]
  }
}
