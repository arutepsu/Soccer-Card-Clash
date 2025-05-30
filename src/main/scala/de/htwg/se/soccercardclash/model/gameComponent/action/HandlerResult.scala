package de.htwg.se.soccercardclash.model.gameComponent.action

import de.htwg.se.soccercardclash.model.gameComponent.action.strategy.IActionStrategy
import de.htwg.se.soccercardclash.model.gameComponent.IGameState
import de.htwg.se.soccercardclash.util.GameActionEvent
import de.htwg.se.soccercardclash.util.ObservableEvent


trait IActionHandler {
  def setNext(handler: IActionHandler): IActionHandler

  def handle(strategy: IActionStrategy, state: IGameState): Option[(Boolean, IGameState, List[ObservableEvent])]
}


abstract class BaseActionHandler extends IActionHandler {
  private var nextHandler: Option[IActionHandler] = None

  override def setNext(handler: IActionHandler): IActionHandler = {
    nextHandler = Some(handler)
    handler
  }

  protected def handleNext(strategy: IActionStrategy, state: IGameState): Option[(Boolean, IGameState, List[ObservableEvent])] =
    nextHandler.flatMap(_.handle(strategy, state))
}


