package de.htwg.se.soccercardclash.view.gui.components.dialog

import de.htwg.se.soccercardclash.model.playerComponent.IPlayer
import de.htwg.se.soccercardclash.view.gui.overlay.Overlay
import de.htwg.se.soccercardclash.controller.{IController, IGameContextHolder}
import de.htwg.se.soccercardclash.util.SceneSwitchEvent
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

class WinnerDialog(winner: IPlayer, overlay: Overlay, controller: IController, contextHolder: IGameContextHolder, autoHide: Boolean) {

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
  
  val winnerAvatarPath = if (winner == contextHolder.get.state.getPlayer1) {
    "/images/data/players/player1.jpg"
  } else {
    "/images/data/players/player2.jpg"
  }
  
  val avatarImage = new ImageView(new Image(winnerAvatarPath)) {
    fitWidth = 150
    fitHeight = 150
    preserveRatio = true
  }
  
  val winnerLabel = new Text(s"🏆 ${winner.name} Wins!") {
    style = "-fx-font-size: 24px; -fx-font-weight: bold; -fx-fill: white;"
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