package view.gui.components.actionButton

import controller.IController
import scalafx.scene.Scene
import view.gui.components.sceneView.GameStatusBar
import view.gui.scenes.PlayingFieldScene
trait ActionButton[S <: Scene] { // Restricts to Scene or its subclasses
  def execute(controller: IController, scene: S): Unit
}
