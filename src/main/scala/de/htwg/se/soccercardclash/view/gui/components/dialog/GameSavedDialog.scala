package de.htwg.se.soccercardclash.view.gui.components.dialog

import de.htwg.se.soccercardclash.view.gui.overlay.Overlay
import de.htwg.se.soccercardclash.controller.{Events, IController}
import scalafx.scene.control.Button
import scalafx.scene.layout.{StackPane, VBox}
import scalafx.scene.text.Text
import scalafx.scene.image.{Image, ImageView}
import scalafx.geometry.Pos
import scalafx.scene.Node
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scalafx.application.Platform
import de.htwg.se.soccercardclash.view.gui.components.uiFactory.GameButtonFactory
class GameSavedDialog(overlay: Overlay, autoHide: Boolean) {
  
  val backgroundImagePath = "/images/data/frames/pause (1).png"
  val imageUrl = Option(getClass.getResource(backgroundImagePath))
    .map(_.toExternalForm)
    .getOrElse {
      println(s"Error: Image not found at $backgroundImagePath")
      ""
    }

  val backgroundImageView = new ImageView(new Image(imageUrl)) {
    fitWidth = 800
    fitHeight = 500
    preserveRatio = false
  }
  
  val savedLabel = new Text("💾 Game Saved!") {
    style = "-fx-font-size: 26px; -fx-font-weight: bold; -fx-fill: white;"
  }
  
  val backButton: Button = GameButtonFactory.createGameButton(
    text = "Ok",
    width = 140,
    height = 50
  ) { () =>
    overlay.hide()
  }
  
  val layout = new VBox {
    alignment = Pos.CENTER
    spacing = 20
    children = Seq(
      savedLabel,
      backButton
    )
  }
  
  val dialogPane = new StackPane {
    alignment = Pos.CENTER
    children = Seq(backgroundImageView, layout)
  }

  overlay.show(dialogPane, autoHide)
}
