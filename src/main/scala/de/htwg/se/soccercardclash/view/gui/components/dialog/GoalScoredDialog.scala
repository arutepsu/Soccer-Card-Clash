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
import de.htwg.se.soccercardclash.view.gui.utils.Styles

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
    styleClass += "info-message"
  }


  private val layout = new VBox {
    alignment = Pos.CENTER
    spacing = 15
    children = Seq(scoredLabel, avatarImage)
  }

  private val dialogPane = new StackPane {
    alignment = Pos.CENTER
    children = Seq(backgroundImageView, layout)
    stylesheets += Styles.infoDialogCss
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
