package view.scenes.sceneManager
//
import scalafx.stage.Stage
import scalafx.scene.Scene
import scalafx.animation.{FadeTransition, Interpolator}
import scalafx.util.Duration
import scalafx.application.Platform
import scalafx.Includes._
import util.Observable
import util.Observer
//object SceneManager {
//  private var stage: Stage = _
//  private var lastSceneWidth: Double = 800  // Default width
//  private var lastSceneHeight: Double = 600 // Default height
//
//  def init(primaryStage: Stage): Unit = {
//    stage = primaryStage
//    stage.width = lastSceneWidth
//    stage.height = lastSceneHeight
//    stage.show()
//  }
//
//  def switchScene(newScene: Scene): Unit = {
//    val oldSceneOpt = Option(stage.scene) // ✅ Get current scene safely
//    lastSceneWidth = stage.width()
//    lastSceneHeight = stage.height()
//
//    oldSceneOpt match {
//      case Some(oldScene) if oldScene.root.value != null => // ✅ Check if old scene exists and has a root
//        val fadeOut = new FadeTransition(Duration(200), oldScene.root.value)
//        fadeOut.toValue = 0.2
//        fadeOut.interpolator = Interpolator.EaseOut
//
//        fadeOut.setOnFinished(_ => Platform.runLater(() => {
//          stage.scene = newScene
//          applySceneSize()
//
//          val fadeIn = new FadeTransition(Duration(500), newScene.root.value)
//          fadeIn.fromValue = 0.2
//          fadeIn.toValue = 1.0
//          fadeIn.interpolator = Interpolator.EaseIn
//          fadeIn.play()
//        }))
//
//        fadeOut.play()
//
//      case _ => // ✅ First scene (or no previous scene)
//        stage.scene = newScene
//        applySceneSize()
//    }
//  }
//
//  private def applySceneSize(): Unit = {
//    stage.width = lastSceneWidth
//    stage.height = lastSceneHeight
//  }
//
//
//  def refreshCurrentScene(): Unit = {
//    currentScene.foreach(_.root.requestLayout()) // ✅ Force scene refresh
//  }
//}

//package view.scenes // ✅ Ensure this matches the correct package path

import scalafx.application.Platform
import scalafx.scene.Scene
import scalafx.stage.Stage
import util.Observable

object SceneManager extends Observable { // ✅ SceneManager is now Observable
  private var stage: Stage = _
  private var lastSceneWidth: Double = 800  // Default width
  private var lastSceneHeight: Double = 600 // Default height
  private var currentScene: Option[Scene] = None

  def init(primaryStage: Stage): Unit = {
    stage = primaryStage
  }

  def switchScene(newScene: Scene): Unit = {
    Platform.runLater(() => {
      val oldSceneOpt = Option(stage.scene)
      lastSceneWidth = stage.width()
      lastSceneHeight = stage.height()

      oldSceneOpt match {
        case Some(oldScene) if oldScene.root.value != null =>
          val fadeOut = new FadeTransition(Duration(200), oldScene.root.value)
          fadeOut.toValue = 0.2
          fadeOut.interpolator = Interpolator.EaseOut

          fadeOut.setOnFinished(_ => Platform.runLater(() => {
            stage.scene = newScene
            currentScene = Some(newScene)
            applySceneSize()

            val fadeIn = new FadeTransition(Duration(500), newScene.root.value)
            fadeIn.fromValue = 0.2
            fadeIn.toValue = 1.0
            fadeIn.interpolator = Interpolator.EaseIn
            fadeIn.play()

            notifyObservers() // ✅ Notify GUI and TUI when scene changes
          }))

          fadeOut.play()

        case _ =>
          stage.scene = newScene
          currentScene = Some(newScene)
          applySceneSize()
          notifyObservers() // ✅ Ensure observers (TUI/GUI) are notified
      }
    })
  }

  private def applySceneSize(): Unit = {
    stage.width = lastSceneWidth
    stage.height = lastSceneHeight
  }

  def refreshCurrentScene(): Unit = {
    Platform.runLater(() => {
      currentScene.foreach(_.root.value.requestLayout())
      notifyObservers() // ✅ Ensure TUI updates when GUI changes scenes
    })
  }
}
