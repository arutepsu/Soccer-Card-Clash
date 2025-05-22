package de.htwg.se.soccercardclash.view.gui.scenes

import de.htwg.se.soccercardclash.controller.IController
import de.htwg.se.soccercardclash.controller.base.Controller
import de.htwg.se.soccercardclash.util.*
import de.htwg.se.soccercardclash.view.gui.components.dialog.DialogFactory
import de.htwg.se.soccercardclash.view.gui.components.sceneComponents.GameLabel
import de.htwg.se.soccercardclash.view.gui.components.uiFactory.GameButtonFactory
import de.htwg.se.soccercardclash.view.gui.overlay.Overlay
import de.htwg.se.soccercardclash.view.gui.scenes.sceneManager.SceneManager
import de.htwg.se.soccercardclash.view.gui.utils.{Assets, Styles}
import scalafx.application.Platform
import scalafx.collections.ObservableBuffer
import scalafx.geometry.{Insets, Pos}
import scalafx.scene.Scene
import scalafx.scene.control.{Label, ListView}
import scalafx.scene.image.{Image, ImageView}
import scalafx.scene.layout.{BorderPane, HBox, StackPane, VBox}
import scalafx.scene.paint.Color
import scalafx.scene.text.Font

class MainMenuScene(controller: IController) extends GameScene {

  this.getStylesheets.add(Styles.mainMenuCss)
  val overlay = new Overlay(this)
  val bottomSpacer = new HBox {
    minHeight = 40
    visible = false
  }
  val infoLabel = new Label("Developed by Arutepsu") {
    styleClass += "info-label"
  }
  private val logoBox = new VBox {
    alignment = Pos.TOP_CENTER
    children = Seq(Assets.createLogoImageView())
  }
  StackPane.setAlignment(bottomSpacer, Pos.BottomCenter)
  private val rootBox = new VBox {
    spacing = 20
    alignment = Pos.Center
    children = Seq(
      GameButtonFactory.createGameButton("Singleplayer", 200, 150) {
        () => GlobalObservable.notifyObservers(SceneSwitchEvent.CreatePlayerWithAI)
      },
      GameButtonFactory.createGameButton("Multiplayer", 200, 150) {
        () => GlobalObservable.notifyObservers(SceneSwitchEvent.CreatePlayer)
      },
      GameButtonFactory.createGameButton("Load Game", 200, 150) {
        () => GlobalObservable.notifyObservers(SceneSwitchEvent.LoadGame)
      },
      GameButtonFactory.createGameButton("About", 200, 150) {
        () => DialogFactory.showGameInfoDialog(overlay)
      },
      GameButtonFactory.createGameButton("Quit", 200, 150) {
        () => controller.quit()
      },
      overlay.getPane
    )
  }

  StackPane.setAlignment(infoLabel, Pos.BottomCenter)

  this.root = new StackPane {
    children = Seq(
      new VBox {
        spacing = 30
        alignment = Pos.Center
        children = Seq(
          logoBox,
          rootBox,
          bottomSpacer,
        )
      },
      overlay.getPane,
      infoLabel
    )
    styleClass += "logo-image"
  }

}
