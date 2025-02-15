package view.scenes

import controller.IController
import scalafx.geometry.{Insets, Pos}
import scalafx.scene.control.Alert.AlertType
import scalafx.scene.control.Alert
import scalafx.scene.layout.VBox
import scalafx.scene.text.Text
import view.scenes.sceneManager.SceneManager
import view.components.playerComponents.PlayerTextInputField
import view.components.uiFactory.GameButtonFactory
import view.utils.Styles

class CreatePlayerCard(controller: IController) extends VBox {
  prefHeight = 600 // Increased height for better spacing
  prefWidth = 500  // Adjusted width for better UI
  fillWidth = false
  padding = Insets(20)
  alignment = Pos.Center

  // ✅ Apply external CSS for consistent styling
  this.getStylesheets.add(Styles.createPlayerCss)
  styleClass.add("create-player-panel")

  val createPlayersTitle = new Text {
    text = "Create Players"
    styleClass.add("title") // ✅ Using external CSS
  }

  val nameTitle = new Text {
    text = "Enter Player Names"
    styleClass.add("subtitle") // ✅ Using external CSS
  }

  children.addAll(createPlayersTitle, nameTitle)

  val maxAllowedPlayersCount = 2
  val playerTextInputFields =
    for (_ <- 1 to maxAllowedPlayersCount) yield PlayerTextInputField()

  val playerTextInputFieldVBox = new VBox(10) {
    children = playerTextInputFields
    padding = Insets(10)
  }

  children.add(playerTextInputFieldVBox)

  val startButton = GameButtonFactory.createGameButton(
    text = "Start",
    width = 250, // ✅ Adjusted button size
    height = 60
  )(() => startGame())

  startButton.styleClass.add("start-button") // ✅ Apply button style

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

    // ✅ Assign names to players using `setPlayerName` method
    controller.setPlayerName(1, playerNames.head)
    controller.setPlayerName(2, playerNames(1))

    // ✅ Start the game AFTER setting names
    controller.startGame()

    // ✅ Switch to the main game scene
    SceneManager.switchScene(new PlayingFieldScene(controller, 800, 600, 0, () => {}, _ => {}, () => {}))
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