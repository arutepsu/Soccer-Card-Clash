package de.htwg.se.soccercardclash.view.gui.scenes

import de.htwg.se.soccercardclash.controller.IController
import de.htwg.se.soccercardclash.util.*
import de.htwg.se.soccercardclash.view.gui.components.dialog.{ConfirmationDialog, DialogFactory}
import de.htwg.se.soccercardclash.view.gui.components.sceneComponents.GameLabel
import de.htwg.se.soccercardclash.view.gui.components.uiFactory.GameButtonFactory
import de.htwg.se.soccercardclash.view.gui.overlay.Overlay
import de.htwg.se.soccercardclash.view.gui.scenes.sceneManager.SceneManager
import de.htwg.se.soccercardclash.view.gui.utils.Styles
import scalafx.application.Platform
import scalafx.collections.ObservableBuffer
import scalafx.geometry.Pos
import scalafx.scene.{Node, Scene}
import scalafx.scene.control.{Button, ListView}
import scalafx.scene.layout.{HBox, StackPane, VBox}
import scalafx.scene.text.Text

import java.io.File

class LoadGameScene(controller: IController) extends GameScene {

  private val gamesFolder = new File("games")
  private val savedGames = ObservableBuffer[String]()
  this.getStylesheets.add(Styles.loadGameCss)

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

  private val overlay = new Overlay(this)

  private val listView = new ListView(savedGames) {
    styleClass += "custom-list-view"
    onMouseClicked = _ => {
      val selectedGame = selectionModel().getSelectedItem
      if (selectedGame != null) {
        showLoadConfirmation(selectedGame)
      }
    }
  }

  private val backButton = GameButtonFactory.createGameButton("Back", 150, 50) {
    () => GlobalObservable.notifyObservers(SceneSwitchEvent.MainMenu)
  }

  private val rootVBox = new VBox {
    spacing = 10
    alignment = Pos.Center
    children = Seq(
      new GameLabel("Select a Saved Game"),
      listView,
      backButton
    )
  }

  this.root = new StackPane {
    children = Seq(rootVBox, overlay.getPane)
    styleClass += "root"
  }

  overlay.getPane.prefWidth = 800
  overlay.getPane.prefHeight = 600
  overlay.getPane.visible = false

  loadSavedGames()

  override def handleStateEvent(e: StateEvent): Unit = {
  }

  private def showLoadConfirmation(gameFile: String): Unit = {
    Platform.runLater {
      DialogFactory.showLoadGameConfirmation(gameFile, overlay, controller)
    }
  }
}
