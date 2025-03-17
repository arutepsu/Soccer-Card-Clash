package view.gui.scenes

import scalafx.scene.Scene
import scalafx.scene.layout.VBox
import scalafx.scene.text.Text
import scalafx.collections.ObservableBuffer
import scalafx.geometry.Pos
import java.io.File
import scala.io.Source
import sceneManager.SceneManager
import scalafx.collections.ObservableBuffer
import scalafx.geometry.Pos
import scalafx.scene.Scene
import scalafx.scene.control.ListView
import scalafx.scene.layout.VBox
import view.gui.components.GameLabel
import view.gui.components.uiFactory.GameButtonFactory
import view.gui.scenes.sceneManager.SceneManager
import controller.base.Controller
import controller.{Events, IController}
import view.gui.utils.Styles
import scalafx.application.Platform
import util.{ObservableEvent, Observer}
import scalafx.scene.Scene
import scalafx.scene.control.{ListView, Alert}
import scalafx.scene.control.Alert.AlertType
import scalafx.scene.layout.VBox
import scalafx.geometry.Pos
import scalafx.collections.ObservableBuffer
import scalafx.application.Platform
import java.io.File
import scalafx.scene.Scene

import scalafx.scene.Scene
import scalafx.scene.control.{ListView, Alert, ButtonType}
import scalafx.scene.control.Alert.AlertType
import scalafx.scene.layout.VBox
import scalafx.geometry.Pos
import scalafx.collections.ObservableBuffer
import scalafx.application.Platform
import java.io.File

class LoadGameScene(controller: IController) extends Scene with Observer {
  controller.add(this)
  // Directory where saved games are stored
  private val gamesFolder = new File("games")

  // Observable list for saved games
  private val savedGames = ObservableBuffer[String]()

  // Load saved games from the folder
  private def loadSavedGames(): Unit = {
    savedGames.clear()
    if (gamesFolder.exists() && gamesFolder.isDirectory) {
      val files = gamesFolder.listFiles()
      if (files != null) {
        savedGames ++= files
          .filter(f => f.isFile && (f.getName.endsWith(".xml") || f.getName.endsWith(".json")))
          .map(_.getName)
      }
    }
  }

  // UI Elements
  private val listView = new ListView(savedGames)
  private val backButton = GameButtonFactory.createGameButton("Back", 150, 50) {
    () => controller.notifyObservers(Events.MainMenu)
  }

  // ListView Click Event: Open Confirmation on Selection
  listView.onMouseClicked = _ => {
    val selectedGame = listView.selectionModel().getSelectedItem
    if (selectedGame != null) {
      showLoadConfirmation(selectedGame)
    }
  }

  // Root Layout
  this.root = new VBox {
    spacing = 10
    alignment = Pos.Center
    children = Seq(
      new GameLabel("Select a Saved Game"),
      listView,
      backButton
    )
  }

  // Populate ListView when Scene is Loaded
  loadSavedGames()

  // Show a confirmation dialog when loading a game
  private def showLoadConfirmation(gameFile: String): Unit = {
    val alert = new Alert(AlertType.Confirmation) {
      title = "Load Game"
      headerText = s"Load game: $gameFile?"
      contentText = "Do you want to proceed?"
    }

    val result = alert.showAndWait()
    if (result.contains(ButtonType.OK)) {
      controller.loadGame(gameFile)
    }
  }

  // Observer Pattern: Handle Scene Transitions
  override def update(e: ObservableEvent): Unit = {
    Platform.runLater(() => {
      println(s"🔄 GUI Received Event: $e")
      SceneManager.update(e)
    })
  }
}
