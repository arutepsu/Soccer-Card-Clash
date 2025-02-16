package model.playingFiledComponent
import scala.collection.mutable
import util.Observable
import util.UndoManager
import model.cardComponent.cardFactory.DeckFactory
import model.playerComponent.Player
import model.cardComponent.base.{Card, Suit}
import model.playingFiledComponent.refillStrategy._
class FieldState(player1: Player, player2: Player, playingField: PlayingField) {

  def getPlayingField: PlayingField = playingField // ✅ Read-only getter
  def getPlayer1: Player = player1 // ✅ Read-only getter
  def getPlayer2: Player = player2  // ✅ Read-only getter

  // ✅ Strategy pattern: Use a dynamic refill strategy
  private var refillStrategy: RefillStrategy = new StandardRefillStrategy()

  def setRefillStrategy(strategy: RefillStrategy): Unit = {
    this.refillStrategy = strategy
  }

  private var player1Field: List[Card] = List()
  private var player2Field: List[Card] = List()

  private var player1Goalkeeper: Option[Card] = None
  private var player1Defenders: List[Card] = List()

  private var player2Goalkeeper: Option[Card] = None
  private var player2Defenders: List[Card] = List()

  // ✅ Getters and Setters for FieldState Variables
  def getPlayerField(player: Player): List[Card] =
    if (player == player1) player1Field else player2Field

  def setPlayerField(player: Player, newField: List[Card]): Unit =
    if (player == player1) player1Field = newField else player2Field = newField


  def getPlayerGoalkeeper(player: Player): Option[Card] = {
    if (player == player1) player1Goalkeeper else player2Goalkeeper
  }

  def getPlayerDefenders(player: Player): List[Card] = {
    if (player == player1) player1Defenders else player2Defenders
  }

  def setPlayerDefenders(player: Player, newDefenderField: List[Card]): Unit = {
    if (player == player1) {
      player1Defenders = newDefenderField
    } else if (player == player2) {
      player2Defenders = newDefenderField
    }
  }
  def setPlayerGoalkeeper(player: Player, goalkeeper: Option[Card]): Unit = {
    if (player == player1) {
      player1Goalkeeper = goalkeeper
    } else if (player == player2) {
      player2Goalkeeper = goalkeeper
    }
  }

  def setGoalkeeperForAttacker(card: Card): Unit = {
    if (playingField.getAttacker == player1) {
      player1Goalkeeper = Some(card)
    } else {
      player2Goalkeeper = Some(card)
    }
    playingField.notifyObservers() // ✅ Ensure UI refreshes
  }

  def initializeFields(): Unit = {
    println("✅ setPlayingField() was called!")
    refillField(player1, playingField.getHand(player1))
    refillField(player2, playingField.getHand(player1))
  }

  def removeDefenderCard(currentDefender: Player, defenderCard: Card): Unit = {
    if (currentDefender == player1) {
      player1Defenders = player1Defenders.filterNot(_ == defenderCard)
    } else {
      player2Defenders = player2Defenders.filterNot(_ == defenderCard)
    }
  }
  def refillDefenderField(defender: Player): Unit = {
    refillStrategy.refillDefenderField(this, defender)
  }

  def refillField(player: Player, hand: mutable.Queue[Card]): Unit = {
    refillStrategy.refillField(this, player, hand)
  }
  def allDefendersBeaten(currentDefender: Player): Boolean = {
    getPlayerDefenders(currentDefender).isEmpty
    }

  def getDefenderCard(player: Player, index: Int): Card = {
    val defenders = if (player == player1) player1Defenders else player2Defenders
    if (index < 0 || index >= defenders.size) throw new IndexOutOfBoundsException("Invalid defender index")
    defenders(index)
  }
}