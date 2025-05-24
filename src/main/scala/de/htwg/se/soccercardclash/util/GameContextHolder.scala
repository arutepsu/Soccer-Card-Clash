package de.htwg.se.soccercardclash.util

import de.htwg.se.soccercardclash.model.gameComponent.context.GameContext
import de.htwg.se.soccercardclash.util.{Observable, ObservableEvent}

class GameContextHolder extends IGameContextHolder {
  private var currentCtxOpt: Option[GameContext] = None

  def get: GameContext = currentCtxOpt.getOrElse(
    throw new IllegalStateException("GameContext has not been initialized.")
  )

  def set(ctx: GameContext): Unit = {
    currentCtxOpt = Some(ctx)
  }

  def clear(): Unit = {
    currentCtxOpt = None
  }
}

trait IGameContextHolder{
  def get: GameContext
  def set(ctx: GameContext): Unit
  def clear(): Unit
}
