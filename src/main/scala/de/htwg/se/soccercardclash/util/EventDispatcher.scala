package de.htwg.se.soccercardclash.util

import de.htwg.se.soccercardclash.controller.IController
import de.htwg.se.soccercardclash.model.gameComponent.IGameState

object EventDispatcher {

  def dispatch(controller: IController, events: List[ObservableEvent]): Unit =
    events.foreach(controller.notifyObservers)

  def dispatchSingle(controller: IController, event: ObservableEvent): Unit = event match {
    case sceneEvent: SceneSwitchEvent=>
      GlobalObservable.notifyObservers(sceneEvent)
    case other =>
      controller.notifyObservers(other)
  }
}

