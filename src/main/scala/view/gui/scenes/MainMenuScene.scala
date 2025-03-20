package view.gui.scenes

import sceneManager.SceneManager
import scalafx.collections.ObservableBuffer
import scalafx.geometry.Pos
import scalafx.scene.Scene
import scalafx.scene.control.ListView
import scalafx.scene.layout.VBox
import view.gui.components.uiFactory.GameButtonFactory
import view.gui.scenes.sceneManager.SceneManager
import controller.base.Controller
import controller.{Events, IController}
import view.gui.utils.Styles
import scalafx.application.Platform
import util.{ObservableEvent, Observer}
import view.gui.components.sceneComponents.GameLabel

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
