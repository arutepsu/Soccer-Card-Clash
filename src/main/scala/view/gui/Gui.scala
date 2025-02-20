package view.gui

import controller.{Events, IController}
import scalafx.application.JFXApp3.PrimaryStage
import scalafx.application.{JFXApp3, Platform}
import scalafx.scene.image.Image
import scalafx.stage.Stage
import util.*
import view.gui.scenes.MainMenuScene
import view.gui.scenes.sceneManager.SceneManager

class Gui(controller: IController) extends JFXApp3 with Observer {

  override def start(): Unit = {
    val stage = new PrimaryStage {
      title = "Soccer Card Game"
      icons.add(new Image(getClass.getResource("/images/data/logo.png").toExternalForm))
    }

    SceneManager.init(stage, controller)
    controller.notifyObservers(Events.MainMenu)
  }

  /** ✅ Observer Pattern: Refresh the GUI whenever notified */
  override def update(e: ObservableEvent): Unit = {
    Platform.runLater(() => {
      println(s"🔄 GUI Received Event: $e")
      SceneManager.update(e)
    })
  }
}
