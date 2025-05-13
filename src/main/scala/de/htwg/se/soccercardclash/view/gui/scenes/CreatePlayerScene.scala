package de.htwg.se.soccercardclash.view.gui.scenes

import com.google.inject.Inject
import de.htwg.se.soccercardclash.controller.{IController, IGameContextHolder}
import de.htwg.se.soccercardclash.util.*
import de.htwg.se.soccercardclash.view.gui.components.alert.GameAlertFactory
import de.htwg.se.soccercardclash.view.gui.components.uiFactory.GameButtonFactory
import de.htwg.se.soccercardclash.view.gui.overlay.Overlay
import de.htwg.se.soccercardclash.view.gui.scenes.sceneManager.SceneManager
import de.htwg.se.soccercardclash.view.gui.utils.Styles
import scalafx.application.Platform
import scalafx.geometry.{Insets, Pos}
import scalafx.scene.Scene
import scalafx.scene.control.TextField
import scalafx.scene.layout.{StackPane, VBox}
import scalafx.scene.text.Text

class CreatePlayerScene @Inject()(
                                   controller: IController,
                                   contextHolder: IGameContextHolder
                                 ) extends GameScene {

  private val overlay = new Overlay(this)
  val maxAllowedPlayersCount = 2
  val playerTextInputFields: Seq[TextField] = for (_ <- 1 to maxAllowedPlayersCount) yield new TextField()

  private val rootVBox: VBox = new VBox {
    prefHeight = 600
    prefWidth = 500
    fillWidth = false
    padding = Insets(20)
    alignment = Pos.Center
    this.getStylesheets.add(Styles.createPlayerCss)
    styleClass.add("create-player-panel")

    val createPlayersTitle = new Text {
      text = "Create Players"
      styleClass.add("title")
    }

    val nameTitle = new Text {
      text = "Enter Player Names"
      styleClass.add("subtitle")
    }

    children.addAll(createPlayersTitle, nameTitle)

    val playerTextInputFieldVBox = new VBox(10) {
      children = playerTextInputFields
      padding = Insets(10)
    }

    children.add(playerTextInputFieldVBox)

    val startButton = GameButtonFactory.createGameButton("Start", 250, 60) {
      () => startGame()
    }

    val mainMenuButton = GameButtonFactory.createGameButton("Back", 250, 60) {
      () =>
        contextHolder.clear()
        GlobalObservable.notifyObservers(SceneSwitchEvent.MainMenu)
    }

    startButton.styleClass.add("start-button")

    val startButtonBox = new VBox(10) {
      alignment = Pos.TOP_CENTER
      children = Seq(startButton, mainMenuButton)
    }

    children.add(startButtonBox)
  }

  root = new StackPane {
    children = Seq(rootVBox, overlay.getPane)
  }

  overlay.getPane.prefWidth = 800
  overlay.getPane.prefHeight = 600
  overlay.getPane.visible = false

  private def startGame(): Unit = {
    val playerNames = getPlayerNames()

    if (playerNames.size != 2) {
      showAlert("Exactly 2 players are required to start the game.")
      return
    }

    controller.createGame(playerNames.head, playerNames(1))
    GlobalObservable.notifyObservers(SceneSwitchEvent.PlayingField)
  }

  private def getPlayerNames(): Seq[String] =
    playerTextInputFields.map(_.text.value.trim).filter(_.nonEmpty)

  private def showAlert(content: String): Unit = {
    val alert = GameAlertFactory.createAlert(content, overlay, autoHide = false)
    overlay.show(alert, autoHide = false)
  }
}
