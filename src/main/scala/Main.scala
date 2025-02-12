import model.cardComponent.Card
import model.playerComponent.Player
import model.cardComponent.Deck
import scalafx.stage.Stage
import model.cardComponent.Value.{Ace, Eight, Five, Four, Jack, King, Nine, Queen, Seven, Six, Ten, Three, Two, Value}
import model.cardComponent.Suit.{Hearts, Diamonds, Spades, Clubs}
import view.scenes.MainMenuScene
import scala.collection.mutable
//object Main {
//  def main(args: Array[String]): Unit = {
//    startGame()
//  }
//}
import scalafx.application.JFXApp3
import scalafx.application.JFXApp3.PrimaryStage
import view.Gui
import view.Tui
import model.cardComponent.Deck
import model.playerComponent.Player
import model.playingFiledComponent.PlayingField
import scala.io.StdIn.readLine
import scala.collection.mutable
import scala.util.Try
import controller.Controller
//object Main extends JFXApp3 {
//
//  override def start(): Unit = {
//    stage = new PrimaryStage { // Stage is set here
//      title = "Soccer Card Game"
//    }
//
//    // Now that stage is initialized, we can pass it safely
//    val soccerCardGame = new SoccerCardGame(stage)
//    stage.scene = soccerCardGame.mainMenuScene() // Start with the main menu
//  }
//}
import controller.Controller
import model.playerComponent.Player
import model.playingFiledComponent.PlayingField
import model.cardComponent.Deck
import scala.collection.mutable
import scalafx.application.JFXApp3
import controller.BoostDefenderCommand

import controller.Controller
import model.playerComponent.Player
import model.playingFiledComponent.PlayingField
import model.cardComponent.Deck
import scala.collection.mutable
import scalafx.application.JFXApp3
import scalafx.stage.Stage
import view.scenes.sceneManager.SceneManager
import view.scenes.MainMenuScene
import view.Gui

object Main extends JFXApp3 {
  override def start(): Unit = {
    val (player1, player2) = dealCards()

    // Convert player hands to queues
    val player1HandQueue = player1.getCards.to(mutable.Queue)
    val player2HandQueue = player2.getCards.to(mutable.Queue)

    // Initialize the PlayingField with players and hands
    val pf = new PlayingField(player1, player1HandQueue, player2, player2HandQueue)
    pf.setPlayingField()

    // Create the GameController
    val controller = new Controller(player1, player2, pf)

    // ✅ Start TUI in a separate thread
    new Thread(() => {
      val tui = new Tui(controller)
      tui.start()
    }).start()

    // ✅ Start GUI using SceneManager

    // ✅ Start GUI (which starts MainMenuScene)
    val gui = new Gui(controller)
    gui.start()
  }

  def dealCards(): (Player, Player) = {
    val deck = Deck.createDeck()
    Deck.shuffleDeck(deck)

    val hand1 = for (_ <- 1 to 26) yield deck.dequeue()
    val hand2 = for (_ <- 1 to 26) yield deck.dequeue()

    val player1 = Player("Player 1", hand1.toList)
    val player2 = Player("Player 2", hand2.toList)

    (player1, player2)
  }
}


//object Main extends JFXApp3 {
//  override def start(): Unit = {
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
//    // ✅ Start TUI in a separate thread
//    new Thread(() => {
//      val tui = new Tui(controller)
//      tui.start()
//    }).start()
//
//    // ✅ Start GUI normally
//    // Start the GUI without passing the stage explicitly
//    new Gui(controller).start()
////    new Tui(controller).start()
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
//object Main {
//  def main(args: Array[String]): Unit = {
//    // Deal cards
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
////test execute
////    println("Before Attack:")
////    println(player1)
////    println(player2)
////
////    val attackSuccessful = pf.execute(0)
////
////    println("After Attack:")
////    println(player1)
////    println(player2)
////    println(s"Attack Successful: $attackSuccessful")
////test boost
//    // Choose a card to boost (Index 0 for demonstration)
//    // Print initial state
//    println("\n=== Before Attack ===")
////    println(s"Player 1 Defenders: ${pf.playerDefenders(player1).mkString(", ")}")
////    println(s"Player 2 Defenders: ${pf.playerDefenders(player2).mkString(", ")}")
//    println(s"Player 1 Hand: ${controller.getPlayerHand(controller.getPlayer1).mkString(", ")}")
//    println(s"Player 2 Hand: ${controller.getPlayerHand(controller.getPlayer2).mkString(", ")}")
//    println(s"Player 1 Defenders: ${pf.playerDefenders(player1).mkString(", ")}")
//    println(s"Player 2 Defenders: ${pf.playerDefenders(player2).mkString(", ")}")
//    // Choose a card to boost (Index 0 for demonstration)
////    pf.chooseBoostCard(0)
//
//    controller.executeAttackCommandDouble(0)
//    // Print state after boosting
//    println("\n=== After Attack ===")
//    println(s"Player 1 Hand: ${controller.getPlayerHand(controller.getPlayer1).mkString(", ")}")
//    println(s"Player 2 Hand: ${controller.getPlayerHand(controller.getPlayer2).mkString(", ")}")
//    println(s"Player 1 Defenders: ${pf.playerDefenders(player1).mkString(", ")}")
//    println(s"Player 2 Defenders: ${pf.playerDefenders(player2).mkString(", ")}")
//    controller.undo()
//
//    println("\n=== After 1 Undo ===")
//    println(s"Player 1 Hand: ${controller.getPlayerHand(controller.getPlayer1).mkString(", ")}")
//    println(s"Player 2 Hand: ${controller.getPlayerHand(controller.getPlayer2).mkString(", ")}")
//    println(s"Player 1 Defenders: ${pf.playerDefenders(player1).mkString(", ")}")
//    println(s"Player 2 Defenders: ${pf.playerDefenders(player2).mkString(", ")}")
////    controller.undo()
////    // Print state after boosting
////    println("\n=== After 1 Undo ===")
////    println(s"Player 1 Defenders: ${pf.playerDefenders(player1).mkString(", ")}")
////    println(s"Player 2 Defenders: ${pf.playerDefenders(player2).mkString(", ")}")
////    controller.undo()
////    println("\n=== After 2 Undo ===")
////    println(s"Player 1 Defenders: ${pf.playerDefenders(player1).mkString(", ")}")
////    println(s"Player 2 Defenders: ${pf.playerDefenders(player2).mkString(", ")}")
////
////    // Perform an attack (assuming attacker attacks index 0 of the defender's field)
////    println("\n=== Attacking Defender's First Card ===")
////    pf.attack(0) // Attack the first defender card
////
////    // Print state after attack
////    println("\n=== After Attack ===")
////    println(s"Player 1 Defenders: ${pf.playerDefenders(player1).mkString(", ")}")
////    println(s"Player 2 Defenders: ${pf.playerDefenders(player2).mkString(", ")}")
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