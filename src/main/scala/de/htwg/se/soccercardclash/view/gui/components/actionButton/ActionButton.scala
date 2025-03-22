package de.htwg.se.soccercardclash.view.gui.components.actionButton

import de.htwg.se.soccercardclash.controller.IController
import de.htwg.se.soccercardclash.view.gui.components.sceneComponents.GameStatusBar
import scalafx.scene.Scene

import de.htwg.se.soccercardclash.view.gui.scenes.PlayingFieldScene
trait ActionButton[S <: Scene] { // Restricts to Scene or its subclasses
  def execute(controller: IController, scene: S): Unit
}
