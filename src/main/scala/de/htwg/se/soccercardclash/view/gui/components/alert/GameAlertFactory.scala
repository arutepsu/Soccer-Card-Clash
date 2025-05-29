package de.htwg.se.soccercardclash.view.gui.components.alert

import de.htwg.se.soccercardclash.view.gui.components.uiFactory.GameButtonFactory
import scalafx.application.Platform
import scalafx.geometry.Pos
import scalafx.scene.Node
import scalafx.scene.control.Button
import scalafx.scene.image.{Image, ImageView}
import scalafx.scene.layout.{StackPane, VBox}
import scalafx.scene.text.Text
import de.htwg.se.soccercardclash.view.gui.overlay.Overlay
import de.htwg.se.soccercardclash.view.gui.utils.Styles

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object GameAlertFactory {

  private val backgroundImage = new ImageView(new Image("/images/data/frames/overlay.png")) {
    fitWidth = 800
    fitHeight = 600
    smooth = true
    cache = true
  }

  def createAlert(message: String, overlay: Overlay, autoHide: Boolean): Node = {
    val warningText = new Text(s"$message") {
      styleClass += "dialog-message"
    }

    val okButton = GameButtonFactory.createGameButton(
      text = "OK",
      width = 150,
      height = 50
    ) { () =>
      overlay.hide()
    }

    val alertBox = new VBox {
      alignment = Pos.CENTER
      spacing = 20
      children = Seq(warningText, okButton)
    }

    val alertPane = new StackPane {
      alignment = Pos.CENTER
      children = Seq(backgroundImage, alertBox)
      stylesheets += Styles.infoDialogCss
    }

    if (autoHide) {
      Future {
        Thread.sleep(3000)
        Platform.runLater(() => overlay.hide())
      }
    }

    alertPane
  }
}