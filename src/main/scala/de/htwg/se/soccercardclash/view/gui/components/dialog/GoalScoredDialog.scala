package de.htwg.se.soccercardclash.view.gui.components.dialog

import de.htwg.se.soccercardclash.model.playerComponent.IPlayer
import de.htwg.se.soccercardclash.view.gui.components.playerView.PlayerAvatar
import de.htwg.se.soccercardclash.view.gui.overlay.Overlay
import de.htwg.se.soccercardclash.controller.{IController, IGameContextHolder}
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

class GoalScoredDialog(
                        player: IPlayer,
                        overlay: Overlay,
                        controller: IController,
                        gameContextHolder: IGameContextHolder,
                        autoHide: Boolean = true
                      ) {

  private val game = gameContextHolder.get.state
  private val scores = game.getScores
  
  private val backgroundImagePath = "/images/data/frames/pause (1).png"
  private val imageUrl = Option(getClass.getResource(backgroundImagePath))
    .map(_.toExternalForm)
    .getOrElse {
      println(s"Error: Image not found at $backgroundImagePath")
      ""
    }

  private val backgroundImageView = new ImageView(new Image(imageUrl)) {
    fitWidth = 700
    fitHeight = 400
    preserveRatio = false
  }
  
  private val avatarPath = if (player == game.getPlayer1) {
    "/images/data/players/player1.jpg"
  } else {
    "/images/data/players/player2.jpg"
  }

  private val avatarImage = new ImageView(new Image(avatarPath)) {
    fitWidth = 120
    fitHeight = 120
    preserveRatio = true
  }
  
  private val scoredLabel = new Text(s"${player.name} scored a goal!") {
    style = "-fx-font-size: 22px; -fx-font-weight: bold; -fx-fill: white;"
  }
  
  private val scoreLabel = new Text(s"${game.getPlayer1.name}: ${scores.getScorePlayer1}  |  ${game.getPlayer2.name}: ${scores.getScorePlayer2}") {
    style = "-fx-font-size: 18px; -fx-fill: white;"
  }

  private val layout = new VBox {
    alignment = Pos.CENTER
    spacing = 15
    children = Seq(scoredLabel, avatarImage, scoreLabel)
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
