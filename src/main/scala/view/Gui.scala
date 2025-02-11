package view

import scalafx.application.JFXApp3.PrimaryStage
import controller.Controller
import scalafx.stage.Stage
import view.scenes.MainMenuScene

//class Gui(controller: Controller) {
//  def start(): Unit = {
//    // ✅ Use PrimaryStage from ScalaFX 3.x
//    val stage = new PrimaryStage {
//      title = "Soccer Card Game" // Set the window title
//    }
//
//    // ✅ Pass the initialized stage to SoccerCardGame
//    new MainMenuScene(stage, controller)
//
//    // ✅ Display the stage
//    stage.show()
//  }
//}
import controller.Controller
import scalafx.stage.Stage
import view.scenes.sceneManager.SceneManager
import view.scenes.MainMenuScene

class Gui(controller: Controller) {
  def start(): Unit = {
    // ✅ Use PrimaryStage from ScalaFX 3.x
    val stage = new PrimaryStage {
      title = "Soccer Card Game" // Set the window title
    }

//    // ✅ Pass the initialized stage to SoccerCardGame
    SceneManager.init(stage) // ✅ Initialize SceneManager
    SceneManager.switchScene(new MainMenuScene(controller).mainMenuScene()) // ✅ Set initial scene
  }
}
