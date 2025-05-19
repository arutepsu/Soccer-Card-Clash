package de.htwg.se.soccercardclash.view.gui.components.sceneComponents

import de.htwg.se.soccercardclash.controller.IController
import de.htwg.se.soccercardclash.view.gui.scenes.PlayingFieldScene
import de.htwg.se.soccercardclash.view.gui.components.sceneComponents.{GameStatusBar, GameStatusMessages}
import scalafx.scene.layout.VBox
import scalafx.geometry.Pos
import scalafx.geometry.Insets
import scalafx.scene.control.Button
import de.htwg.se.soccercardclash.view.gui.components.uiFactory.GameButtonFactory
import de.htwg.se.soccercardclash.view.gui.components.actionButton.*
import de.htwg.se.soccercardclash.model.gameComponent.state.IGameState
import de.htwg.se.soccercardclash.util.{GlobalObservable, SceneSwitchEvent}
import de.htwg.se.soccercardclash.view.gui.components.actionButton.{ActionButtonFactory, DoubleButton, SingleButton}
import de.htwg.se.soccercardclash.view.gui.components.dialog.PauseDialog


case class NavButtonBar(controller: IController, playingField: IGameState, playingFieldScene: PlayingFieldScene, gameStatusBar: GameStatusBar) extends VBox {

  alignment = Pos.CENTER_LEFT
  spacing = 10
  padding = Insets(20)


  val showDefendersButton: Button = GameButtonFactory.createGameButton(
    text = "Show Defenders",
    180, 80
  ) { () =>
    GlobalObservable.notifyObservers(SceneSwitchEvent.AttackerDefenderCards)

  }
  val pause: Button = GameButtonFactory.createGameButton(
    text = "Pause",
    180, 80,
  ) { () =>
    val menuOverlay = new PauseDialog(controller, playingFieldScene, playingFieldScene.overlay)
    menuOverlay.show()
  }


  val makeSwapButton: Button = GameButtonFactory.createGameButton(
    text = "Make Swap",
    180, 80,
  ) { () =>
    GlobalObservable.notifyObservers(SceneSwitchEvent.AttackerHandCards)
  }

  children = Seq(
    pause,
    showDefendersButton,
    makeSwapButton)
}
