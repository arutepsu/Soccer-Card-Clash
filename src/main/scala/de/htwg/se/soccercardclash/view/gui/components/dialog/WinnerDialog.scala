package de.htwg.se.soccercardclash.view.gui.components.dialog

import de.htwg.se.soccercardclash.model.playerComponent.IPlayer
import de.htwg.se.soccercardclash.view.gui.overlay.Overlay
import de.htwg.se.soccercardclash.controller.IController
import de.htwg.se.soccercardclash.controller.contextHolder.IGameContextHolder
import de.htwg.se.soccercardclash.util.SceneSwitchEvent
import de.htwg.se.soccercardclash.view.gui.components.playerView.PlayerAvatarRegistry
import scalafx.scene.control.Button
import scalafx.scene.layout.{StackPane, VBox}
import scalafx.scene.text.{Font, Text}
import scalafx.scene.image.{Image, ImageView}
import scalafx.geometry.Pos
import scalafx.scene.Node

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scalafx.application.Platform
import de.htwg.se.soccercardclash.view.gui.components.uiFactory.GameButtonFactory

class WinnerDialog(winner: IPlayer, overlay: Overlay, controller: IController, contextHolder: IGameContextHolder, autoHide: Boolean) {
  
  Font.loadFont(getClass.getResourceAsStream("/fonts/Rajdhani/Rajdhani-Regular.ttf"), 20)
  Font.loadFont(getClass.getResourceAsStream("/fonts/Rajdhani/Rajdhani-Bold.ttf"), 20)
  val backgroundImagePath = "/images/data/frames/overlay.png"
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

  val avatarImage = new ImageView(PlayerAvatarRegistry.getAvatarImage(winner)) {
    fitWidth = 250
    fitHeight = 250
    preserveRatio = true
  }
  
  val winnerLabel = new Text(s"🏆 ${winner.name} Wins!") {
    style =
      "-fx-font-family: Rajdhani;" +
        "-fx-font-size: 32px;" +
        "-fx-font-weight: bold;" +
        "-fx-fill: linear-gradient(from 0% 0% to 100% 100%, #ff00cc, #83018e);" +
        "-fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.8), 8, 0.4, 0, 2);"
  }
  
  val mainMenuButton: Button = GameButtonFactory.createGameButton(
    text = "Main Menu",
    width = 180,
    height = 60
  ) { () =>
    overlay.hide()
    Future {
      Thread.sleep(300)
      Platform.runLater {
        controller.notifyObservers(SceneSwitchEvent.MainMenu)
      }
    }
  }
  
  val layout = new VBox {
    alignment = Pos.CENTER
    spacing = 20
    children = Seq(
      winnerLabel,
      avatarImage,
      mainMenuButton 
    )
  }
  
  val dialogPane = new StackPane {
    alignment = Pos.CENTER
    children = Seq(backgroundImageView, layout)
  }
  
  overlay.show(dialogPane, autoHide)
}