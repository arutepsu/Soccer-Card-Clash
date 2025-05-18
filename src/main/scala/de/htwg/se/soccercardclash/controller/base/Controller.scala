package de.htwg.se.soccercardclash.controller.base

import com.google.inject.Inject
import de.htwg.se.soccercardclash.controller.command.{ICommand, ICommandFactory}
import de.htwg.se.soccercardclash.controller.{IController, IGameContextHolder}
import de.htwg.se.soccercardclash.model.playerComponent.IPlayer
import de.htwg.se.soccercardclash.model.playerComponent.base.Player
import de.htwg.se.soccercardclash.model.gameComponent.state.IGameState
import de.htwg.se.soccercardclash.model.gameComponent.state.base.GameState
import de.htwg.se.soccercardclash.model.gameComponent.state.manager.IActionManager
import de.htwg.se.soccercardclash.model.gameComponent.service.IGameService
import de.htwg.se.soccercardclash.model.playerComponent.playerAction.{CanPerformAction, OutOfActions, PlayerActionPolicies}
import de.htwg.se.soccercardclash.util.{EventDispatcher, ObservableEvent, Observer, UndoManager, Observable}
import de.htwg.se.soccercardclash.model.gameComponent.context.GameContext
import de.htwg.se.soccercardclash.model.cardComponent.dataStructure.IHandCardsQueueFactory
import de.htwg.se.soccercardclash.model.playerComponent.base.AI
import de.htwg.se.soccercardclash.util.*

class Controller @Inject()(
                            commandFactory: ICommandFactory,
                            gameService: IGameService,
                            handFactory: IHandCardsQueueFactory,
                            actionManager: IActionManager,
                            contextHolder: IGameContextHolder
                          ) extends IController{

  private def run(ctx: GameContext, command: ICommand, mainEvent: ObservableEvent): (GameContext, Boolean) = {

    val result = ctx.undoManager.doStep(command, ctx.state)
    val updatedCtx = ctx.copy(state = result.state)
    val allEvents = if (result.success) mainEvent :: result.events else mainEvent :: fallbackEventFor(mainEvent, ctx)
    EventDispatcher.dispatch(this, allEvents)

    if (result.success) {
      EventDispatcher.dispatchSingle(this, TurnEvent.NextTurnEvent)
    }


    (updatedCtx, result.success)
  }

  private def fallbackEventFor(mainEvent: ObservableEvent, ctx: GameContext): List[ObservableEvent] = {
    val attacker = ctx.state.getRoles.attacker
    mainEvent match {
      case GameActionEvent.DoubleAttack => List(StateEvent.NoDoubleAttacksEvent(attacker))
      case GameActionEvent.BoostDefender | GameActionEvent.BoostGoalkeeper =>
        List(StateEvent.NoBoostsEvent(attacker))
      case GameActionEvent.RegularSwap | GameActionEvent.ReverseSwap =>
        List(StateEvent.NoSwapsEvent(attacker))
      case _ => Nil
    }
  }

  override def singleAttack(defenderIndex: Int, ctx: GameContext): (GameContext, Boolean) =
    run(ctx, commandFactory.createSingleAttackCommand(defenderIndex), GameActionEvent.RegularAttack)

  override def doubleAttack(defenderIndex: Int, ctx: GameContext): (GameContext, Boolean) =
    run(ctx, commandFactory.createDoubleAttackCommand(defenderIndex), GameActionEvent.DoubleAttack)

  override def regularSwap(index: Int, ctx: GameContext): (GameContext, Boolean) =
    run(ctx, commandFactory.createRegularSwapCommand(index), GameActionEvent.RegularSwap)

  override def reverseSwap(ctx: GameContext): (GameContext, Boolean) =
    run(ctx, commandFactory.createReverseSwapCommand(), GameActionEvent.ReverseSwap)

  override def boostDefender(defenderIndex: Int, ctx: GameContext): (GameContext, Boolean) =
    run(ctx, commandFactory.createBoostDefenderCommand(defenderIndex), GameActionEvent.BoostDefender)

  override def boostGoalkeeper(ctx: GameContext): (GameContext, Boolean) =
    run(ctx, commandFactory.createBoostGoalkeeperCommand(), GameActionEvent.BoostGoalkeeper)

  override def createGame(player1: String, player2: String): Unit = {
    val ctx = GameContext(gameService.createNewGame(player1, player2), new UndoManager)
    contextHolder.set(ctx)
  }

  override def createGameWithAI(humanPlayerName: String): Unit = {
    val ctx = GameContext(gameService.createNewGameWithAI(humanPlayerName), new UndoManager)
    contextHolder.set(ctx)
    EventDispatcher.dispatchSingle(this, TurnEvent.NextTurnEvent)
  }

  override def loadGame(fileName: String): Boolean = {
    gameService.loadGame(fileName).toOption match {
      case Some(state) =>
        contextHolder.set(GameContext(state, new UndoManager))
        EventDispatcher.dispatchSingle(this, SceneSwitchEvent.PlayingField)
        true
      case None =>
        false
    }
  }

  override def saveGame(ctx: GameContext): Boolean = {
    val success = gameService.saveGame(ctx.state).isSuccess
    if (success) EventDispatcher.dispatchSingle(this, GameActionEvent.SaveGame)
    success
  }

  override def undo(ctx: GameContext): GameContext = {
    val (newState, events) = ctx.undoManager.undoStep(ctx.state)
    val updatedCtx = ctx.copy(state = newState)
    contextHolder.set(updatedCtx)
    EventDispatcher.dispatch(this, GameActionEvent.Undo :: events)
    updatedCtx
  }

  override def redo(ctx: GameContext): GameContext = {
    val (newState, events) = ctx.undoManager.redoStep(ctx.state)
    val updatedCtx = ctx.copy(state = newState)
    contextHolder.set(updatedCtx)
    EventDispatcher.dispatch(this, GameActionEvent.Redo :: events)
    updatedCtx
  }

  override def quit(): Unit = System.exit(0)

  override def executePlayerAction(action: PlayerAction, ctx: GameContext): (GameContext, Boolean) = {
    action match {
      case SingleAttackAction(defenderIndex) =>
        singleAttack(defenderIndex, ctx)
      case DoubleAttackAction(defenderIndex) =>
        doubleAttack(defenderIndex, ctx)
      case RegularSwapAction(index) =>
        regularSwap(index, ctx)
      case ReverseSwapAction =>
        reverseSwap(ctx)
      case BoostDefenderAction(defenderIndex) =>
        boostDefender(defenderIndex, ctx)
      case BoostGoalkeeperAction =>
        boostGoalkeeper(ctx)
      case UndoAction =>
        (undo(ctx), true)
      case RedoAction =>
        (redo(ctx), true)
      case NoOpAction =>
        (ctx, true)
    }
  }
}
