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
class MainMenuScene(controller: IController) extends Observer { // ✅ Now an Observer
  controller.add(this)
  // Placeholder for saved games
  val savedGames = ObservableBuffer("Game 1", "Game 2", "Game 3")

  // Main menu scene
  def mainMenuScene(): Scene = new Scene {
    this.getStylesheets.add(Styles.mainMenuCss)

    root = new VBox {
      spacing = 10
      alignment = Pos.Center
      children = Seq(
        new GameLabel("Soccer Card Clash", 1.5) {
          styleClass.add("title-label") // ✅ Apply custom title style
        },
        GameButtonFactory.createGameButton("Create New Game", 200, 80) {
          () => SceneManager.switchScene(createGameScene()) // ✅ SceneManager handles switching
        },
        GameButtonFactory.createGameButton("Load Game", 200, 80) {
          () => SceneManager.switchScene(loadGameScene()) // ✅ SceneManager handles switching
        },
        GameButtonFactory.createGameButton("Quit", 200, 80) {
          () => sys.exit(0)
        }
      )
    }
  }


  // Create game scene
  def createGameScene(): Scene = new Scene {
    root = new CreatePlayerCard(controller)
  }

  // Load game scene
  def loadGameScene(): Scene = {
    val listView = new ListView(savedGames)
    val backButton = GameButtonFactory.createGameButton("Back", 150, 50) {
      () => SceneManager.switchScene(mainMenuScene()) // ✅ Switch back to menu
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

  override def update(e: ObservableEvent): Unit = {
    Platform.runLater(() => {
      println("🔄 Main Menu Updating!")
//      e match {
//        case ControllerEvents.MainMenu =>
//          println("📌 Switching to Main Menu!")
//          SceneManager.switchScene(new MainMenuScene(controller).mainMenuScene())
//
//        case _ => println("🔕 Ignoring event, only Main Menu updates GUI.")
//      }
      SceneManager.refreshCurrentScene()
    })
  }
}