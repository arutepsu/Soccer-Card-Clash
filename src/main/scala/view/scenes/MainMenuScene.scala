
package view.scenes

import controller.Controller
import scalafx.Includes.jfxScene2sfx
import scalafx.collections.ObservableBuffer
import scalafx.geometry.Pos
import scalafx.scene.Scene
import scalafx.scene.SceneIncludes.jfxScene2sfx
import scalafx.scene.control.{Label, ListView, TextField}
import scalafx.scene.layout.VBox
import scalafx.stage.Stage
import view.components.GameLabel
import view.components.playerComponents.PlayerTextInputField
import view.components.uiFactory.{CardAnimationFactory, GameButtonFactory}
class MainMenuScene(stage: Stage, controller: Controller) { // Accepts stage as a parameter

  // Placeholder for saved games
  val savedGames = ObservableBuffer("Game 1", "Game 2", "Game 3")

  // Main menu scene
  def mainMenuScene(): Scene = new Scene {
    root = new VBox {
      spacing = 10
      alignment = Pos.Center
      children = Seq(
        new GameLabel("Soccer Card Game", 1.5),
        GameButtonFactory.createGameButton("Create New Game", 200, 50) {
          () => stage.scene = createGameScene() // Fixes missing parameters
        },
        GameButtonFactory.createGameButton("Load Game", 200, 50) {
          () => stage.scene = loadGameScene()
        },
        GameButtonFactory.createGameButton("Quit", 200, 50) {
          () => sys.exit(0)
        }
      )
    }
  }

  // Calls CreatePlayerCard to transition to player creation
  def createGameScene(): Scene = {
    new Scene {
      root = new CreatePlayerCard(stage, stage.width.value, stage.height.value, controller) // FIXED: Pass required parameters
    }
  }

  // Load game scene
  def loadGameScene(): Scene = {
    val listView = new ListView(savedGames)
    val backButton = GameButtonFactory.createGameButton("Back", 150, 50) {
      () => stage.scene = mainMenuScene()
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

  // Set initial scene
  stage.scene = mainMenuScene()
}
