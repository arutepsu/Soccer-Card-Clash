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
import scalafx.animation._
import scalafx.util.Duration
import scalafx.scene.Node
import scalafx.scene.layout.StackPane
import scalafx.application.Platform
import scalafx.scene.effect.GaussianBlur
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

class Overlay(gameScene: Scene) {
  protected val overlayPane: StackPane = new StackPane {
    style = "-fx-background-color: rgba(0, 0, 0, 0.0); -fx-padding: 20px;"
    visible = false
    alignment = Pos.CENTER
    maxWidth = gameScene.width.value * 1.2
    maxHeight = gameScene.height.value * 1.2
  }

  gameScene.width.onChange { (_, _, newWidth) =>
    overlayPane.maxWidth = newWidth.doubleValue() * 1.2
  }

  gameScene.height.onChange { (_, _, newHeight) =>
    overlayPane.maxHeight = newHeight.doubleValue() * 1.2
  }

  private var onHiddenCallback: () => Unit = () => {}

  def setOnHidden(callback: () => Unit): Unit = {
    onHiddenCallback = callback
  }

  def show(content: Node, autoHide: Boolean): Unit = {
    val sizeMultiplier = if (autoHide) 1.2 else 8.0

    overlayPane.maxWidth = gameScene.width.value * sizeMultiplier
    overlayPane.maxHeight = gameScene.height.value * sizeMultiplier

    gameScene.width.onChange { (_, _, newWidth) =>
      overlayPane.maxWidth = newWidth.doubleValue() * sizeMultiplier
    }

    gameScene.height.onChange { (_, _, newHeight) =>
      overlayPane.maxHeight = newHeight.doubleValue() * sizeMultiplier
    }
    overlayPane.children.setAll(content)
    overlayPane.visible = true

    val fadeIn = new FadeTransition(Duration(500), overlayPane)
    fadeIn.fromValue = 0.0
    fadeIn.toValue = 1.0

    val scaleUp = new ScaleTransition(Duration(500), overlayPane)
    scaleUp.toX = 1.0
    scaleUp.toY = 1.0

    val parallelTransition = new ParallelTransition()
    parallelTransition.children = Seq(fadeIn, scaleUp)
    parallelTransition.play()

    if (autoHide) {
      Future {
        Thread.sleep(3000)
        Platform.runLater(() => hide())
      }
    }
  }

  def hide(): Unit = {
    val fadeOut = new FadeTransition(Duration(500), overlayPane)
    fadeOut.fromValue = 1.0
    fadeOut.toValue = 0.0

    val scaleDown = new ScaleTransition(Duration(500), overlayPane)
    scaleDown.toX = 0.8
    scaleDown.toY = 0.8

    val parallelTransition = new ParallelTransition()
    parallelTransition.children = Seq(fadeOut, scaleDown)
    parallelTransition.setOnFinished { _ =>
      overlayPane.visible = false
      onHiddenCallback()
    }
    parallelTransition.play()
  }

  def getPane: StackPane = overlayPane
}
