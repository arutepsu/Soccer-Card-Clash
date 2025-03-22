package de.htwg.se.soccercardclash.view.gui.components.dialog

import scalafx.scene.layout.{VBox, StackPane, HBox}
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
import de.htwg.se.soccercardclash.controller.{Events, IController}

class ConfirmationDialog(overlay: Overlay, message: String, onConfirm: () => Unit, controller: IController) {

  // 🎨 Background Image for Dialog
  private val backgroundImagePath = "/images/data/frames/pause (1).png"
  private val backgroundImage = new ImageView(new Image(backgroundImagePath)) {
    fitWidth = 800
    fitHeight = 600
    smooth = true
    cache = true
  }

  // 📝 Message Text (Now White for better visibility)
  private val messageText = new Text(message) {
    style = "-fx-font-size: 20px; -fx-fill: white; -fx-font-weight: bold;"
  }

  // ✅ OK Button - Calls `onConfirm` when clicked
  private val okButton: Button = GameButtonFactory.createGameButton(
    text = "OK",
    width = 180,
    height = 60
  ) { () =>
    overlay.hide()
    println("✅ Confirmation OK clicked") // Debugging

    Future {
      Thread.sleep(300) // ✅ Small delay for smooth transition
      Platform.runLater {
        onConfirm() // Calls the provided function
      }
    }
  }

  // ❌ Cancel Button - Just hides overlay
  private val cancelButton: Button = GameButtonFactory.createGameButton(
    text = "Cancel",
    width = 180,
    height = 60
  ) { () =>
    overlay.hide()
    println("❌ Confirmation Cancel clicked") // Debugging
  }

  // 🔘 Button Layout
  private val buttonBox = new HBox(10, okButton, cancelButton) {
    alignment = Pos.CENTER
  }

  // 📦 Alert Box
  private val alertBox = new VBox {
    alignment = Pos.CENTER
    spacing = 20
    children = Seq(messageText, buttonBox)
  }

  // 🖼️ Dialog with Background
  private val dialogPane = new StackPane {
    alignment = Pos.CENTER
    children = Seq(backgroundImage, alertBox) // Background First, Then Content
  }

  // 📌 Show Dialog
  def show(): Unit = {
    println("🔲 Showing Confirmation Dialog") // Debugging
    overlay.show(dialogPane, autoHide = false)
  }

  // ❌ Hide Dialog
  def hide(): Unit = {
    overlay.hide()
  }
}
