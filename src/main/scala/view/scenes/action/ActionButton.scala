package view.scenes.action
import controller.IController
import model.playingFiledComponent.PlayingField
import view.scenes.PlayingFieldScene
import view.components.gameComponents.GameStatusBar
import view.scenes.PlayingFieldScene
import scalafx.scene.Scene
trait ActionButton[S <: Scene] { // Restricts to Scene or its subclasses
  def execute(controller: IController, scene: S, gameStatusBar: GameStatusBar): Unit
}
