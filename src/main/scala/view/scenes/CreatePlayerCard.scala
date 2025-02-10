
package view.scenes

import controller.Controller
import model.cardComponent.Deck
import model.playerComponent.Player
import model.playingFiledComponent.PlayingField
import scalafx.event.ActionEvent
import scalafx.geometry.{Insets, Pos}
import scalafx.scene.control.Alert.AlertType
import scalafx.scene.control.{Alert, Button}
import scalafx.scene.layout.VBox
import scalafx.scene.text.Text
import scalafx.stage.Stage
import view.scenes.GamePlayerScene
import view.components.playerComponents.PlayerTextInputField
import view.components.uiFactory.GameButtonFactory
import scala.collection.mutable


class CreatePlayerCard(stage: Stage, windowWidth: Double, windowHeight: Double, controller: Controller) extends VBox {
  prefHeight = 450
  prefWidth = 450
  fillWidth = false
  padding = Insets(top = 10, right = 20, bottom = 10, left = 20)
  alignment = Pos.Center

  style = "-fx-background-color: #FFCD92;" +
    "-fx-background-radius: 36;" +
    "-fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.7), 10, 0, 5, 5);"

  val createPlayersTitle = new Text {
    text = "Create Players"
    style = "-fx-fill: #7A2626; -fx-font-size: 30;"
  }

  val nameTitle = new Text {
    text = "Enter Player Names"
    alignmentInParent = Pos.BASELINE_LEFT
  }

  children.addAll(createPlayersTitle, nameTitle)

  val maxAllowedPlayersCount = 2
  val playerTextInputFields =
    for (_ <- 1 to maxAllowedPlayersCount) yield PlayerTextInputField()

  val playerTextInputFieldVBox = new VBox(8) {
    children = playerTextInputFields
    padding = Insets(top = 0, right = 20, bottom = 20, left = 20)
  }

  children.addAll(playerTextInputFieldVBox)

  val startButton = GameButtonFactory.createGameButton(
    text = "Start",
    width = 350,
    height = 70
  )(onFinishedAction = () => startGame())

  val startButtonBox = new VBox(0) {
    alignment = Pos.TOP_CENTER
    children = Seq(startButton)
  }

  children.add(startButtonBox)

  /** Gets entered player names */
  def getPlayerNames(): Seq[String] = {
    playerTextInputFields.map(_.text.value.trim).filter(_.nonEmpty)
  }

  /** Starts the game and switches to GamePlayerScene */
  def startGame(): Unit = {
    val playerNames = getPlayerNames()

    if (playerNames.size != 2) {
      showAlert("Error", "Exactly 2 players are required to start the game.")
      return
    }

    controller.getPlayer1.setName(playerNames.head)
    controller.getPlayer2.setName(playerNames(1))

    // Pass the controller to your game scene.
    stage.scene = new GamePlayerScene(
      controller,
      windowWidth,
      windowHeight,
      0,
      () => {},
      _ => {},
      () => {}
    )
//    controller.initRoles()
  }

  /** Deals cards to players */
  private def dealCards(player1Name: String, player2Name: String): (Player, Player) = {
    val deck = Deck.createDeck()
    Deck.shuffleDeck(deck)

    val hand1 = for (_ <- 1 to 26) yield deck.dequeue()
    val hand2 = for (_ <- 1 to 26) yield deck.dequeue()

    val player1 = Player(player1Name, hand1.toList)
    val player2 = Player(player2Name, hand2.toList)

    (player1, player2)
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
//class CreatePlayerCard(stage: Stage, windowWidth: Double, windowHeight: Double) extends VBox {
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
//    // Deal cards and create players
//    val (player1, player2) = dealCards(playerNames.head, playerNames(1))
//
//    // Initialize new PlayingField with mutable hands
//    val player1HandQueue = player1.getCards.to(mutable.Queue)
//    val player2HandQueue = player2.getCards.to(mutable.Queue)
//    // Print player hands
//    println(s"$player1's Hand: ${player1HandQueue.mkString(", ")}")
//    println(s"$player2's Hand: ${player2HandQueue.mkString(", ")}")
//    val playingField = new PlayingField(player1, player1HandQueue, player2, player2HandQueue)
//    playingField.setPlayingField() // Set up initial field state
//
//    // Create the controller using the playing field and players.
//    val controller = new Controller(player1, player2, playingField)
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
//    controller.initRoles()
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