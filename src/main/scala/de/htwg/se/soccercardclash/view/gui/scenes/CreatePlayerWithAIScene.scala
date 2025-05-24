package de.htwg.se.soccercardclash.view.gui.scenes
import com.google.inject.Inject
import de.htwg.se.soccercardclash.controller.IController
import de.htwg.se.soccercardclash.model.playerComponent.base.*
import de.htwg.se.soccercardclash.util.*
import de.htwg.se.soccercardclash.view.gui.components.alert.GameAlertFactory
import de.htwg.se.soccercardclash.view.gui.components.playerView.PlayerAvatarRegistry
import de.htwg.se.soccercardclash.view.gui.components.sceneComponents.GameStartupDataHolder
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

class CreatePlayerWithAIScene @Inject()(
                                         controller: IController,
                                         contextHolder: IGameContextHolder,
                                         startupDataHolder: GameStartupDataHolder
                                       ) extends GameScene {
  
  this.getStylesheets.add(Styles.createPlayerWitAICss)
  Font.loadFont(getClass.getResourceAsStream("/fonts/Rajdhani/Rajdhani-Regular.ttf"), 20)
  Font.loadFont(getClass.getResourceAsStream("/fonts/Rajdhani/Rajdhani-Bold.ttf"), 20)

  private val overlay = new Overlay(this)

  val playerTextInput: TextField = new TextField {
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
    styleClass.add("create-player-panel")

    // --- Logo ---
    val logo = Assets.createLogoImageView()

    // --- Title aSection ---
    val createPlayersTitle: Label = new Label("Create Player") {
      styleClass += "title"
      padding = Insets(10)
    }

    val nameTitle: Label = new Label("Enter Player's Name") {
      styleClass += "subtitle"
    }

    val titleSection = new VBox(5) {
      alignment = Pos.CENTER
      children.addAll(createPlayersTitle, nameTitle)
    }

    // --- Input Field ---
    val inputFieldBox = new VBox(10) {
      alignment = Pos.CENTER
      children = Seq(playerTextInput)
    }

    // --- Buttons ---
    val startButton = GameButtonFactory.createGameButton("Ok", 250, 60) {
      () => proceedToAISelection()
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

    // --- Final layout ---
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

  private def proceedToAISelection(): Unit = {
    getPlayerName match {
      case Some(humanName) =>
        startupDataHolder.data.humanPlayerName = Some(humanName)
        EventDispatcher.dispatchSingle(controller, SceneSwitchEvent.AISelection)

      case None =>
        showAlert("Please enter the player's name.")
    }
  }


  private def getPlayerName: Option[String] =
    Option(playerTextInput.text.value.trim).filter(_.nonEmpty)

  private def showAlert(content: String): Unit = {
    val alert = GameAlertFactory.createAlert(content, overlay, autoHide = false)
    overlay.show(alert, autoHide = false)
  }
}
