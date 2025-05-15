package de.htwg.se.soccercardclash.view.gui.scenes

import de.htwg.se.soccercardclash.controller.IController
import de.htwg.se.soccercardclash.controller.base.Controller
import de.htwg.se.soccercardclash.util.*
import de.htwg.se.soccercardclash.view.gui.components.sceneComponents.GameLabel
import de.htwg.se.soccercardclash.view.gui.components.uiFactory.GameButtonFactory
import de.htwg.se.soccercardclash.view.gui.scenes.sceneManager.SceneManager
import de.htwg.se.soccercardclash.view.gui.utils.Styles
import scalafx.application.Platform
import scalafx.collections.ObservableBuffer
import scalafx.geometry.Pos
import scalafx.scene.Scene
import scalafx.scene.control.ListView
import scalafx.scene.layout.VBox

class MainMenuScene(controller: IController) extends GameScene {

  this.getStylesheets.add(Styles.mainMenuCss)

  root = new VBox {
    spacing = 10
    alignment = Pos.Center
    children = Seq(
      new GameLabel("Soccer Card Clash", 1.5) {
        styleClass.add("title-label")
      },
      GameButtonFactory.createGameButton("Singleplayer", 200, 80) {
        () => GlobalObservable.notifyObservers(SceneSwitchEvent.CreatePlayerWithAI)
      },
      GameButtonFactory.createGameButton("Multiplayer", 200, 80) {
        () => GlobalObservable.notifyObservers(SceneSwitchEvent.CreatePlayer)
      },
      GameButtonFactory.createGameButton("Load Game", 200, 80) {
        () => GlobalObservable.notifyObservers(SceneSwitchEvent.LoadGame)
      },
      GameButtonFactory.createGameButton("Quit", 200, 80) {
        () => controller.quit()
      }
    )
  }
}
