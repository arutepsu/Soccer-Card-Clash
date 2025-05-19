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
import scalafx.geometry.{Insets, Pos}
import scalafx.scene.{Node, Scene}
import scalafx.scene.control.{Button, ListView}
import scalafx.scene.layout.{HBox, StackPane, VBox}
import scalafx.scene.text.{Font, Text}
import scalafx.scene.control.Label

import java.io.File
import de.htwg.se.soccercardclash.controller.IGameContextHolder
class LoadGameScene(controller: IController, gameContextHolder: IGameContextHolder) extends GameScene {

  Font.loadFont(getClass.getResourceAsStream("/fonts/Rajdhani/Rajdhani-Regular.ttf"), 20)
  Font.loadFont(getClass.getResourceAsStream("/fonts/Rajdhani/Rajdhani-Bold.ttf"), 20)
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
    prefWidth = 50
    prefHeight = 400
    styleClass += "custom-list-view"
    onMouseClicked = _ => {
      val selectedGame = selectionModel().getSelectedItem
      if (selectedGame != null) {
        showLoadConfirmation(selectedGame)
      }
    }
  }


  private val backButton = GameButtonFactory.createGameButton("Back", 200, 100) {
    () => GlobalObservable.notifyObservers(SceneSwitchEvent.MainMenu)
  }
  val label = new VBox {
    spacing = 20
    alignment = Pos.CENTER
    padding = Insets(20)

    val titleLabel = new Label("Select a Saved Game") {
      styleClass += "title"
    }

    children = Seq(titleLabel)
  }

  private val rootVBox = new VBox {
    spacing = 20
    alignment = Pos.CENTER
    padding = Insets(20)
    children = Seq(
      label,
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
      DialogFactory.showLoadGameConfirmation(gameFile, overlay, controller, gameContextHolder)
    }
  }
}
