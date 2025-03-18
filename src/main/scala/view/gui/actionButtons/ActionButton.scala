package view.gui.actionButtons

import controller.IController

import view.gui.scenes.PlayingFieldScene
import view.gui.components.sceneView.GameStatusBar
import view.gui.scenes.PlayingFieldScene
import scalafx.scene.Scene
trait ActionButton[S <: Scene] { // Restricts to Scene or its subclasses
  def execute(controller: IController, scene: S): Unit
}
