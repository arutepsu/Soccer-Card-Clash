package de.htwg.se.soccercardclash.view.gui.components.dialog

import de.htwg.se.soccercardclash.view.gui.components.uiFactory.GameButtonFactory
import de.htwg.se.soccercardclash.view.gui.overlay.Overlay
import de.htwg.se.soccercardclash.util.SceneSwitchEvent
import scalafx.scene.control.Button
import scalafx.scene.layout.{StackPane, VBox}
import scalafx.scene.text.{Text, TextAlignment}
import scalafx.scene.image.{Image, ImageView}
import scalafx.geometry.Pos
import scalafx.scene.Node

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scalafx.application.Platform

class InfoDialog(
                  title: String,
                  message: String,
                  overlay: Overlay,
                  autoHide: Boolean = false
                ) {

  private val backgroundImagePath = "/images/data/frames/pause (1).png"
  private val imageUrl = Option(getClass.getResource(backgroundImagePath))
    .map(_.toExternalForm)
    .getOrElse {
      println(s"Error: Image not found at $backgroundImagePath")
      ""
    }

  private val backgroundImageView = new ImageView(new Image(imageUrl)) {
    fitWidth = 1200
    fitHeight = 800
    preserveRatio = false
  }

  private val titleText = new Text(title) {
    style = "-fx-font-size: 26px; -fx-font-weight: bold; -fx-fill: gold;"
    wrappingWidth = 600
    textAlignment = TextAlignment.Center
  }

  private val messageText = new Text(message) {
    style = "-fx-font-size: 18px; -fx-fill: white;"
    wrappingWidth = 600
    textAlignment = TextAlignment.Center
  }

  private val okButton = GameButtonFactory.createGameButton(
    text = "OK",
    width = 140,
    height = 50
  ) { () =>
    overlay.hide()
  }

  private val layout = new VBox {
    alignment = Pos.Center
    spacing = 20
    children = Seq(titleText, messageText, okButton)
  }

  private val dialogPane = new StackPane {
    alignment = Pos.Center
    children = Seq(backgroundImageView, layout)
  }

  overlay.show(dialogPane, autoHide)
}
