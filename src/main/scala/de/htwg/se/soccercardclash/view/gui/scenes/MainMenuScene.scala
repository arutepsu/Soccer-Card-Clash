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
import scalafx.geometry.{Insets, Pos}
import scalafx.scene.Scene
import scalafx.scene.control.{Label, ListView}
import scalafx.scene.image.{Image, ImageView}
import scalafx.scene.layout.{BorderPane, HBox, StackPane, VBox}
import scalafx.scene.paint.Color
import scalafx.scene.text.Font
class MainMenuScene(controller: IController) extends GameScene {

  this.getStylesheets.add(Styles.mainMenuCss)
  Font.loadFont(getClass.getResourceAsStream("/fonts/Rajdhani/Rajdhani-SemiBold.ttf"), 10)
  val overlay = new Overlay(this)
  private val logoBox = new VBox {
    spacing = 10
    alignment = Pos.Center
    children = Seq(
      new ImageView(new Image(getClass.getResource("/images/data/logo/logo1k.png").toExternalForm)) {
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

  val bottomSpacer = new HBox {
    minHeight = 70
    visible = false
  }
  StackPane.setAlignment(bottomSpacer, Pos.BottomCenter)

  // Load font properly
  val fontStream = getClass.getResourceAsStream("/fonts/Rajdhani/Rajdhani-Medium.ttf")
  val loadedFont = Font.loadFont(fontStream, 12)

  // Get the actual font name safely
  val actualFontName = Option(loadedFont).map(_.getName).getOrElse("System")

  val infoLabel = new Label("Developed by Arutepsu") {
    font = Font.font(actualFontName, 32)
    textFill = Color.web("#39ff14")
    padding = Insets(0, 10, 10, 0)
  }
  StackPane.setAlignment(infoLabel, Pos.BottomRight)

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
