package de.htwg.se.soccercardclash.controller

import de.htwg.se.soccercardclash.model.gameComponent.context.GameContext
import de.htwg.se.soccercardclash.util.{Events, Observable, ObservableEvent}

class GameContextHolder extends IGameContextHolder {
  private var currentCtxOpt: Option[GameContext] = None

  def get: GameContext = currentCtxOpt.getOrElse(
    throw new IllegalStateException("GameContext has not been initialized.")
  )

  def set(ctx: GameContext): Unit = {
    currentCtxOpt = Some(ctx)
  }

  def update(f: GameContext => (GameContext, List[ObservableEvent])): Unit = {
    val (newCtx, events) = f(get)
    currentCtxOpt = Some(newCtx)
  }

  def clear(): Unit = {
    currentCtxOpt = None
  }
}

trait IGameContextHolder extends Observable{
  def get: GameContext
  def set(ctx: GameContext): Unit
  def update(f: GameContext => (GameContext, List[ObservableEvent])): Unit
  def clear(): Unit
}
