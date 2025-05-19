package de.htwg.se.soccercardclash.view.gui.scenes

import com.google.inject.Inject
import de.htwg.se.soccercardclash.controller.{IController, IGameContextHolder}
import de.htwg.se.soccercardclash.util.*
import de.htwg.se.soccercardclash.view.gui.components.alert.GameAlertFactory
import de.htwg.se.soccercardclash.view.gui.components.playerView.PlayerAvatarRegistry
import de.htwg.se.soccercardclash.view.gui.components.uiFactory.GameButtonFactory
import de.htwg.se.soccercardclash.view.gui.overlay.Overlay
import de.htwg.se.soccercardclash.view.gui.scenes.sceneManager.SceneManager
import de.htwg.se.soccercardclash.view.gui.utils.{Assets, Styles}
import scalafx.application.Platform
import scalafx.geometry.{Insets, Pos}
import scalafx.scene.Scene
import scalafx.scene.control.{Label, TextField}
import scalafx.scene.layout.{StackPane, VBox}
import scalafx.scene.text.{Font, Text}
import de.htwg.se.soccercardclash.model.playerComponent.IPlayer
import de.htwg.se.soccercardclash.view.gui.components.uiFactory.GameButtonFactory.getClass
import scalafx.scene.image.{Image, ImageView}
class CreatePlayerScene @Inject()(
                                   controller: IController,
                                   contextHolder: IGameContextHolder
                                 ) extends GameScene {
  this.getStylesheets.add(Styles.createPlayerCss)
  Font.loadFont(getClass.getResourceAsStream("/fonts/Rajdhani/Rajdhani-Regular.ttf"), 20)
  Font.loadFont(getClass.getResourceAsStream("/fonts/Rajdhani/Rajdhani-Bold.ttf"), 20)
  private val overlay = new Overlay(this)
  val maxAllowedPlayersCount = 2

  val playerTextInputFields: Seq[TextField] =
    for (_ <- 1 to maxAllowedPlayersCount) yield new TextField {
      prefWidth = 300
      prefHeight = 40
      styleClass += "player-text-input"
    }

  private val rootVBox: VBox = new VBox {
    spacing = 20
    prefHeight = 600
    prefWidth = 500
    fillWidth = false
    padding = Insets(10)
    alignment = Pos.TOP_CENTER
    this.getStylesheets.add(Styles.createPlayerCss)
    styleClass.add("create-player-panel")

    val logo = new VBox {
      alignment = Pos.TOP_CENTER
      children = Seq(Assets.createLogoImageView())
    }

    val createPlayersTitle: Label = new Label("Create Players") {
      styleClass += "title"
      padding = Insets(10)
    }

    val nameTitle: Label = new Label("Enter Player Names") {
      styleClass += "subtitle"
    }

    val titleSection = new VBox(5) {
      alignment = Pos.CENTER
      children.addAll(createPlayersTitle, nameTitle)
    }

    val inputFieldBox = new VBox(10) {
      alignment = Pos.CENTER
      children.addAll(playerTextInputFields.map(_.delegate): _*)
    }

    val startButton = GameButtonFactory.createGameButton("Start", 250, 60) {

      () => startGame()
    }

    val mainMenuButton = GameButtonFactory.createGameButton("Back", 250, 60) {
      () =>
        contextHolder.clear()
        GlobalObservable.notifyObservers(SceneSwitchEvent.MainMenu)
    }


    val startButtonBox = new VBox(10) {
      alignment = Pos.CENTER
      children = Seq(startButton, mainMenuButton)
    }

    children.addAll(
      logo,
      titleSection,
      inputFieldBox,
      startButtonBox
    )
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

    val players = Seq(
      contextHolder.get.state.getRoles.attacker,
      contextHolder.get.state.getRoles.defender
    )

    PlayerAvatarRegistry.assignAvatarsInOrder(players)

    EventDispatcher.dispatchSingle(controller, SceneSwitchEvent.PlayingField)
  }

  private def getPlayerNames(): Seq[String] =
    playerTextInputFields.map(_.text.value.trim).filter(_.nonEmpty)

  private def showAlert(content: String): Unit = {
    val alert = GameAlertFactory.createAlert(content, overlay, autoHide = false)
    overlay.show(alert, autoHide = false)
  }

}