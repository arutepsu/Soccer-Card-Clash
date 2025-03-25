package de.htwg.se.soccercardclash.view.gui.scenes

import scalafx.collections.ObservableBuffer
import scalafx.geometry.Pos
import scalafx.scene.Scene
import scalafx.scene.control.ListView
import scalafx.scene.layout.VBox
import de.htwg.se.soccercardclash.view.gui.components.uiFactory.GameButtonFactory
import de.htwg.se.soccercardclash.view.gui.scenes.sceneManager.SceneManager
import de.htwg.se.soccercardclash.controller.base.Controller
import de.htwg.se.soccercardclash.controller.IController
import de.htwg.se.soccercardclash.view.gui.utils.Styles
import scalafx.application.Platform
import de.htwg.se.soccercardclash.util.{Events, ObservableEvent, Observer}
import de.htwg.se.soccercardclash.view.gui.components.sceneComponents.GameLabel

class MainMenuScene(controller: IController) extends Scene with Observer {
  controller.add(this)
  this.getStylesheets.add(Styles.mainMenuCss)
  root = new VBox {
    spacing = 10
    alignment = Pos.Center
    children = Seq(
      new GameLabel("Soccer Card Clash", 1.5) {
        styleClass.add("title-label")
      },
      GameButtonFactory.createGameButton("Create New Game", 200, 80) {
        () => controller.notifyObservers(Events.CreatePlayers)
      },
      GameButtonFactory.createGameButton("Load Game", 200, 80) {
        () => controller.notifyObservers(Events.LoadGame)
      },
      GameButtonFactory.createGameButton("Quit", 200, 80) {
        () => controller.notifyObservers(Events.Quit)
      }
    )
  }

  override def update(e: ObservableEvent): Unit = {
    Platform.runLater(() => {
      println(s"🔄 GUI Received Event: $e")
      SceneManager.update(e)
    })
  }
}
