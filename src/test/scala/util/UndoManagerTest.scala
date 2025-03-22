package util

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import controller.command.ICommand

class UndoManagerTest extends AnyFlatSpec with Matchers {

  "UndoManager" should "add command to undoStack and clear redoStack on doStep" in {
    val undoManager = new UndoManager
    val command1 = new TestCommand()
    val command2 = new TestCommand()

    undoManager.doStep(command1)
    undoManager.doStep(command2)

    // Ensure command2 is on the top of the undoStack
    undoManager.getUndoStack.head should be(command2)

    // Ensure redoStack is cleared
    undoManager.getRedoStack shouldBe Nil
  }

  it should "perform undoStep correctly" in {
    val undoManager = new UndoManager
    val command1 = new TestCommand()

    undoManager.doStep(command1)
    undoManager.undoStep()

    // Ensure the undoStack is empty
    undoManager.getUndoStack shouldBe Nil

    // Ensure the command was undone
    command1.executed shouldBe false

    // Ensure the command is in the redoStack
    undoManager.getRedoStack.head should be(command1)
  }

  it should "perform redoStep correctly" in {
    val undoManager = new UndoManager
    val command1 = new TestCommand()

    undoManager.doStep(command1)
    undoManager.undoStep()
    undoManager.redoStep()

    // Ensure the command is back in the undoStack
    undoManager.getUndoStack.head should be(command1)

    // Ensure the command was redone
    command1.executed shouldBe true

    // Ensure the redoStack is empty
    undoManager.getRedoStack shouldBe Nil
  }

  it should "not perform undoStep or redoStep if there are no commands" in {
    val undoManager = new UndoManager

    undoManager.undoStep()
    undoManager.redoStep()

    // Ensure both undoStack and redoStack are still empty
    undoManager.getUndoStack shouldBe Nil
    undoManager.getRedoStack shouldBe Nil
  }
}

class TestCommand(var executed: Boolean = false) extends ICommand {
  def doStep(): Boolean = {
    executed = true
    true
  }

  def undoStep(): Unit = {
    executed = false
  }

  def redoStep(): Unit = {
    executed = true
  }
}
