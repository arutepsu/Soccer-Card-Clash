import model.cardComponent.Deck
import model.playerComponent.Player
import model.playingFiledComponent.PlayingField
import view.Tui
import scala.io.StdIn.readLine
import scala.collection.mutable
import scala.util.Try
import controller.Controller

object Game {
//  def startGame(): Unit = {
//    // Deal cards to players
//    val (player1, player2) = dealCards()
//
//    // Convert player hands to queues
//    val player1HandQueue = player1.getCards.to(mutable.Queue)
//    val player2HandQueue = player2.getCards.to(mutable.Queue)
//
//    // Initialize the PlayingField with players and hands
//    val pf = new PlayingField(player1, player1HandQueue, player2, player2HandQueue)
//    pf.setPlayingField()
//
//    // Create the GameController and TUI
//    val controller = new Controller(player1, player2, pf)
//    val tui = new TUI(controller)
//
//    // Start the game using the TUI
//    controller.startGame() // Initialize game state and notify observers
//    //    tui.processInputLine("n") // Trigger new game in TUI
//  }

  // Start the GUI on the Event Dispatch Thread
  //    SwingUtilities.invokeLater(() => {
  //      val guiFrame = new gui(player1, player2, pf)
  //      guiFrame.visible = true
  //    })
  //  }

//  def dealCards(): (Player, Player) = {
//    val deck = Deck.createDeck()
//    Deck.shuffleDeck(deck)
//
//    val hand1 = for (_ <- 1 to 26) yield deck.dequeue()
//    val hand2 = for (_ <- 1 to 26) yield deck.dequeue()
//
//    val player1 = Player("Player 1", hand1.toList)
//    val player2 = Player("Player 2", hand2.toList)
//
//    (player1, player2)
//  }
  /** Deals cards to players with the given names */
  private def dealCards(player1Name: String, player2Name: String): (Player, Player) = {
    val deck = Deck.createDeck()
    Deck.shuffleDeck(deck)

    val hand1 = for (_ <- 1 to 26) yield deck.dequeue()
    val hand2 = for (_ <- 1 to 26) yield deck.dequeue()

    val player1 = Player(player1Name, hand1.toList)
    val player2 = Player(player2Name, hand2.toList)

    (player1, player2)
  }

  var playerNames: Seq[String] = Seq.empty // ✅ Store player names globally

  def prepareGame(): Controller = {
    if (playerNames.length != 2) {
      throw new IllegalStateException("Player names must be set before starting the game.")
    }

    // ✅ Pass names to dealCards
    val (player1, player2) = dealCards(playerNames.head, playerNames(1))

    // ✅ Initialize PlayingField with mutable hands
    val player1HandQueue = player1.getCards.to(mutable.Queue)
    val player2HandQueue = player2.getCards.to(mutable.Queue)

    println(s"👥 Players: ${player1.name} vs ${player2.name}")
    println(s"${player1.name}'s Hand: ${player1HandQueue.mkString(", ")}")
    println(s"${player2.name}'s Hand: ${player2HandQueue.mkString(", ")}")

    val playingField = new PlayingField(player1, player1HandQueue, player2, player2HandQueue)
    playingField.setPlayingField()

    // ✅ Return the initialized Controller
    new Controller(player1, player2, playingField)
  }

}

//import model.cardComponent.Deck
//import model.playerComponent.Player
//import model.playingFiledComponent.PlayingField
//import view.{TUI}
//import javax.swing.SwingUtilities
//import controller.Controller
//import javafx.application.Application
////import view.GUI
//import view.MainMenu
//object Game {
//  def main(args: Array[String]): Unit = {
//    println("Choose interface: [1] TUI, [2] GUI")
//    val choice = scala.io.StdIn.readLine().trim
//
//    choice match {
//      case "1" =>
//        startTUI()
//      case "2" =>
//        startGUI()
//      case _ =>
//        println("Invalid choice. Exiting.")
//    }
//  }
//
//  def startTUI(): Unit = {
//    // Deal cards to players
//    val (player1, player2) = dealCards()
//
//    // Convert player hands to queues
//    val player1HandQueue = player1.getCards.to(mutable.Queue)
//    val player2HandQueue = player2.getCards.to(mutable.Queue)
//
//    // Initialize the PlayingField with players and hands
//    val pf = new PlayingField(player1, player1HandQueue, player2, player2HandQueue)
//    pf.setPlayingField()
//
//    // Create the GameController and TUI
//    val controller = new Controller(player1, player2, pf)
//    val tui = new TUI(controller)
//
//    // Start the game using the TUI
//    controller.startGame() // Initialize game state and notify observers
//  }
//
//  def startGUI(): Unit = {
//    // Deal cards to players
//    val (player1, player2) = dealCards()
//
//    // Convert player hands to queues
//    val player1HandQueue = player1.getCards.to(mutable.Queue)
//    val player2HandQueue = player2.getCards.to(mutable.Queue)
//
//    // Initialize the PlayingField with players and hands
//    val pf = new PlayingField(player1, player1HandQueue, player2, player2HandQueue)
//    pf.setPlayingField()
//
//    // Create the GameController
//    val controller = new Controller(player1, player2, pf)
//
//    // Start the JavaFX GUI
//    Application.launch(classOf[GUI], controller)
//  }
//
//  def dealCards(): (Player, Player) = {
//    val deck = Deck.createDeck()
//    Deck.shuffleDeck(deck)
//
//    val hand1 = for (_ <- 1 to 26) yield deck.dequeue()
//    val hand2 = for (_ <- 1 to 26) yield deck.dequeue()
//
//    val player1 = Player("Player 1", hand1.toList)
//    val player2 = Player("Player 2", hand2.toList)
//
//    (player1, player2)
//  }
//}
