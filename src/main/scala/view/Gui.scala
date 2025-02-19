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
import util.*
import scalafx.application.{JFXApp3, Platform}
import controller.ControllerEvents

class Gui(controller: IController) extends JFXApp3 with Observer {

  override def start(): Unit = {
    val stage = new PrimaryStage {
      title = "Soccer Card Game"
      icons.add(new Image(getClass.getResource("/images/data/logo.png").toExternalForm))
    }

    SceneManager.init(stage, controller)
    controller.notifyObservers(ControllerEvents.MainMenu)
  }

  /** ✅ Observer Pattern: Refresh the GUI whenever notified */
  override def update(e: ObservableEvent): Unit = {
    Platform.runLater(() => {
      println(s"🔄 GUI Received Event: $e")
      SceneManager.update(e)
    })
  }
}
