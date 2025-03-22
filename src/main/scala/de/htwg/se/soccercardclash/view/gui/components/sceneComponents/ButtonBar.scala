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
import de.htwg.se.soccercardclash.controller.Events
import de.htwg.se.soccercardclash.model.playingFiledComponent.IPlayingField
import de.htwg.se.soccercardclash.view.gui.components.actionButton.{ActionButtonFactory, DoubleButton, SingleButton}
import de.htwg.se.soccercardclash.view.gui.components.dialog.PauseDialog

case class ButtonBar(controller: IController, playingField: IPlayingField, playingFieldScene: PlayingFieldScene, gameStatusBar: GameStatusBar) extends VBox {


  val singleAttackButton: Button = ActionButtonFactory.createAttackButton(
    SingleButton(),
    "Attack",
    150,
    50,
    playingFieldScene,
    controller
  )
  val doubleAttackButton: Button = ActionButtonFactory.createDoubleAttackButton(
    DoubleButton(),
    "Double Attack",
    150,
    50,
    playingFieldScene,
    controller)

  alignment = Pos.CENTER_LEFT
  spacing = 10
  padding = Insets(20)


  val showDefendersButton: Button = GameButtonFactory.createGameButton(
    text = "Show Defenders",
    width = 180,
    height = 50
  ) { () =>
    controller.notifyObservers(Events.AttackerDefenderCards)
  }
  val pause: Button = GameButtonFactory.createGameButton(
    text = "Pause",
    width = 180,
    height = 50
  ) { () =>
    val menuOverlay = new PauseDialog(controller, playingFieldScene, playingFieldScene.overlay) // ✅ Create menu overlay
    menuOverlay.show() // ✅ Show menu overlay inside OverlayPause
  }


  val makeSwapButton: Button = GameButtonFactory.createGameButton(
    text = "Make Swap",
    width = 180,
    height = 50
  ) { () =>
    controller.notifyObservers(Events.AttackerHandCards)
  }

  children = Seq(
    pause,
    singleAttackButton,
    doubleAttackButton,
    showDefendersButton,
    makeSwapButton)
}
