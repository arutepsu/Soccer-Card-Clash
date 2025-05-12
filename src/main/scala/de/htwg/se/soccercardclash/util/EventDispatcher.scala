package de.htwg.se.soccercardclash.util

import de.htwg.se.soccercardclash.controller.IGameContextHolder
import de.htwg.se.soccercardclash.model.gameComponent.state.IGameState
object EventDispatcher {

  def dispatch(contextHolder: IGameContextHolder, events: List[ObservableEvent]): Unit = {
    events.foreach(contextHolder.notifyObservers)
    println(s"✅ Events dispatched to contextHolder: $events")
  }

  def dispatchSingle(contextHolder: IGameContextHolder, event: ObservableEvent): Unit = {
    contextHolder.notifyObservers(event)
    println(s"✅ Event dispatched to contextHolder: $event")
  }
}

