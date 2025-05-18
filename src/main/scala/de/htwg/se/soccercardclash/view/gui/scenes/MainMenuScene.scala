package de.htwg.se.soccercardclash.view.gui.scenes

import de.htwg.se.soccercardclash.controller.IController
import de.htwg.se.soccercardclash.controller.base.Controller
import de.htwg.se.soccercardclash.util.*
import de.htwg.se.soccercardclash.view.gui.components.dialog.DialogFactory
import de.htwg.se.soccercardclash.view.gui.components.sceneComponents.GameLabel
import de.htwg.se.soccercardclash.view.gui.components.uiFactory.GameButtonFactory
import de.htwg.se.soccercardclash.view.gui.overlay.Overlay
import de.htwg.se.soccercardclash.view.gui.scenes.sceneManager.SceneManager
import de.htwg.se.soccercardclash.view.gui.utils.Styles
import scalafx.application.Platform
import scalafx.collections.ObservableBuffer
import scalafx.geometry.Pos
import scalafx.scene.Scene
import scalafx.scene.control.ListView
import scalafx.scene.layout.{BorderPane, HBox, StackPane, VBox}
import scalafx.scene.text.Font
import scalafx.scene.control.Label
import scalafx.scene.image.{Image, ImageView}
class MainMenuScene(controller: IController) extends GameScene {

  this.getStylesheets.add(Styles.mainMenuCss)
  Font.loadFont(getClass.getResourceAsStream("/fonts/Rajdhani/Rajdhani-SemiBold.ttf"), 10)
  val overlay = new Overlay(this)
  private val logoBox = new VBox {
    spacing = 10
    alignment = Pos.Center
    children = Seq(
      new ImageView(new Image(getClass.getResource("/images/data/logo.png").toExternalForm)) {
        fitWidth = 300
        preserveRatio = true
        smooth = true
      }
    )
  }
  private val rootBox = new VBox {
    spacing = 10
    alignment = Pos.Center
    children = Seq(
      GameButtonFactory.createGameButton("Singleplayer", 200, 200) {
        () => GlobalObservable.notifyObservers(SceneSwitchEvent.CreatePlayerWithAI)
      },
      GameButtonFactory.createGameButton("Multiplayer", 200, 200) {
        () => GlobalObservable.notifyObservers(SceneSwitchEvent.CreatePlayer)
      },
      GameButtonFactory.createGameButton("Load Game", 200, 200) {
        () => GlobalObservable.notifyObservers(SceneSwitchEvent.LoadGame)
      },
      GameButtonFactory.createGameButton("About", 200, 200) {
        () => DialogFactory.showGameInfoDialog(overlay)
      },
      GameButtonFactory.createGameButton("Quit", 200, 200) {
        () => controller.quit()
      },
      overlay.getPane
    )
  }
  this.root = new StackPane {
    children = Seq(
      new VBox {
        spacing = 30
        alignment = Pos.Center
        children = Seq(
          logoBox,
          rootBox
        )
      },

      overlay.getPane
    )
    styleClass += "logo-image"
  }

}
