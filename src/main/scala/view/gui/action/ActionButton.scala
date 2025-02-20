package view.gui.action

import controller.IController
import model.playingFiledComponent.base.PlayingField
import view.gui.scenes.PlayingFieldScene
import view.gui.components.sceneBar.GameStatusBar
import view.gui.scenes.PlayingFieldScene
import scalafx.scene.Scene
trait ActionButton[S <: Scene] { // Restricts to Scene or its subclasses
  def execute(controller: IController, scene: S, gameStatusBar: GameStatusBar): Unit
}
