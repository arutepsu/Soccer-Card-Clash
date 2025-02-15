package view.scenes.sceneManager
//
import scalafx.stage.Stage
import scalafx.scene.Scene
import scalafx.animation.{FadeTransition, Interpolator}
import scalafx.util.Duration
import scalafx.application.Platform
import scalafx.Includes._

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
object SceneManager {
  private var stage: Stage = _
  private var lastSceneWidth: Double = 800  // Default width
  private var lastSceneHeight: Double = 600 // Default height
  private var currentScene: Option[Scene] = None // ✅ Add missing variable

  def init(primaryStage: Stage): Unit = {
    stage = primaryStage
    stage.width = lastSceneWidth
    stage.height = lastSceneHeight
    stage.show()
  }

  def switchScene(newScene: Scene): Unit = {
    val oldSceneOpt = Option(stage.scene) // ✅ Get current scene safely
    lastSceneWidth = stage.width()
    lastSceneHeight = stage.height()

    oldSceneOpt match {
      case Some(oldScene) if oldScene.root.value != null => // ✅ Check if old scene exists and has a root
        val fadeOut = new FadeTransition(Duration(200), oldScene.root.value)
        fadeOut.toValue = 0.2
        fadeOut.interpolator = Interpolator.EaseOut

        fadeOut.setOnFinished(_ => Platform.runLater(() => {
          stage.scene = newScene
          currentScene = Some(newScene) // ✅ Update `currentScene`
          applySceneSize()

          val fadeIn = new FadeTransition(Duration(500), newScene.root.value)
          fadeIn.fromValue = 0.2
          fadeIn.toValue = 1.0
          fadeIn.interpolator = Interpolator.EaseIn
          fadeIn.play()
        }))

        fadeOut.play()

      case _ => // ✅ First scene (or no previous scene)
        stage.scene = newScene
        currentScene = Some(newScene) // ✅ Update `currentScene`
        applySceneSize()
    }
  }

  private def applySceneSize(): Unit = {
    stage.width = lastSceneWidth
    stage.height = lastSceneHeight
  }

  def refreshCurrentScene(): Unit = {
    currentScene.foreach(scene => scene.root.value.requestLayout()) // ✅ Corrected
  }
}
