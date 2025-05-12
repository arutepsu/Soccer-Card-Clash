package de.htwg.se.soccercardclash.controller.base

import com.google.inject.Inject
import de.htwg.se.soccercardclash.controller.command.{ICommand, ICommandFactory}
import de.htwg.se.soccercardclash.controller.{IController, IGameContextHolder}
import de.htwg.se.soccercardclash.model.playerComponent.IPlayer
import de.htwg.se.soccercardclash.model.gameComponent.state.IGameState
import de.htwg.se.soccercardclash.model.gameComponent.state.base.GameState
import de.htwg.se.soccercardclash.model.gameComponent.state.manager.IActionManager
import de.htwg.se.soccercardclash.model.gameComponent.service.IGameService
import de.htwg.se.soccercardclash.model.playerComponent.playerAction.{CanPerformAction, OutOfActions, PlayerActionPolicies}
import de.htwg.se.soccercardclash.util.{EventDispatcher, Events, ObservableEvent, Observer, UndoManager}
import de.htwg.se.soccercardclash.model.gameComponent.context.GameContext
import de.htwg.se.soccercardclash.model.cardComponent.dataStructure.IHandCardsQueueFactory
class Controller @Inject()(
                            commandFactory: ICommandFactory,
                            gameService: IGameService,
                            handFactory: IHandCardsQueueFactory,
                            actionManager: IActionManager,
                            contextHolder: IGameContextHolder
                          ) extends IController {

  private def run(ctx: GameContext, command: ICommand, mainEvent: ObservableEvent): (GameContext, Boolean) = {

    val result = ctx.undoManager.doStep(command, ctx.state)
    val updatedCtx = ctx.copy(state = result.state)
    val allEvents = if (result.success) mainEvent :: result.events else mainEvent :: fallbackEventFor(mainEvent, ctx)
    EventDispatcher.dispatch(contextHolder, allEvents)

    (updatedCtx, result.success)
  }

  private def fallbackEventFor(mainEvent: ObservableEvent, ctx: GameContext): List[ObservableEvent] = {
    val attacker = ctx.state.getRoles.attacker
    mainEvent match {
      case Events.DoubleAttack => List(Events.NoDoubleAttacksEvent(attacker))
      case ev if ev == Events.BoostDefender || ev == Events.BoostGoalkeeper =>
        List(Events.NoBoostsEvent(attacker))
      case ev if ev == Events.RegularSwap || ev == Events.ReverseSwap =>
        List(Events.NoSwapsEvent(attacker))

      case _ => Nil
    }
  }

  def singleAttack(defenderIndex: Int, ctx: GameContext): (GameContext, Boolean) =
    run(ctx, commandFactory.createSingleAttackCommand(defenderIndex), Events.RegularAttack)

  def doubleAttack(defenderIndex: Int, ctx: GameContext): (GameContext, Boolean) =
    run(ctx, commandFactory.createDoubleAttackCommand(defenderIndex), Events.DoubleAttack)

  def regularSwap(index: Int, ctx: GameContext): (GameContext, Boolean) =
    run(ctx, commandFactory.createRegularSwapCommand(index), Events.RegularSwap)

  def reverseSwap(ctx: GameContext): (GameContext, Boolean) =
    run(ctx, commandFactory.createReverseSwapCommand(), Events.ReverseSwap)

  def boostDefender(defenderIndex: Int, ctx: GameContext): (GameContext, Boolean) =
    run(ctx, commandFactory.createBoostDefenderCommand(defenderIndex), Events.BoostDefender)

  def boostGoalkeeper(ctx: GameContext): (GameContext, Boolean) =
    run(ctx, commandFactory.createBoostGoalkeeperCommand(), Events.BoostGoalkeeper)

  override def createGame(player1: String, player2: String): Unit = {
    val ctx = GameContext(gameService.createNewGame(player1, player2), new UndoManager)
    contextHolder.set(ctx)
    contextHolder.notifyObservers(Events.PlayingField)
  }

  override def loadGame(fileName: String): Boolean = {
    gameService.loadGame(fileName).toOption match {
      case Some(state) =>
        contextHolder.set(GameContext(state, new UndoManager))
        contextHolder.notifyObservers(Events.PlayingField)
        true
      case None =>
        false
    }
  }

  def saveGame(ctx: GameContext): Boolean = {
    val success = gameService.saveGame(ctx.state).isSuccess
    if (success) contextHolder.notifyObservers(Events.SaveGame)
    success
  }

  def undo(ctx: GameContext): GameContext = {
    val (newState, events) = ctx.undoManager.undoStep(ctx.state)
    val updatedCtx = ctx.copy(state = newState)
    contextHolder.set(updatedCtx)

    EventDispatcher.dispatch(contextHolder, Events.Undo :: events)

    updatedCtx
  }

  def redo(ctx: GameContext): GameContext = {
    val (newState, events) = ctx.undoManager.redoStep(ctx.state)
    val updatedCtx = ctx.copy(state = newState)
    contextHolder.set(updatedCtx)

    EventDispatcher.dispatch(contextHolder, Events.Redo :: events)
    updatedCtx
  }

  def quit(): Unit = System.exit(0)

}
