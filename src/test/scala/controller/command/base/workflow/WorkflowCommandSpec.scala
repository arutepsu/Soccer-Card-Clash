package controller.command.base.workflow

import model.gameComponent.IGame
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.mockito.MockitoSugar
import org.mockito.Mockito._
import org.mockito.ArgumentMatchers._
import scala.util.{Failure, Success}


// Concrete test subclass to allow instantiation
private class TestWorkflowCommand extends WorkflowCommand {
  override def doStep(): Boolean = true
}

class WorkflowCommandSpec extends AnyWordSpec with Matchers with MockitoSugar {

  "WorkflowCommand" should {
    "have an empty undoStep()" in {
      val command = new TestWorkflowCommand() // ✅ Fix: Use concrete subclass instead
      command.undoStep() // Should do nothing
      succeed // Test passes if no exception is thrown
    }

    "call doStep() when redoStep() is called" in {
      val command = spy(new TestWorkflowCommand()) // ✅ Fix: Use spy() to verify method calls

      command.redoStep()

      verify(command).doStep() // Ensures redoStep() calls doStep()
    }
  }
}

// CreateGameWorkflowCommand Tests
class CreateGameWorkflowCommandSpec extends AnyWordSpec with Matchers with MockitoSugar {

  "CreateGameWorkflowCommand" should {
    "execute successfully when game.createGame() succeeds" in {
      val mockGame = mock[IGame]
      val command = new CreateGameWorkflowCommand(mockGame, "Alice", "Bob")

      doNothing().when(mockGame).createGame("Alice", "Bob") // ✅ Fix: Use doNothing() for void methods

      val result = command.doStep()

      result shouldBe true
      verify(mockGame).createGame("Alice", "Bob")
    }

    "return false when game.createGame() fails" in {
      val mockGame = mock[IGame]
      val command = new CreateGameWorkflowCommand(mockGame, "Alice", "Bob")

      doThrow(new RuntimeException("Error")).when(mockGame).createGame("Alice", "Bob") // ✅ Fix: Use doThrow() for exceptions

      val result = command.doStep()

      result shouldBe false
      verify(mockGame).createGame("Alice", "Bob")
    }
  }
}

// QuitWorkflowCommand Tests
class QuitWorkflowCommandSpec extends AnyWordSpec with Matchers with MockitoSugar {

  "QuitWorkflowCommand" should {
    "execute successfully when game.exit() succeeds" in {
      val mockGame = mock[IGame]
      val command = new QuitWorkflowCommand(mockGame)

      doNothing().when(mockGame).exit() // ✅ Fix: Use doNothing() for void methods

      val result = command.doStep()

      result shouldBe true
      verify(mockGame).exit()
    }

    "return false when game.exit() fails" in {
      val mockGame = mock[IGame]
      val command = new QuitWorkflowCommand(mockGame)

      doThrow(new RuntimeException("Error")).when(mockGame).exit() // ✅ Fix: Use doThrow() for exceptions

      val result = command.doStep()

      result shouldBe false
      verify(mockGame).exit()
    }
  }
}


// SaveGameWorkflowCommand Tests
class SaveGameWorkflowCommandSpec extends AnyWordSpec with Matchers with MockitoSugar {

  "SaveGameWorkflowCommand" should {
    "execute successfully when game.saveGame() succeeds" in {
      val mockGame = mock[IGame]
      val command = new SaveGameWorkflowCommand(mockGame)

      doNothing().when(mockGame).saveGame() // ✅ Fix: Use doNothing() for void methods

      val result = command.doStep()

      result shouldBe true
      verify(mockGame).saveGame()
    }

    "return false when game.saveGame() fails" in {
      val mockGame = mock[IGame]
      val command = new SaveGameWorkflowCommand(mockGame)

      doThrow(new RuntimeException("Error")).when(mockGame).saveGame() // ✅ Fix: Use doThrow() for exceptions

      val result = command.doStep()

      result shouldBe false
      verify(mockGame).saveGame()
    }
  }
}


// LoadGameWorkflowCommand Tests
class LoadGameWorkflowCommandSpec extends AnyWordSpec with Matchers with MockitoSugar {

  "LoadGameWorkflowCommand" should {
    "execute successfully when game.loadGame() succeeds" in {
      val mockGame = mock[IGame]
      val command = new LoadGameWorkflowCommand(mockGame, "savefile.dat")

      doNothing().when(mockGame).loadGame("savefile.dat") // ✅ Fix: Use doNothing() for void methods

      val result = command.doStep()

      result shouldBe true
      verify(mockGame).loadGame("savefile.dat")
    }

    "return false when game.loadGame() fails" in {
      val mockGame = mock[IGame]
      val command = new LoadGameWorkflowCommand(mockGame, "savefile.dat")

      doThrow(new RuntimeException("Error")).when(mockGame).loadGame("savefile.dat") // ✅ Fix: Use doThrow() for exceptions

      val result = command.doStep()

      result shouldBe false
      verify(mockGame).loadGame("savefile.dat")
    }
  }
}