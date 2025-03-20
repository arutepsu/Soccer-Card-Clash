package view.gui.components.alert

import scalafx.application.Platform
import scalafx.geometry.Pos
import scalafx.scene.Node
import scalafx.scene.control.Button
import scalafx.scene.image.{Image, ImageView}
import scalafx.scene.layout.{StackPane, VBox}
import scalafx.scene.text.Text
import view.gui.overlay.Overlay

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object GameAlertFactory {

  // 🎨 Background Image for Alerts
  private val backgroundImage = new ImageView(new Image("/images/data/frames/pause (1).png")) {
    fitWidth = 800
    fitHeight = 600
    smooth = true
    cache = true
  }

  // 🔔 Create an Alert with Custom Message + AutoHide
  def createAlert(message: String, overlay: Overlay, autoHide: Boolean): Node = {
    val warningText = new Text(s"⚠️ $message") {
      style = "-fx-font-size: 26px; -fx-font-weight: bold; -fx-fill: red;" // ✅ Text is now RED
    }

    val okButton = new Button("OK") {
      onAction = _ => overlay.hide()
    }

    val alertBox = new VBox {
      alignment = Pos.CENTER
      spacing = 20
      children = Seq(warningText, okButton)
    }

    // 🔲 StackPane to Layer Background Image & Alert Content
    val alertPane = new StackPane {
      alignment = Pos.CENTER
      children = Seq(backgroundImage, alertBox) // ✅ Background First, Then Content
    }

    // 🕒 Auto-hide logic
    if (autoHide) {
      Future {
        Thread.sleep(3000) // ✅ Alert disappears after 3 seconds
        Platform.runLater(() => overlay.hide())
      }
    }

    alertPane
  }
}