package view.gui.scenes

import controller.{Events, IController}
import scalafx.scene.control.Alert.AlertType
import scalafx.scene.control.Alert
import scalafx.scene.layout.VBox
import scalafx.scene.text.Text
import view.gui.scenes.sceneManager.SceneManager
import view.gui.components.playerView.PlayerTextInputField
import view.gui.components.uiFactory.GameButtonFactory
import view.gui.utils.Styles
import scalafx.scene.layout.VBox
import scalafx.scene.control.Alert
import scalafx.geometry.Insets
import scalafx.scene.text.Text
import scalafx.scene.control.TextField
import scalafx.application.Platform
import controller.IController
import util.{ObservableEvent, Observer}
import scalafx.geometry.Pos
import scalafx.scene.Scene

class CreatePlayerScene(controller: IController) extends Scene(new VBox) with Observer {

  controller.add(this) // Register as an observer

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

    startButton.styleClass.add("start-button")

    val startButtonBox = new VBox(10) {
      alignment = Pos.TOP_CENTER
      children = Seq(startButton)
    }

    children.add(startButtonBox)
  }

  root = rootVBox // ✅ Set the VBox as the scene root

  // ✅ Now `playerTextInputFields` is accessible
  def getPlayerNames(): Seq[String] = {
    playerTextInputFields.map(_.text.value.trim).filter(_.nonEmpty)
  }

  def startGame(): Unit = {
    val playerNames = getPlayerNames()

    if (playerNames.size != 2) {
      showAlert("Error", "Exactly 2 players are required to start the game.")
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

  private def showAlert(titleText: String, content: String): Unit = {
    val alert = new Alert(AlertType.Warning)
    alert.title = titleText
    alert.headerText = None
    alert.contentText = content
    alert.showAndWait()
  }
}
