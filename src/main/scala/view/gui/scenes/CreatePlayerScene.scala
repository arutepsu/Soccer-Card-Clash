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

class CreatePlayerScene(controller: IController) extends VBox with Observer {

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

  val maxAllowedPlayersCount = 2
  val playerTextInputFields = for (_ <- 1 to maxAllowedPlayersCount) yield new TextField()

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

  def getPlayerNames(): Seq[String] = {
    playerTextInputFields.map(_.text.value.trim).filter(_.nonEmpty)
  }

  def startGame(): Unit = {
    val playerNames = getPlayerNames()

    if (playerNames.size != 2) {
      showAlert("Error", "Exactly 2 players are required to start the game.")
      return
    }
    controller.startGame(playerNames.head, playerNames(1))
    controller.notifyObservers(Events.PlayingField)
  }



  override def update(e: ObservableEvent): Unit = {
    Platform.runLater(() => {
      println("🔄 CreatePlayerCard Updating!")

      playerTextInputFields.head.text = controller.getPlayer1.name
      playerTextInputFields(1).text = controller.getPlayer2.name
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
