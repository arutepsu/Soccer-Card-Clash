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
import util._
import scalafx.application.Platform
import controller.ControllerEvents

class Gui(controller: IController) extends Observer{
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
  override def update(e: ObservableEvent): Unit = {
    Platform.runLater(() => {
      println(s"🔄 GUI Updating: Event - $e")

      e match {
        case ControllerEvents.MainMenu =>
          println("📌 Switching to Main Menu!")
          SceneManager.switchScene(new MainMenuScene(controller).mainMenuScene())

        case _ => println("🔕 Ignoring event, only Main Menu updates GUI.")
      }
    })
  }
}