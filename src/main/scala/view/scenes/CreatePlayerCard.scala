package view.scenes

import controller.{ControllerEvents, IController}
import scalafx.scene.control.Alert.AlertType
import scalafx.scene.control.Alert
import scalafx.scene.layout.VBox
import scalafx.scene.text.Text
import view.scenes.sceneManager.SceneManager
import view.components.playerComponents.PlayerTextInputField
import view.components.uiFactory.GameButtonFactory
import view.utils.Styles
import scalafx.scene.layout.VBox
import scalafx.scene.control.Alert
import scalafx.geometry.Insets
import scalafx.scene.text.Text
import scalafx.scene.control.TextField
import scalafx.application.Platform
import controller.IController
import util.{ObservableEvent, Observer}
import scalafx.geometry.Pos

class CreatePlayerCard(controller: IController) extends VBox with Observer { // ✅ Now an Observer

  prefHeight = 600
  prefWidth = 500
  fillWidth = false
  padding = Insets(20)
  alignment = Pos.Center

  // ✅ Register as an observer
  controller.add(this)

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

    // ✅ Assign names to players using `setPlayerName`
    controller.setPlayerName(1, playerNames.head)
    controller.setPlayerName(2, playerNames(1))

    // ✅ Start the game AFTER setting names
    // ✅ Start game **only if it hasn't been started yet**
    if (controller.getPlayingField == null) {
      println("🎲 Initializing game...")
      controller.startGame()
    }

    // ✅ Switch to the main game scene
    SceneManager.switchScene(new PlayingFieldScene(controller, 800, 600, 0, () => {}, _ => {}, () => {}))
  }

  /** ✅ Observer Pattern: Refresh the player names when notified */
  override def update(e: ObservableEvent): Unit = {
    Platform.runLater(() => {
      println("🔄 CreatePlayerCard Updating!")

      // ✅ Ensure input fields match player names
      playerTextInputFields.head.text = controller.getPlayer1.name
      playerTextInputFields(1).text = controller.getPlayer2.name
      e match {
        case ControllerEvents.StartGame =>
          println("📌 Switching to Main Menu!")
          SceneManager.switchScene(new PlayingFieldScene(controller, 800, 600, 0, () => {}, _ => {}, () => {}))
      }
    })
  }

  /** Displays an alert message */
  private def showAlert(titleText: String, content: String): Unit = {
    val alert = new Alert(AlertType.Warning)
    alert.title = titleText
    alert.headerText = None
    alert.contentText = content
    alert.showAndWait()
  }
}
