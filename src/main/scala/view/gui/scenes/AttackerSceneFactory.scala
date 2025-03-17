package view.gui.scenes

import controller.IController
import model.playingFiledComponent.IPlayingField

object AttackerSceneFactory {
  
  def createAttackerDefendersScene(controller: IController, playingFieldScene: PlayingFieldScene, playingField: Option[IPlayingField], windowWidth: Double, windowHeight: Double): AttackerDefendersScene = {
    AttackerDefendersScene(controller,playingFieldScene, playingField, windowWidth, windowHeight)
  }

  def createAttackerHandScene(controller: IController, playingFieldScene: PlayingFieldScene, playingField: Option[IPlayingField], windowWidth: Double, windowHeight: Double): AttackerHandScene = {
    AttackerHandScene(controller,playingFieldScene, playingField, windowWidth, windowHeight)
  }

  def createPlayingFieldScene(controller: IController): PlayingFieldScene = {
    PlayingFieldScene(controller, 800, 600)
  }
}


