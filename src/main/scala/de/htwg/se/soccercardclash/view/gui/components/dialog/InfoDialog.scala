package de.htwg.se.soccercardclash.view.gui.components.dialog

import de.htwg.se.soccercardclash.view.gui.components.uiFactory.GameButtonFactory
import de.htwg.se.soccercardclash.view.gui.overlay.Overlay
import de.htwg.se.soccercardclash.util.SceneSwitchEvent
import de.htwg.se.soccercardclash.view.gui.utils.Styles
import scalafx.scene.control.{Button, ScrollPane}
import scalafx.scene.layout.{StackPane, VBox}
import scalafx.scene.text.{Font, Text, TextAlignment}
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
  // val regularFont = Font.loadFont(getClass.getResourceAsStream("/fonts/Rajdhani/Rajdhani-Regular.ttf"), 18)
  // val boldFont = Font.loadFont(getClass.getResourceAsStream("/fonts/Rajdhani/Rajdhani-Bold.ttf"), 28)


  private val backgroundImagePath = "/images/data/frames/overlay.png"
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
    styleClass += "dialog-title"
    wrappingWidth = 600
    textAlignment = TextAlignment.Center
  }

  private val messageContent = if (message == "GAME_INFO") {
    GameInfoPaneFactory.createMainInstructionsDialog(overlay.getPane.width.value-400, overlay.getPane.height.value-280)
  } else if (message == "SWAP_INFO") {
    GameInfoPaneFactory.createSwapInstructionsDialog(overlay.getPane.width.value - 400, overlay.getPane.height.value - 280)
  } else if (message == "BOOST_INFO") {
    GameInfoPaneFactory.createBoostInstructionsDialog(overlay.getPane.width.value - 400, overlay.getPane.height.value - 280)
  } else if (message == "GENERAL_INFO") {
    GameInfoPaneFactory.createGameInfoDialog(overlay.getPane.width.value - 400, overlay.getPane.height.value - 280)
  }else {
    new Text(message) {
      styleClass += "dialog-message"
      wrappingWidth = 600
      textAlignment = TextAlignment.Center
    }
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
    children = Seq(titleText, messageContent, okButton)
  }

  private val dialogPane = new StackPane {
    alignment = Pos.Center
    children = Seq(backgroundImageView, layout)

  }

  overlay.show(dialogPane, autoHide)
  dialogPane.getStylesheets.add(Styles.infoDialogCss)

}
