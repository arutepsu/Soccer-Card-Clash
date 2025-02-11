package view.scenes.sceneManager

import scalafx.scene.Scene
import scalafx.stage.Stage

object SceneManager {
  private var stage: Option[Stage] = None

  def init(primaryStage: Stage): Unit = {
    stage = Some(primaryStage)
  }

  def switchScene(newScene: Scene): Unit = {
    stage match {
      case Some(s) => s.scene = newScene
//        s.fullScreen = true // ✅ Ensure the stage stays fullscreen after switching
      case None    => println("❌ Error: Stage is not initialized in SceneManager!")
    }
  }
}
