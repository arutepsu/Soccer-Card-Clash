import model.cardComponent.Deck
import model.playerComponent.Player
import model.playingFiledComponent.PlayingField
import view.Tui
import javax.swing.SwingUtilities
//import scala.swing.Frame
import scala.io.StdIn.readLine
import scala.collection.mutable
import scala.util.Try
import controller.Controller
import controller.AttackCommand
object GameSimulate {
  def startGame(): Unit = {
    // Deal cards to players
    val (player1, player2) = dealCards()

    // Convert player hands to queues
    val player1HandQueue = player1.getCards.to(mutable.Queue)
    val player2HandQueue = player2.getCards.to(mutable.Queue)

    // Initialize the PlayingField with players and hands
    val pf = new PlayingField(player1, player1HandQueue, player2, player2HandQueue)
    pf.setPlayingField()

    // Create the GameController and TUI
    val controller = new Controller(player1, player2, pf)
    val tui = new Tui(controller)

    // Start the game using the TUI
    controller.startGame()

    // Simulate a game loop with undo and redo actions
    simulateGame(controller, pf)

    // Optional: Start the GUI
    // SwingUtilities.invokeLater(() => {
    //   val guiFrame = new gui(player1, player2, pf)
    //   guiFrame.visible = true
    // })
  }

  def simulateGame(controller: Controller, pf: PlayingField): Unit = {
    // Simulate a new attack
    println("\n--- Starting a New Attack ---")
    val attackCommand = new AttackCommand(1, pf)
    pf.executeCommand(attackCommand) // Uses PlayingField's executeCommand

    displayGameState(pf)

    // Undo the attack
    println("\n--- Undoing the Attack ---")
    pf.undo()

    displayGameState(pf)

    // Redo the attack
    println("\n--- Redoing the Attack ---")
    pf.redo()

    displayGameState(pf)
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

  // Helper function to display the game state
  def displayGameState(pf: PlayingField): Unit = {
    println("Current Game State:")
    println(s"Attacker: ${pf.getAttacker.name}")
    println(s"Defender: ${pf.getDefender.name}")
    println(s"Player 1 Score: ${pf.getScorePlayer1}")
    println(s"Player 2 Score: ${pf.getScorePlayer2}")
    println(s"Player 1 Defenders: ${pf.playerDefenders(pf.getAttacker)}")
    println(s"Player 1 Goalkeeper: ${pf.getGoalkeeper(pf.getAttacker).getOrElse("None")}")
    println(s"Player 2 Defenders: ${pf.playerDefenders(pf.getDefender)}")
    println(s"Player 2 Goalkeeper: ${pf.getGoalkeeper(pf.getDefender).getOrElse("None")}")
    println(s"Player 1 Cards: ${pf.getHand(pf.getAttacker)}")
    println(s"Player 2 Cards: ${pf.getHand(pf.getDefender)}")
    println("------------------------------------------------")
  }
}

