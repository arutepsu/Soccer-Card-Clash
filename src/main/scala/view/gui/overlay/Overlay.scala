package view.gui.overlay

import scalafx.animation.FadeTransition
import scalafx.application.Platform
import scalafx.geometry.Pos
import scalafx.scene.Node
import scalafx.scene.layout.StackPane
import scalafx.util.Duration
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scalafx.scene.Scene

class Overlay(gameScene: Scene) {  // Renamed parameter to avoid ambiguity

  private val overlayPane: StackPane = new StackPane {
    style = "-fx-background-color: rgba(0, 0, 0, 0.6); -fx-padding: 20px;"
    visible = false
    alignment = Pos.CENTER
    maxWidth = gameScene.width.value / 2  // ✅ Use renamed variable
    maxHeight = gameScene.height.value / 2 // ✅ Use renamed variable
  }

  // Ensure overlay resizes dynamically when scene resizes
  gameScene.width.onChange { (_, _, newWidth) =>
    overlayPane.maxWidth = newWidth.doubleValue() / 2
  }

  gameScene.height.onChange { (_, _, newHeight) =>
    overlayPane.maxHeight = newHeight.doubleValue() / 2
  }

  /** Shows an overlay with fade-in animation */
  def show(content: Node): Unit = {
    overlayPane.children.setAll(content)
    overlayPane.visible = true

    val fadeIn = new FadeTransition(Duration(300), overlayPane)
    fadeIn.fromValue = 0.0
    fadeIn.toValue = 1.0
    fadeIn.play()

    Future {
      Thread.sleep(3000)
      Platform.runLater(() => hide())
    }
  }

  /** Hides the overlay with fade-out animation */
  def hide(): Unit = {
    val fadeOut = new FadeTransition(Duration(300), overlayPane)
    fadeOut.fromValue = 1.0
    fadeOut.toValue = 0.0
    fadeOut.setOnFinished(_ => overlayPane.visible = false)
    fadeOut.play()
  }

  /** Returns the overlay pane for inclusion in the scene */
  def getPane: StackPane = overlayPane
}
