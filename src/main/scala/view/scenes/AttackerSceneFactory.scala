package view.scenes
import controller.IController
import model.playingFiledComponent.PlayingField
object AttackerSceneFactory {

  /** ✅ Creates an AttackerDefendersScene safely */
  def createAttackerDefendersScene(controller: IController, playingField: Option[PlayingField], windowWidth: Double, windowHeight: Double): AttackerDefendersScene = {
    AttackerDefendersScene(controller, playingField, windowWidth, windowHeight)
  }

  /** ✅ Creates an AttackerHandScene safely */
  def createAttackerHandScene(controller: IController, playingField: Option[PlayingField], windowWidth: Double, windowHeight: Double): AttackerHandScene = {
    AttackerHandScene(controller, playingField, windowWidth, windowHeight)
  }
}


