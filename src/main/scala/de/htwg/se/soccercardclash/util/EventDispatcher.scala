package de.htwg.se.soccercardclash.util

import de.htwg.se.soccercardclash.controller.IController
import de.htwg.se.soccercardclash.model.gameComponent.state.IGameState
object EventDispatcher {

  def dispatch(controller: IController, events: List[ObservableEvent]): Unit =
    events.foreach(controller.notifyObservers)

  def dispatchSingle(controller: IController, event: ObservableEvent): Unit = event match {
    case sceneEvent: SceneSwitchEvent=>
      println(f"to be dispatched: ${event}")
      GlobalObservable.notifyObservers(sceneEvent)
    case other =>
      println(f"to be dispatched: ${other}")
      controller.notifyObservers(other)
  }
}

