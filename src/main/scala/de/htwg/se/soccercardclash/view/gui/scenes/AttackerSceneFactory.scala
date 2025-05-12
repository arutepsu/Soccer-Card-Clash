package de.htwg.se.soccercardclash.view.gui.scenes

import de.htwg.se.soccercardclash.controller.{IController, IGameContextHolder}
import de.htwg.se.soccercardclash.model.gameComponent.state.IGameState

object AttackerSceneFactory {
  
  def createAttackerDefendersScene(controller: IController, playingFieldScene: PlayingFieldScene, playingField: Option[IGameState], windowWidth: Double, windowHeight: Double): AttackerDefendersScene = {
    AttackerDefendersScene(controller,playingFieldScene, playingField, windowWidth, windowHeight)
  }

  def createAttackerHandScene(controller: IController, playingFieldScene: PlayingFieldScene, contextHolder: IGameContextHolder, windowWidth: Double, windowHeight: Double): AttackerHandScene = {
    AttackerHandScene(controller,playingFieldScene, contextHolder, windowWidth, windowHeight)
  }

  def createPlayingFieldScene(controller: IController, contextHolder: IGameContextHolder): PlayingFieldScene = {
    PlayingFieldScene(controller,contextHolder,  800, 600)
  }
}


