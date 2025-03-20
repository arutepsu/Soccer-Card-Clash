package view.gui.components.dialog

import scalafx.scene.control.Button
import scalafx.scene.layout.{HBox, StackPane, VBox}
import scalafx.scene.text.Text
import scalafx.scene.Scene
import scalafx.geometry.Pos
import scalafx.scene.Node

import scala.concurrent.Future
import scalafx.application.Platform

import scala.concurrent.ExecutionContext.Implicits.global
import controller.IController
import model.playerComponent.IPlayer
import view.gui.overlay.Overlay

object DialogFactory {

  // 🎉 Show Winner Dialog (Game Over Popup)
  def showGameOverPopup(winner: IPlayer, overlay: Overlay, controller: IController, autoHide: Boolean): Unit = {
    new WinnerDialog(winner, overlay, controller, autoHide) // ✅ Uses overlay with autoHide
  }

  def showLoadGameConfirmation(gameFile: String, overlay: Overlay, controller: IController): Unit = {
    new ConfirmationDialog(
      overlay,
      s"Load game: $gameFile?\nDo you want to proceed?",
      () => controller.loadGame(gameFile), // ✅ Load game on confirmation
      controller
    ).show()
  }
}
