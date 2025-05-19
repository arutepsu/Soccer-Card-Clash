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
import de.htwg.se.soccercardclash.view.gui.components.dialog.{DialogFactory, PauseDialog}
import de.htwg.se.soccercardclash.view.gui.overlay.Overlay

case class ActionButtonBar(controller: IController,
                           playingField: IGameState,
                           playingFieldScene: PlayingFieldScene,
                           gameStatusBar: GameStatusBar,
                           overlay: Overlay) extends VBox {


  val singleAttackButton: Button = ActionButtonFactory.createAttackButton(
    SingleButton(),
    "Attack",
    180, 80,
    playingFieldScene,
    controller
  )
  val doubleAttackButton: Button = ActionButtonFactory.createDoubleAttackButton(
    DoubleButton(),
    "Double Attack",
    180, 80,
    playingFieldScene,
    controller)

  private val infoButton: Button = GameButtonFactory.createGameButton("Info", 180, 80) {
    () => DialogFactory.showHandInfoDialog("Title", "Message", overlay)
  }

  alignment = Pos.CENTER_LEFT
  spacing = 10
  padding = Insets(20)
  
  children = Seq(
    singleAttackButton,
    doubleAttackButton,
    infoButton)
}
