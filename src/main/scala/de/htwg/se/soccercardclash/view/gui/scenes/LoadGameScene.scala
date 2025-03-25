package de.htwg.se.soccercardclash.view.gui.scenes

import scalafx.scene.Scene
import scalafx.scene.layout.{HBox, StackPane, VBox}
import scalafx.scene.text.Text
import scalafx.scene.control.{Button, ListView}
import scalafx.collections.ObservableBuffer
import scalafx.geometry.Pos
import scalafx.application.Platform
import scalafx.scene.Node

import java.io.File
import de.htwg.se.soccercardclash.view.gui.overlay.Overlay
import de.htwg.se.soccercardclash.view.gui.components.uiFactory.GameButtonFactory
import de.htwg.se.soccercardclash.controller.IController
import de.htwg.se.soccercardclash.util.{Events, ObservableEvent, Observer}
import de.htwg.se.soccercardclash.view.gui.scenes.sceneManager.SceneManager
import scalafx.scene.Scene
import scalafx.scene.layout.{HBox, StackPane, VBox}
import scalafx.scene.text.Text
import scalafx.scene.control.{Button, ListView}
import scalafx.collections.ObservableBuffer
import scalafx.geometry.Pos
import scalafx.application.Platform
import scalafx.scene.Node

import java.io.File
import de.htwg.se.soccercardclash.view.gui.overlay.Overlay
import de.htwg.se.soccercardclash.view.gui.components.uiFactory.GameButtonFactory
import de.htwg.se.soccercardclash.controller.IController
import de.htwg.se.soccercardclash.util.{ObservableEvent, Observer}
import de.htwg.se.soccercardclash.view.gui.scenes.sceneManager.SceneManager
import de.htwg.se.soccercardclash.view.gui.components.dialog.{ConfirmationDialog, DialogFactory}
import de.htwg.se.soccercardclash.view.gui.components.sceneComponents.GameLabel

class LoadGameScene(controller: IController) extends Scene with Observer {
  controller.add(this)

  // 📂 Directory where saved games are stored
  private val gamesFolder = new File("games")

  // 📌 Observable list for saved games
  private val savedGames = ObservableBuffer[String]()

  // 🔄 Load saved games from the folder
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

  // 📌 Overlay for dialogs
  private val overlay = new Overlay(this)

  // 📜 ListView for saved games
  private val listView = new ListView(savedGames)

  // 🔙 Back button to main menu
  private val backButton = GameButtonFactory.createGameButton("Back", 150, 50) {
    () => controller.notifyObservers(Events.MainMenu)
  }

  // 🖱️ ListView Click Event: Open Confirmation on Selection
  listView.onMouseClicked = _ => {
    val selectedGame = listView.selectionModel().getSelectedItem
    if (selectedGame != null) {
      showLoadConfirmation(selectedGame)
    }
  }

  // 🏗️ Root Layout
  private val rootVBox = new VBox {
    spacing = 10
    alignment = Pos.Center
    children = Seq(
      new GameLabel("Select a Saved Game"),
      listView,
      backButton
    )
  }

  // ✅ Add Overlay to Scene
  this.root = new StackPane {
    children = Seq(rootVBox, overlay.getPane)
  }

  // 🔄 Populate ListView when Scene is Loaded
  loadSavedGames()

  private def showLoadConfirmation(gameFile: String): Unit = {
    Platform.runLater(() => {
      println(s"📢 Showing Load Confirmation for: $gameFile") // Debugging

      // ✅ Use DialogFactory instead of creating ConfirmationDialog manually
      DialogFactory.showLoadGameConfirmation(gameFile, overlay, controller)
    })
  }



  // 🔄 Observer Pattern: Handle Scene Transitions
  override def update(e: ObservableEvent): Unit = {
    Platform.runLater(() => {
      println(s"🔄 GUI Received Event: $e")
      SceneManager.update(e)
    })
  }
}
