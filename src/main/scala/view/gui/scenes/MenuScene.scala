package view.gui.scenes

import view.gui.components.sceneView.MenuButtonBar
import controller.IController
import scalafx.application.Platform
import scalafx.geometry.Pos
import view.gui.scenes.sceneManager.SceneManager
import scalafx.scene.Scene
import scalafx.scene.layout.{HBox, Region, StackPane, VBox}
import util.{ObservableEvent, Observer}
import view.gui.utils.Styles
import scalafx.scene.Scene
import scalafx.scene.image.{Image, ImageView}
import scalafx.scene.layout.{StackPane, VBox}
import scalafx.geometry.Pos

class MenuScene(
                 controller: IController,
                 playingFieldScene: PlayingFieldScene,
                 sceneManager: SceneManager.type
               ) extends Scene(new StackPane) with Observer { // ✅ Now also an Observer

  controller.add(this) // ✅ Register as an observer

  // Background Image
  val backgroundImage = new ImageView(new Image("/images/data/images/menu.png")) {
    fitWidth = 800  // ✅ Adjust dynamically if needed
    fitHeight = 600
    preserveRatio = false
  }

  // Menu Layout
  val menuLayout = new VBox {
    spacing = 20
    alignment = Pos.Center
    val menuButtonBar = new MenuButtonBar(controller, playingFieldScene, sceneManager)
    children = Seq(
      menuButtonBar.continueButton,
      menuButtonBar.undoButton,
      menuButtonBar.redoButton,
      menuButtonBar.saveGameButton,
      menuButtonBar.mainMenuButton
    )
  }

  // Set root node
  root = new StackPane {
    children = Seq(
      backgroundImage,
      menuLayout
    )
  }

  // ✅ Observer update method
  override def update(e: ObservableEvent): Unit = {
    Platform.runLater(() => {
      println("🔄 MenuScene Updating!")
      SceneManager.update(e)
    })
  }
}
