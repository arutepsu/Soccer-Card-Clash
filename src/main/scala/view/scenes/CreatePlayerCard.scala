//
//package view.scenes
//
//import SceneManager.SceneManager
//import model.cardComponent.Deck
//import model.playerComponent.Player
//import model.playingFiledComponent.PlayingField
//import scalafx.event.ActionEvent
//import scalafx.geometry.{Insets, Pos}
//import scalafx.scene.control.Alert.AlertType
//import scalafx.scene.control.{Alert, Button}
//import scalafx.scene.layout.VBox
//import scalafx.scene.text.Text
//import scalafx.stage.Stage
//import view.scenes.GamePlayerScene
//import view.components.playerComponents.PlayerTextInputField
//import view.components.uiFactory.GameButtonFactory
//import scala.collection.mutable
//import controller.Controller
//
//class CreatePlayerCard(stage: Stage, windowWidth: Double, windowHeight: Double, controller: Controller) extends VBox {
//  prefHeight = 450
//  prefWidth = 450
//  fillWidth = false
//  padding = Insets(top = 10, right = 20, bottom = 10, left = 20)
//  alignment = Pos.Center
//
//  style = "-fx-background-color: #FFCD92;" +
//    "-fx-background-radius: 36;" +
//    "-fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.7), 10, 0, 5, 5);"
//
//  val createPlayersTitle = new Text {
//    text = "Create Players"
//    style = "-fx-fill: #7A2626; -fx-font-size: 30;"
//  }
//
//  val nameTitle = new Text {
//    text = "Enter Player Names"
//    alignmentInParent = Pos.BASELINE_LEFT
//  }
//
//  children.addAll(createPlayersTitle, nameTitle)
//
//  val maxAllowedPlayersCount = 2
//  val playerTextInputFields =
//    for (_ <- 1 to maxAllowedPlayersCount) yield PlayerTextInputField()
//
//  val playerTextInputFieldVBox = new VBox(8) {
//    children = playerTextInputFields
//    padding = Insets(top = 0, right = 20, bottom = 20, left = 20)
//  }
//
//  children.addAll(playerTextInputFieldVBox)
//
//  val startButton = GameButtonFactory.createGameButton(
//    text = "Start",
//    width = 350,
//    height = 70
//  )(onFinishedAction = () => startGame())
//
//  val startButtonBox = new VBox(0) {
//    alignment = Pos.TOP_CENTER
//    children = Seq(startButton)
//  }
//
//  children.add(startButtonBox)
//
//  /** Gets entered player names */
//  def getPlayerNames(): Seq[String] = {
//    playerTextInputFields.map(_.text.value.trim).filter(_.nonEmpty)
//  }
//
//  /** Starts the game and switches to GamePlayerScene */
//  def startGame(): Unit = {
//    val playerNames = getPlayerNames()
//
//    if (playerNames.size != 2) {
//      showAlert("Error", "Exactly 2 players are required to start the game.")
//      return
//    }
//
//    controller.getPlayer1.setName(playerNames.head)
//    controller.getPlayer2.setName(playerNames(1))
//
//    // Pass the controller to your game scene.
//    stage.scene = new GamePlayerScene(
//      controller,
//      windowWidth,
//      windowHeight,
//      0,
//      () => {},
//      _ => {},
//      () => {}
//    )
////    controller.initRoles()
//  }
//
//  /** Deals cards to players */
//  private def dealCards(player1Name: String, player2Name: String): (Player, Player) = {
//    val deck = Deck.createDeck()
//    Deck.shuffleDeck(deck)
//
//    val hand1 = for (_ <- 1 to 26) yield deck.dequeue()
//    val hand2 = for (_ <- 1 to 26) yield deck.dequeue()
//
//    val player1 = Player(player1Name, hand1.toList)
//    val player2 = Player(player2Name, hand2.toList)
//
//    (player1, player2)
//  }
//
//  /** Displays an alert message */
//  private def showAlert(titleText: String, content: String): Unit = {
//    val alert = new Alert(AlertType.Warning)
//    alert.title = titleText
//    alert.headerText = None
//    alert.contentText = content
//    alert.showAndWait()
//  }
//}
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
