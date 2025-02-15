package view

import scalafx.application.JFXApp3.PrimaryStage
import controller.IController
import scalafx.stage.Stage
import view.scenes.MainMenuScene
import scalafx.stage.Stage
import scalafx.scene.image.Image
import view.scenes.sceneManager.SceneManager
import view.scenes.MainMenuScene
import controller.IController
import util.Observer
import scalafx.application.Platform

class Gui(controller: IController)extends Observer{
  controller.add(this)
  def start(): Unit = {
    // ✅ Use PrimaryStage from ScalaFX 3.x
    val stage = new PrimaryStage {
      title = "Soccer Card Game" // Set the window title
      icons.add(new Image("/view/data/logo.png"))
    }

    //    // ✅ Pass the initialized stage to SoccerCardGame
    SceneManager.init(stage) // ✅ Initialize SceneManager
    SceneManager.switchScene(new MainMenuScene(controller).mainMenuScene()) // ✅ Set initial scene
  }

  /** ✅ Observer Pattern: Refresh the GUI whenever notified */
  override def update: Unit = {
    Platform.runLater(() => {
      println("🔄 GUI Updating!")
      SceneManager.refreshCurrentScene() // 🔄 Replace with your actual GUI update method
    })
  }
}