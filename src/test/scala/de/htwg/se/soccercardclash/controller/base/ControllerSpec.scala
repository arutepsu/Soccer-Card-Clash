package de.htwg.se.soccercardclash.controller.base

import de.htwg.se.soccercardclash.controller.command.{CommandResult, ICommand, ICommandFactory}
import de.htwg.se.soccercardclash.controller.IController
import de.htwg.se.soccercardclash.model.cardComponent.dataStructure.IHandCardsQueueFactory
import de.htwg.se.soccercardclash.model.playerComponent.IPlayer
import de.htwg.se.soccercardclash.model.playerComponent.base.Player
import de.htwg.se.soccercardclash.model.gameComponent.state.IGameState
import de.htwg.se.soccercardclash.model.gameComponent.state.base.GameState
import de.htwg.se.soccercardclash.model.gameComponent.action.manager.IActionManager
import de.htwg.se.soccercardclash.model.gameComponent.service.IGameService
import de.htwg.se.soccercardclash.model.playerComponent.playerAction.{CanPerformAction, OutOfActions, PlayerActionPolicies}
import de.htwg.se.soccercardclash.util.{EventDispatcher, IGameContextHolder, Observable, ObservableEvent, Observer, UndoManager}
import de.htwg.se.soccercardclash.model.gameComponent.context.GameContext
import de.htwg.se.soccercardclash.model.gameComponent.state.components.Roles
import de.htwg.se.soccercardclash.model.playerComponent.base.AI
import de.htwg.se.soccercardclash.util.*
import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import org.mockito.Mockito.*
import org.mockito.ArgumentMatchers.*
import org.scalatest.matchers.should.Matchers.shouldBe
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatestplus.mockito.MockitoSugar

import scala.util.{Success, Try, Failure}

class ControllerSpec extends AnyFlatSpec with MockitoSugar {

  val mockCommandFactory = mock[ICommandFactory]
  val mockGameService = mock[IGameService]
  val mockContextHolder = mock[IGameContextHolder]

  val controller = new Controller(mockCommandFactory, mockGameService, mockContextHolder)

  "singleAttack" should "run the correct command and update context" in {
    val initialState = mock[IGameState]
    val ctx = GameContext(initialState, new UndoManager)
    val mockCommand = mock[ICommand]
    val updatedState = mock[IGameState]

    val expectedResult = CommandResult(success = true, updatedState, List(GameActionEvent.RegularAttack))

    when(mockCommandFactory.createSingleAttackCommand(0)).thenReturn(mockCommand)
    when(mockCommand.execute(initialState)).thenReturn(expectedResult)

    val (newCtx, success) = controller.singleAttack(0, ctx)

    assert(success)
    assert(newCtx.state eq updatedState)
    verify(mockContextHolder).set(newCtx)
  }

  "doubleAttack" should "run the correct command and update context on success" in {
    val initialState = mock[IGameState]
    val updatedState = mock[IGameState]
    val ctx = GameContext(initialState, new UndoManager)
    val mockCommand = mock[ICommand]

    val result = CommandResult(success = true, updatedState, List(GameActionEvent.DoubleAttack))

    when(mockCommandFactory.createDoubleAttackCommand(1)).thenReturn(mockCommand)
    when(mockCommand.execute(initialState)).thenReturn(result)

    val (newCtx, success) = controller.doubleAttack(1, ctx)

    assert(success)
    assert(newCtx.state eq updatedState)
    verify(mockContextHolder).set(newCtx)
  }

  "doubleAttack fallback" should "dispatch NoDoubleAttacksEvent when command fails" in {
    val attacker = mock[IPlayer]
    val state = mock[IGameState]
    val ctx = GameContext(state, new UndoManager)

    val mockCommand = mock[ICommand]
    val result = CommandResult(success = false, state, Nil)

    when(mockCommandFactory.createDoubleAttackCommand(1)).thenReturn(mockCommand)
    when(mockCommand.execute(state)).thenReturn(result)
    when(state.getRoles).thenReturn(Roles(attacker, mock[IPlayer]))

    val (newCtx, success) = controller.doubleAttack(1, ctx)

    assert(!success)
    assert(newCtx == ctx)

  }

  "createGame" should "set context with new game" in {
    val state = mock[IGameState]
    when(mockGameService.createNewGame("Alice", "Bob")).thenReturn(state)

    controller.createGame("Alice", "Bob")

    verify(mockContextHolder).set(argThat(_.state == state))
  }

  "createGameWithAI" should "set context and dispatch AI event" in {
    val state = mock[IGameState]
    when(mockGameService.createNewGameWithAI("Alice", "Taka")).thenReturn(state)

    controller.createGameWithAI("Alice", "Taka")

    verify(mockContextHolder).set(argThat(_.state == state))
  }

  "undo" should "update context state and dispatch Undo event" in {
    val state = mock[IGameState]
    val newState = mock[IGameState]
    val ctx = GameContext(state, new UndoManager)
    val events = List(GameActionEvent.Undo)

    val undoManagerSpy = spy(ctx.undoManager)
    when(undoManagerSpy.undoStep(state)).thenReturn((newState, events))

    val updatedCtx = controller.undo(ctx.copy(undoManager = undoManagerSpy))

    assert(updatedCtx.state eq newState)
    verify(mockContextHolder).set(updatedCtx)
  }

  "redo" should "update context state and dispatch Redo event" in {
    val state = mock[IGameState]
    val newState = mock[IGameState]
    val ctx = GameContext(state, new UndoManager)
    val events = List(GameActionEvent.Redo)

    val undoManagerSpy = spy(ctx.undoManager)
    when(undoManagerSpy.redoStep(state)).thenReturn((newState, events))

    val updatedCtx = controller.redo(ctx.copy(undoManager = undoManagerSpy))

    assert(updatedCtx.state eq newState)
    verify(mockContextHolder).set(updatedCtx)
  }

  "regularSwap" should "run the correct command and update context" in {
    val initialState = mock[IGameState]
    val updatedState = mock[IGameState]
    val ctx = GameContext(initialState, new UndoManager)
    val mockCommand = mock[ICommand]

    val result = CommandResult(success = true, updatedState, List(GameActionEvent.RegularSwap))

    when(mockCommandFactory.createRegularSwapCommand(1)).thenReturn(mockCommand)
    when(mockCommand.execute(initialState)).thenReturn(result)

    val (newCtx, success) = controller.regularSwap(1, ctx)

    assert(success)
    assert(newCtx.state eq updatedState)
    verify(mockContextHolder).set(newCtx)
  }

  "regularSwap fallback" should "dispatch NoSwapsEvent when command fails" in {
    val attacker = mock[IPlayer]
    val state = mock[IGameState]
    val ctx = GameContext(state, new UndoManager)
    val mockCommand = mock[ICommand]

    val result = CommandResult(success = false, state, Nil)

    when(mockCommandFactory.createRegularSwapCommand(2)).thenReturn(mockCommand)
    when(mockCommand.execute(state)).thenReturn(result)
    when(state.getRoles).thenReturn(Roles(attacker, mock[IPlayer]))

    val (newCtx, success) = controller.regularSwap(2, ctx)

    assert(!success)
    assert(newCtx == ctx)

  }

  "reverseSwap" should "run the correct command and update context" in {
    val initialState = mock[IGameState]
    val updatedState = mock[IGameState]
    val ctx = GameContext(initialState, new UndoManager)
    val mockCommand = mock[ICommand]

    val result = CommandResult(success = true, updatedState, List(GameActionEvent.ReverseSwap))

    when(mockCommandFactory.createReverseSwapCommand()).thenReturn(mockCommand)
    when(mockCommand.execute(initialState)).thenReturn(result)

    val (newCtx, success) = controller.reverseSwap(ctx)

    assert(success)
    assert(newCtx.state eq updatedState)
    verify(mockContextHolder).set(newCtx)
  }

  "reverseSwap fallback" should "dispatch NoSwapsEvent when command fails" in {
    val attacker = mock[IPlayer]
    val state = mock[IGameState]
    val ctx = GameContext(state, new UndoManager)
    val mockCommand = mock[ICommand]

    val result = CommandResult(success = false, state, Nil)

    when(mockCommandFactory.createReverseSwapCommand()).thenReturn(mockCommand)
    when(mockCommand.execute(state)).thenReturn(result)
    when(state.getRoles).thenReturn(Roles(attacker, mock[IPlayer]))

    val (newCtx, success) = controller.reverseSwap(ctx)

    assert(!success)
    assert(newCtx == ctx)

  }

  "boostDefender" should "run the correct command and update context" in {
    val initialState = mock[IGameState]
    val updatedState = mock[IGameState]
    val ctx = GameContext(initialState, new UndoManager)
    val mockCommand = mock[ICommand]

    val result = CommandResult(success = true, updatedState, List(GameActionEvent.BoostDefender))

    when(mockCommandFactory.createBoostDefenderCommand(0)).thenReturn(mockCommand)
    when(mockCommand.execute(initialState)).thenReturn(result)

    val (newCtx, success) = controller.boostDefender(0, ctx)

    assert(success)
    assert(newCtx.state eq updatedState)
    verify(mockContextHolder).set(newCtx)
  }


  "boostDefender fallback" should "dispatch NoBoostsEvent when command fails" in {
    val attacker = mock[IPlayer]
    val state = mock[IGameState]
    val ctx = GameContext(state, new UndoManager)
    val mockCommand = mock[ICommand]

    val result = CommandResult(success = false, state, Nil)

    when(mockCommandFactory.createBoostDefenderCommand(1)).thenReturn(mockCommand)
    when(mockCommand.execute(state)).thenReturn(result)
    when(state.getRoles).thenReturn(Roles(attacker, mock[IPlayer]))

    val (newCtx, success) = controller.boostDefender(1, ctx)

    assert(!success)
    assert(newCtx == ctx)

  }

  "boostGoalkeeper" should "run the correct command and update context" in {
    val initialState = mock[IGameState]
    val updatedState = mock[IGameState]
    val ctx = GameContext(initialState, new UndoManager)
    val mockCommand = mock[ICommand]

    val result = CommandResult(success = true, updatedState, List(GameActionEvent.BoostGoalkeeper))

    when(mockCommandFactory.createBoostGoalkeeperCommand()).thenReturn(mockCommand)
    when(mockCommand.execute(initialState)).thenReturn(result)

    val (newCtx, success) = controller.boostGoalkeeper(ctx)

    assert(success)
    assert(newCtx.state eq updatedState)
    verify(mockContextHolder).set(newCtx)
  }
  "boostGoalkeeper fallback" should "dispatch NoBoostsEvent when command fails" in {
    val attacker = mock[IPlayer]
    val state = mock[IGameState]
    val ctx = GameContext(state, new UndoManager)
    val mockCommand = mock[ICommand]

    val result = CommandResult(success = false, state, Nil)

    when(mockCommandFactory.createBoostGoalkeeperCommand()).thenReturn(mockCommand)
    when(mockCommand.execute(state)).thenReturn(result)
    when(state.getRoles).thenReturn(Roles(attacker, mock[IPlayer]))

    val (newCtx, success) = controller.boostGoalkeeper(ctx)

    assert(!success)
    assert(newCtx == ctx)

  }


  "loadGame" should "return false and not update context when game loading fails" in {
    val mockGameService = mock[IGameService]
    val mockContextHolder = mock[IGameContextHolder]
    val mockCommandFactory = mock[ICommandFactory]
    val controller = new Controller(mockCommandFactory, mockGameService, mockContextHolder)

    when(mockGameService.loadGame("invalidGame"))
      .thenReturn(Failure(new Exception("Load failed")): Try[IGameState])

    val result = controller.loadGame("invalidGame")

    result shouldBe false
    verify(mockContextHolder, never()).set(any[GameContext])
  }



  "saveGame" should "return true and dispatch SaveGame event if successful" in {
    val state = mock[IGameState]
    val ctx = GameContext(state, new UndoManager)

    when(mockGameService.saveGame(state)).thenReturn(Success(()))

    val result = controller.saveGame(ctx)

    assert(result)
  }

  "saveGame" should "return false and not dispatch event if unsuccessful" in {
    val state = mock[IGameState]
    val ctx = GameContext(state, new UndoManager)

    when(mockGameService.saveGame(state)).thenReturn(Try(throw new Exception("fail")))

    val result = controller.saveGame(ctx)

    assert(!result)
  }


  "executeAIAction" should "call singleAttack for SingleAttackAIAction" in {
    val ctx = GameContext(mock[IGameState], new UndoManager)
    val spyController = spy(controller)
    val expectedCtx = ctx.copy()

    doReturn((expectedCtx, true)).when(spyController).singleAttack(1, ctx)

    val result = spyController.executeAIAction(SingleAttackAIAction(1), ctx)

    assert(result._1 eq expectedCtx)
    assert(result._2)
    verify(spyController).singleAttack(1, ctx)
  }

  it should "call doubleAttack for DoubleAttackAIAction" in {
    val ctx = GameContext(mock[IGameState], new UndoManager)
    val spyController = spy(controller)
    val expectedCtx = ctx.copy()

    doReturn((expectedCtx, true)).when(spyController).doubleAttack(2, ctx)

    val result = spyController.executeAIAction(DoubleAttackAIAction(2), ctx)

    assert(result._1 eq expectedCtx)
    assert(result._2)
    verify(spyController).doubleAttack(2, ctx)
  }

  it should "call regularSwap for RegularSwapAIAction" in {
    val ctx = GameContext(mock[IGameState], new UndoManager)
    val spyController = spy(controller)
    val expectedCtx = ctx.copy()

    doReturn((expectedCtx, true)).when(spyController).regularSwap(0, ctx)

    val result = spyController.executeAIAction(RegularSwapAIAction(0), ctx)

    assert(result._1 eq expectedCtx)
    verify(spyController).regularSwap(0, ctx)
  }

  it should "call reverseSwap for ReverseSwapAIAction" in {
    val ctx = GameContext(mock[IGameState], new UndoManager)
    val spyController = spy(controller)
    val expectedCtx = ctx.copy()

    doReturn((expectedCtx, true)).when(spyController).reverseSwap(ctx)

    val result = spyController.executeAIAction(ReverseSwapAIAction, ctx)

    assert(result._1 eq expectedCtx)
    verify(spyController).reverseSwap(ctx)
  }

  it should "call boostDefender for BoostAIAction with DefenderZone" in {
    val ctx = GameContext(mock[IGameState], new UndoManager)
    val spyController = spy(controller)
    val expectedCtx = ctx.copy()

    doReturn((expectedCtx, true)).when(spyController).boostDefender(3, ctx)

    val result = spyController.executeAIAction(BoostAIAction(3, DefenderZone), ctx)

    assert(result._1 eq expectedCtx)
    verify(spyController).boostDefender(3, ctx)
  }

  it should "call boostGoalkeeper for BoostAIAction with GoalkeeperZone" in {
    val ctx = GameContext(mock[IGameState], new UndoManager)
    val spyController = spy(controller)
    val expectedCtx = ctx.copy()

    doReturn((expectedCtx, true)).when(spyController).boostGoalkeeper(ctx)

    val result = spyController.executeAIAction(BoostAIAction(0, GoalkeeperZone), ctx)

    assert(result._1 eq expectedCtx)
    verify(spyController).boostGoalkeeper(ctx)
  }

  it should "call undo and return true for UndoAIAction" in {
    val ctx = GameContext(mock[IGameState], new UndoManager)
    val spyController = spy(controller)
    val expectedCtx = ctx.copy()

    doReturn(expectedCtx).when(spyController).undo(ctx)

    val result = spyController.executeAIAction(UndoAIAction, ctx)

    assert(result._1 eq expectedCtx)
    assert(result._2)
    verify(spyController).undo(ctx)
  }

  it should "call redo and return true for RedoAIAction" in {
    val ctx = GameContext(mock[IGameState], new UndoManager)
    val spyController = spy(controller)
    val expectedCtx = ctx.copy()

    doReturn(expectedCtx).when(spyController).redo(ctx)

    val result = spyController.executeAIAction(RedoAIAction, ctx)

    assert(result._1 eq expectedCtx)
    assert(result._2)
    verify(spyController).redo(ctx)
  }

  it should "do nothing and return true for NoOpAIAction" in {
    val ctx = GameContext(mock[IGameState], new UndoManager)

    val result = controller.executeAIAction(NoOpAIAction, ctx)

    assert(result._1 eq ctx)
    assert(result._2)
  }

}

