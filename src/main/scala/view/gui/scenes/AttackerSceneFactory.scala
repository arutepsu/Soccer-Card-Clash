package view.gui.scenes

import controller.IController
import model.playingFiledComponent.PlayingField
object AttackerSceneFactory {
  
  def createAttackerDefendersScene(controller: IController, playingField: Option[PlayingField], windowWidth: Double, windowHeight: Double): AttackerDefendersScene = {
    AttackerDefendersScene(controller, playingField, windowWidth, windowHeight)
  }

  def createAttackerHandScene(controller: IController, playingField: Option[PlayingField], windowWidth: Double, windowHeight: Double): AttackerHandScene = {
    AttackerHandScene(controller, playingField, windowWidth, windowHeight)
  }
}


