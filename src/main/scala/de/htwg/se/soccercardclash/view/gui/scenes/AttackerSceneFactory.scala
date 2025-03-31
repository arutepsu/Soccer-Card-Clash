package de.htwg.se.soccercardclash.view.gui.scenes

import de.htwg.se.soccercardclash.controller.IController
import de.htwg.se.soccercardclash.model.gameComponent.playingFiledComponent.IPlayingField

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


