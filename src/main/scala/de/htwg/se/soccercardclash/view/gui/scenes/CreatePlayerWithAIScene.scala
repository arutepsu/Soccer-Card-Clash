package de.htwg.se.soccercardclash.view.gui.scenes
import com.google.inject.Inject
import de.htwg.se.soccercardclash.controller.{IController, IGameContextHolder}
import de.htwg.se.soccercardclash.util.*
import de.htwg.se.soccercardclash.view.gui.components.alert.GameAlertFactory
import de.htwg.se.soccercardclash.view.gui.components.playerView.PlayerAvatarRegistry
import de.htwg.se.soccercardclash.view.gui.components.uiFactory.GameButtonFactory
import de.htwg.se.soccercardclash.view.gui.overlay.Overlay
import de.htwg.se.soccercardclash.view.gui.scenes.sceneManager.SceneManager
import de.htwg.se.soccercardclash.view.gui.utils.Styles
import scalafx.application.Platform
import scalafx.geometry.{Insets, Pos}
import scalafx.scene.Scene
import scalafx.scene.control.{Label, TextField}
import scalafx.scene.layout.{StackPane, VBox}
import scalafx.scene.text.Text
import de.htwg.se.soccercardclash.model.playerComponent.base.*
class CreatePlayerWithAIScene @Inject()(
                                    controller: IController,
                                    contextHolder: IGameContextHolder
                                  ) extends GameScene {

  private val overlay = new Overlay(this)
  val playerTextInput: TextField = new TextField()


  private val rootVBox: VBox = new VBox {
    prefHeight = 600
    prefWidth = 500
    fillWidth = false
    padding = Insets(20)
    alignment = Pos.Center
    this.getStylesheets.add(Styles.createPlayerWitAICss)
    styleClass.add("create-player-ai-panel")

    val createPlayersTitle: Label = new Label("Create Player") {
      styleClass += "title"
    }

    val nameTitle: Label = new Label("Enter Player's Name") {
      styleClass += ("subtitle")
    }

    children.addAll(createPlayersTitle, nameTitle)

    val playerTextInputFieldVBox = new VBox(10) {
      children = Seq(playerTextInput)
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
    getPlayerName match {
      case Some(humanName) =>
        controller.createGameWithAI(humanName)

        val players = Seq(
          contextHolder.get.state.getRoles.attacker,
          contextHolder.get.state.getRoles.defender
        )

        PlayerAvatarRegistry.assignAvatarsInOrder(players)

        EventDispatcher.dispatchSingle(controller, SceneSwitchEvent.PlayingField)
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
