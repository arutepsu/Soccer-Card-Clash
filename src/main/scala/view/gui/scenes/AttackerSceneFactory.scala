package view.gui.scenes

import controller.IController
import model.playingFiledComponent.IPlayingField

object AttackerSceneFactory {
  
  def createAttackerDefendersScene(controller: IController, playingField: Option[IPlayingField], windowWidth: Double, windowHeight: Double): AttackerDefendersScene = {
    AttackerDefendersScene(controller, playingField, windowWidth, windowHeight)
  }

  def createAttackerHandScene(controller: IController, playingField: Option[IPlayingField], windowWidth: Double, windowHeight: Double): AttackerHandScene = {
    AttackerHandScene(controller, playingField, windowWidth, windowHeight)
  }
}


