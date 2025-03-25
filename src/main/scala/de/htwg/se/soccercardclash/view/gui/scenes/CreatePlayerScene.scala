package de.htwg.se.soccercardclash.view.gui.scenes

import scalafx.scene.Scene
import scalafx.scene.layout.{StackPane, VBox}
import scalafx.geometry.Insets
import scalafx.scene.text.Text
import scalafx.scene.control.TextField
import scalafx.geometry.Pos
import de.htwg.se.soccercardclash.view.gui.components.uiFactory.GameButtonFactory
import de.htwg.se.soccercardclash.controller.IController
import de.htwg.se.soccercardclash.util.{Events, ObservableEvent, Observer}
import de.htwg.se.soccercardclash.view.gui.scenes.sceneManager.SceneManager
import de.htwg.se.soccercardclash.view.gui.overlay.Overlay
import scalafx.application.Platform
import de.htwg.se.soccercardclash.view.gui.components.alert.GameAlertFactory
import de.htwg.se.soccercardclash.view.gui.utils.Styles

class CreatePlayerScene(controller: IController) extends Scene(new StackPane) with Observer {

  controller.add(this) // Register as an observer

  private val overlay = new Overlay(this) // ✅ Added Overlay for Alerts

  val maxAllowedPlayersCount = 2

  // ✅ Declare playerTextInputFields at the class level
  val playerTextInputFields: Seq[TextField] = for (_ <- 1 to maxAllowedPlayersCount) yield new TextField()

  val rootVBox: VBox = new VBox {
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

    val startButton = GameButtonFactory.createGameButton(
      text = "Start",
      width = 250,
      height = 60
    )(() => startGame())

    val mainMenuButton = GameButtonFactory.createGameButton(
      text = "Back",
      width = 250,
      height = 60
    ) { () =>
      controller.notifyObservers(Events.MainMenu) // ✅ Notify SceneManager
      controller.resetGame() // ✅ Reset game after switching scenes
    }

    startButton.styleClass.add("start-button")

    val startButtonBox = new VBox(10) {
      alignment = Pos.TOP_CENTER
      children = Seq(startButton, mainMenuButton)
    }

    children.add(startButtonBox)
  }

  // ✅ Set root to a StackPane with rootVBox and overlay
  root = new StackPane {
    children = Seq(rootVBox, overlay.getPane)
  }

  // ✅ Ensure overlay covers the full scene
  overlay.getPane.prefWidth = 800
  overlay.getPane.prefHeight = 600
  overlay.getPane.visible = false // Initially hidden

  // ✅ Now `playerTextInputFields` is accessible
  def getPlayerNames(): Seq[String] = {
    playerTextInputFields.map(_.text.value.trim).filter(_.nonEmpty)
  }

  def startGame(): Unit = {
    val playerNames = getPlayerNames()

    if (playerNames.size != 2) {
      showAlert("Exactly 2 players are required to start the game.") // ✅ Uses GameAlertFactory
      return
    }
    controller.createGame(playerNames.head, playerNames(1))
    
  }

  override def update(e: ObservableEvent): Unit = {
    Platform.runLater(() => {
      println("🔄 CreatePlayerScene Updating!")
      playerTextInputFields.head.text = controller.getCurrentGame.getPlayer1.name
      playerTextInputFields(1).text = controller.getCurrentGame.getPlayer2.name
      SceneManager.update(e)
    })
  }
  // ✅ Replaces JavaFX Alert with GameAlertFactory
  private def showAlert(content: String): Unit = {
    Platform.runLater(() => {
      val alert = GameAlertFactory.createAlert(content, overlay, autoHide = false) // ✅ Use factory method
      overlay.show(alert, autoHide = false) // ✅ Display the alert using overlay
    })
  }
}
