//package view
//
//import scalafx.application.JFXApp3.PrimaryStage
//import controller.IController
//import scalafx.stage.Stage
//import view.scenes.MainMenuScene
//import scalafx.stage.Stage
//import scalafx.scene.image.Image
//import view.scenes.sceneManager.SceneManager
//import view.scenes.MainMenuScene
//import controller.BaseControllerImplementation.Controller
//class Gui(controller: IController) {
//  def start(): Unit = {
//    // ✅ Use PrimaryStage from ScalaFX 3.x
//    val stage = new PrimaryStage {
//      title = "Soccer Card Game" // Set the window title
//      icons.add(new Image("/view/data/logo.png"))
//    }
//
////    // ✅ Pass the initialized stage to SoccerCardGame
//    SceneManager.init(stage) // ✅ Initialize SceneManager
//    SceneManager.switchScene(new MainMenuScene(controller).mainMenuScene()) // ✅ Set initial scene
//  }
//}
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

class Gui(controller: IController) {
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
}
