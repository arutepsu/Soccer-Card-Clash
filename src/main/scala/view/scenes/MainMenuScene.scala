package view.scenes

import sceneManager.SceneManager
import scalafx.collections.ObservableBuffer
import scalafx.geometry.Pos
import scalafx.scene.Scene
import scalafx.scene.control.ListView
import scalafx.scene.layout.VBox
import view.components.GameLabel
import view.components.uiFactory.GameButtonFactory
import view.scenes.sceneManager.SceneManager
import controller.base.Controller
import controller.{ControllerEvents, IController}
import view.utils.Styles
import scalafx.application.Platform
import util.{ObservableEvent, Observer}

class MainMenuScene(controller: IController) extends Observer {

  // Placeholder for saved games
  val savedGames = ObservableBuffer("Game 1", "Game 2", "Game 3")

  // ✅ Main menu scene
  def mainMenuScene(): Scene = new Scene {
    this.getStylesheets.add(Styles.mainMenuCss)

    root = new VBox {
      spacing = 10
      alignment = Pos.Center
      children = Seq(
        new GameLabel("Soccer Card Clash", 1.5) {
          styleClass.add("title-label")
        },
        GameButtonFactory.createGameButton("Create New Game", 200, 80) {
          () => controller.notifyObservers(ControllerEvents.StartGame) // ✅ Notify Observers instead
        },
        GameButtonFactory.createGameButton("Load Game", 200, 80) {
          () => controller.notifyObservers(ControllerEvents.LoadGame) // ✅ Notify Observers instead
        },
        GameButtonFactory.createGameButton("Quit", 200, 80) {
          () => controller.notifyObservers(ControllerEvents.Quit)
        }
      )
    }
  }

  // ✅ Create Game Scene
  def createGameScene(): Scene = new Scene {
    root = new CreatePlayerCard(controller)
  }

  // ✅ Load Game Scene
  def loadGameScene(): Scene = {
    val listView = new ListView(savedGames)
    val backButton = GameButtonFactory.createGameButton("Back", 150, 50) {
      () => controller.notifyObservers(ControllerEvents.LoadGame) // ✅ Notify Observers instead
    }

    new Scene {
      root = new VBox {
        spacing = 10
        alignment = Pos.Center
        children = Seq(
          new GameLabel("Select a Saved Game"),
          listView,
          backButton
        )
      }
    }
  }

  // ✅ Observer Pattern: Handle Scene Transitions Here
  override def update(e: ObservableEvent): Unit = {
    Platform.runLater(() => {
      println(s"🔄 GUI Received Event: $e")

      // ✅ Instead of switching scenes manually, notify SceneManager
      SceneManager.update(e)
    })
  }
}
