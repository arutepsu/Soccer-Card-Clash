package de.htwg.se.soccercardclash.view.gui.scenes

import de.htwg.se.soccercardclash.util.*
import scalafx.application.Platform
import scalafx.scene.Scene

abstract class GameScene extends Scene with Observer {

  override def update(e: ObservableEvent): Unit = Platform.runLater {
    e match
      case g: GameActionEvent => handleGameAction(g)
      case state: StateEvent => handleStateEvent(state)
      case _ =>
  }

  def handleGameAction(e: GameActionEvent): Unit = {}

  def handleStateEvent(e: StateEvent): Unit = {}

  def activate(): Unit = GlobalObservable.add(this)

  def deactivate(): Unit = GlobalObservable.remove(this)
}
