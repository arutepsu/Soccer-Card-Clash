package de.htwg.se.soccercardclash.view.gui.components.dialog

import de.htwg.se.soccercardclash.model.playerComponent.IPlayer
import de.htwg.se.soccercardclash.view.gui.overlay.Overlay
import de.htwg.se.soccercardclash.controller.IController
import de.htwg.se.soccercardclash.util.IGameContextHolder
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

class GoalScoredDialog(
                        player: IPlayer,
                        overlay: Overlay,
                        controller: IController,
                        gameContextHolder: IGameContextHolder,
                        autoHide: Boolean = true
                      ) {

  private val game = gameContextHolder.get.state
  private val scores = game.getScores
  Font.loadFont(getClass.getResourceAsStream("/fonts/Rajdhani/Rajdhani-Regular.ttf"), 20)
  Font.loadFont(getClass.getResourceAsStream("/fonts/Rajdhani/Rajdhani-Bold.ttf"), 20)
  private val backgroundImagePath = "/images/data/frames/overlay.png"
  private val imageUrl = Option(getClass.getResource(backgroundImagePath))
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

  val avatarImage = new ImageView(PlayerAvatarRegistry.getAvatarImage(player)) {
    fitWidth = 250
    fitHeight = 250
    preserveRatio = true
  }
  
  private val scoredLabel = new Text(s"${player.name} scored a goal!") {
    style =
      "-fx-font-family: Rajdhani;" +
        "-fx-font-size: 32px;" +
        "-fx-font-weight: bold;" +
        "-fx-fill: linear-gradient(from 0% 0% to 100% 100%, #ff00cc, #83018e);" +
        "-fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.8), 8, 0.4, 0, 2);"
  }


  private val layout = new VBox {
    alignment = Pos.CENTER
    spacing = 15
    children = Seq(scoredLabel, avatarImage)
  }

  private val dialogPane = new StackPane {
    alignment = Pos.CENTER
    children = Seq(backgroundImageView, layout)
  }
  
  overlay.show(dialogPane, autoHide)
  
  if (autoHide) {
    Future {
      Thread.sleep(2500)
      Platform.runLater {
        overlay.hide()
      }
    }
  }
}
