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

import scalafx.scene.effect.GaussianBlur
import scalafx.scene.layout.VBox

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
  private val rootVBox = new VBox {
    spacing = 10
    alignment = Pos.Center
    children = Seq(
      new GameLabel("Select a Saved Game"),
      listView,
      backButton
    )
  }

  this.root = rootVBox

  // Populate ListView when Scene is Loaded
  loadSavedGames()

  // Show a confirmation dialog when loading a game with a blur effect
  private def showLoadConfirmation(gameFile: String): Unit = {
    val blurEffect = new GaussianBlur(3) // Adjust blur radius as needed
    rootVBox.effect = blurEffect // Apply blur effect to the root VBox

    val confirmed = GameAlert.show("Load Game", s"Load game: $gameFile?\nDo you want to proceed?")

    rootVBox.effect = null // Remove blur effect after closing the alert

    if (confirmed) {
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
