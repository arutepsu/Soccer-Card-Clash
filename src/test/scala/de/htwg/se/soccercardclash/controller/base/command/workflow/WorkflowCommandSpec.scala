package de.htwg.se.soccercardclash.controller.base.command.workflow

import de.htwg.se.soccercardclash.controller.command.workflow.*
import de.htwg.se.soccercardclash.model.gameComponent.IGameState
import de.htwg.se.soccercardclash.model.gameComponent.service.IGameService
import de.htwg.se.soccercardclash.util.{GameActionEvent, ObservableEvent, SceneSwitchEvent}
import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import org.mockito.Mockito.*
import org.mockito.ArgumentMatchers.*
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatestplus.mockito.MockitoSugar
import scala.util.{Success, Failure}

class TestExitStrategy extends ExitStrategy {
  var wasCalled: Boolean = false

  override def exit(): Unit = wasCalled = true
}

class WorkflowCommandSpec extends AnyFlatSpec with Matchers {

  "CreateGameWorkflowCommand" should "create a new game and return PlayingField event" in {
    val gameService = mock(classOf[IGameService])
    val initialState = mock(classOf[IGameState])
    val newState = mock(classOf[IGameState])

    when(gameService.createNewGame("A", "B")).thenReturn(newState)

    val command = new CreateGameWorkflowCommand(gameService, "A", "B")
    val result = command.execute(initialState)

    result.success shouldBe true
    result.state shouldBe newState
    result.events should contain(SceneSwitchEvent.PlayingField)
  }

  "SaveGameWorkflowCommand" should "return SaveGame event regardless of success" in {
    val gameService = mock(classOf[IGameService])
    val state = mock(classOf[IGameState])

    when(gameService.saveGame(state)).thenReturn(Success(()))

    val command = new SaveGameWorkflowCommand(gameService)
    val result = command.execute(state)

    result.success shouldBe true
    result.state shouldBe state
    result.events should contain(GameActionEvent.SaveGame)
  }

  "QuitWorkflowCommand" should "invoke ExitStrategy and return unchanged state and empty events" in {
    val testExit = new TestExitStrategy
    val initialState = mock(classOf[IGameState])

    val command = new QuitWorkflowCommand(testExit)
    val result = command.execute(initialState)

    result.success shouldBe true
    result.state shouldBe initialState
    result.events shouldBe empty
    testExit.wasCalled shouldBe true
  }
  "LoadGameWorkflowCommand" should "load game and return LoadGame event on success" in {
    val gameService = mock(classOf[IGameService])
    val state = mock(classOf[IGameState])
    val loadedState = mock(classOf[IGameState])

    when(gameService.loadGame("game1")).thenReturn(Success(loadedState))

    val command = new LoadGameWorkflowCommand(gameService, "game1")
    val result = command.execute(state)

    result.success shouldBe true
    result.state shouldBe loadedState
    result.events should contain(SceneSwitchEvent.LoadGame)
  }

  it should "fallback to original state and return empty events on failure" in {
    val gameService = mock(classOf[IGameService])
    val state = mock(classOf[IGameState])

    when(gameService.loadGame("fail.json")).thenReturn(Failure(new Exception("boom")))

    val command = new LoadGameWorkflowCommand(gameService, "fail.json")
    val result = command.execute(state)

    result.success shouldBe true
    result.state shouldBe state
    result.events shouldBe empty
  }
  
}
