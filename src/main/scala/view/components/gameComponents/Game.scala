package view.components.gameComponents

import model.cardComponent.Deck
import model.playerComponent.Player
import model.playingFiledComponent.PlayingField
import scala.io.StdIn.readLine
import scala.collection.mutable
import scala.util.Try
import controller.Controller

object Game {
  def startGame(player1Name: String, player2Name: String): Controller = {
    // Deal cards to players
    val (player1, player2) = dealCards(player1Name, player2Name)

    // Convert player hands to queues
    val player1HandQueue = player1.getCards.to(mutable.Queue)
    val player2HandQueue = player2.getCards.to(mutable.Queue)

    // Initialize the PlayingField with players and hands
    val pf = new PlayingField(player1, player1HandQueue, player2, player2HandQueue)
    pf.setPlayingField()

    // Create and return the Controller
    val controller = new Controller(player1, player2, pf)
    controller.startGame() // Initialize game state and notify observers
    controller
  }

  private def dealCards(player1Name: String, player2Name: String): (Player, Player) = {
    val deck = Deck.createDeck()
    Deck.shuffleDeck(deck)

    val hand1 = for (_ <- 1 to 26) yield deck.dequeue()
    val hand2 = for (_ <- 1 to 26) yield deck.dequeue()

    val player1 = Player(player1Name, hand1.toList)
    val player2 = Player(player2Name, hand2.toList)

    (player1, player2)
  }
}
