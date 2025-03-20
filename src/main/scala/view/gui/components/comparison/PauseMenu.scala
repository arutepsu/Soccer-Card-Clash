package view.gui.components.comparison

import controller.IController
import scalafx.application.Platform
import scalafx.geometry.Pos
import scalafx.scene.Scene
import scalafx.scene.image.{Image, ImageView}
import scalafx.scene.layout.{HBox, Region, StackPane, VBox}
import util.{ObservableEvent, Observer}
import view.gui.components.sceneView.MenuButtonBar
import view.gui.overlay.Overlay
import view.gui.scenes.PlayingFieldScene
import view.gui.scenes.sceneManager.SceneManager
import view.gui.utils.Styles

class PauseMenu(controller: IController,
                playingFieldScene: PlayingFieldScene,
                overlay: Overlay // ✅ Use OverlayPause
               ) {

  // ✅ Background Image
  private val backgroundImage = new ImageView(new Image("/images/data/frames/pause (1).png")) {
    fitWidth = 800
    fitHeight = 600
    smooth = true
    cache = true
  }

  // ✅ Menu Layout
  private val menuButtonBar = new MenuButtonBar(controller, playingFieldScene, overlay)

  private val menuLayout = new VBox {
    spacing = 20
    alignment = Pos.Center
    children = Seq(
      menuButtonBar.continueButton,
      menuButtonBar.undoButton,
      menuButtonBar.redoButton,
      menuButtonBar.saveGameButton,
      menuButtonBar.mainMenuButton
    )
  }

  // ✅ Create the Menu Pane
  private val menuPane = new StackPane {

    alignment = Pos.Center
    children = Seq(backgroundImage, menuLayout)
  }

  // ✅ Show the Menu Overlay inside `OverlayPause`
  def show(): Unit = {
    overlay.show(menuPane, false)
  }

  // ✅ Hide the Menu Overlay
  def hide(): Unit = {
    overlay.hide()
  }

  // ✅ Ensure Menu Closes When "Continue" is Pressed
  menuButtonBar.continueButton.onAction = _ => hide()
}
