package de.htwg.se.soccercardclash.view.gui.components.dialog

import scalafx.scene.layout.{HBox, StackPane, VBox}
import scalafx.scene.text.Text
import scalafx.scene.control.Button
import scalafx.geometry.Pos
import scalafx.scene.image.{Image, ImageView}
import scalafx.scene.Node
import scalafx.application.Platform

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import de.htwg.se.soccercardclash.view.gui.overlay.Overlay
import de.htwg.se.soccercardclash.view.gui.components.uiFactory.GameButtonFactory
import de.htwg.se.soccercardclash.controller.IController
import de.htwg.se.soccercardclash.util.{GlobalObservable, SceneSwitchEvent}
import de.htwg.se.soccercardclash.controller.IGameContextHolder
import de.htwg.se.soccercardclash.view.gui.components.playerView.PlayerAvatarRegistry
import de.htwg.se.soccercardclash.util.EventDispatcher
class ConfirmationDialog(overlay: Overlay, message: String, onConfirm: () => Unit, controller: IController, contextHolder: IGameContextHolder) {
  
  private val backgroundImagePath = "/images/data/frames/overlay.png"
  private val backgroundImage = new ImageView(new Image(backgroundImagePath)) {
    fitWidth = 800
    fitHeight = 600
    smooth = true
    cache = true
  }

  private val messageText = new Text(message) {
    style = "-fx-font-size: 20px; -fx-fill: white; -fx-font-weight: bold;"
  }

  private val okButton: Button = GameButtonFactory.createGameButton(
    text = "OK",
    width = 180,
    height = 60
  ) { () =>
    overlay.hide()

    Future {
      Thread.sleep(300)
      Platform.runLater {
        onConfirm()
        val players = Seq(
          contextHolder.get.state.getRoles.attacker,
          contextHolder.get.state.getRoles.defender
        )
        PlayerAvatarRegistry.assignAvatarsInOrder(players)

        EventDispatcher.dispatchSingle(controller, SceneSwitchEvent.PlayingField)
      }
    }
  }
  
  private val cancelButton: Button = GameButtonFactory.createGameButton(
    text = "Cancel",
    width = 180,
    height = 60
  ) { () =>
    overlay.hide()
  }
  
  private val buttonBox = new HBox(10, okButton, cancelButton) {
    alignment = Pos.CENTER
  }
  
  private val alertBox = new VBox {
    alignment = Pos.CENTER
    spacing = 20
    children = Seq(messageText, buttonBox)
  }
  
  private val dialogPane = new StackPane {
    alignment = Pos.CENTER
    children = Seq(backgroundImage, alertBox)
  }
  
  def show(): Unit = {
    overlay.show(dialogPane, autoHide = false)
  }
  
  def hide(): Unit = {
    overlay.hide()
  }
}
