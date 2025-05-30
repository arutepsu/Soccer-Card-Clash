package de.htwg.se.soccercardclash.model.gameComponent.action.strategy.executor

import de.htwg.se.soccercardclash.model.gameComponent.IGameState
import de.htwg.se.soccercardclash.model.gameComponent.action.strategy.trigger.IActionStrategy
import de.htwg.se.soccercardclash.util.{GameActionEvent, ObservableEvent}


trait IActionHandler {
  def setNext(handler: IActionHandler): IActionHandler

  def handle(strategy: IActionStrategy, state: IGameState): Option[(Boolean, IGameState, List[ObservableEvent])]
}


abstract class ActionHandler extends IActionHandler {
  private var nextHandler: Option[IActionHandler] = None

  override def setNext(handler: IActionHandler): IActionHandler = {
    nextHandler = Some(handler)
    handler
  }

  protected def handleNext(strategy: IActionStrategy, state: IGameState): Option[(Boolean, IGameState, List[ObservableEvent])] =
    nextHandler.flatMap(_.handle(strategy, state))
}


