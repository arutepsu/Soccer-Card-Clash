package model.playingFiledComponent
import scala.collection.mutable
import model.playerComponent.Player
import model.cardComponent.base.Card
import model.playingFiledComponent.strategy.refillStrategy._
import model.playingFiledComponent.factories.PlayerHandFactory
class FieldState(val playingField: PlayingField, val player1: Player, val player2: Player) {
  def getPlayingField: PlayingField = playingField // ✅ Read-only getter
  def getPlayer1: Player = player1 // ✅ Read-only getter
  def getPlayer2: Player = player2  // ✅ Read-only getter

  // ✅ Player Hands (moved from PlayingField)
  private var player1Cards: mutable.Queue[Card] = PlayerHandFactory.createHand(List())
  private var player2Cards: mutable.Queue[Card] = PlayerHandFactory.createHand(List())

  /** 📌 Initializes player hands */
  def initializePlayerHands(player1Hand: List[Card], player2Hand: List[Card]): Unit = {
    player1Cards = PlayerHandFactory.createHand(player1Hand)
    player2Cards = PlayerHandFactory.createHand(player2Hand)
  }

  def getAttackingCard: Card = getPlayerHand(playingField.getAttacker).last

  def getDefenderCard: Card = getPlayerHand(playingField.getDefender).last

  // ✅ Strategy pattern: Use a dynamic refill strategy
  private var refillStrategy: RefillStrategy = new StandardRefillStrategy()
  def setRefillStrategy(strategy: RefillStrategy): Unit = refillStrategy = strategy

  // ✅ Field State (player defenders and goalkeepers)
  private var player1Field: List[Card] = List()
  private var player2Field: List[Card] = List()

  private var player1Goalkeeper: Option[Card] = None
  private var player1Defenders: List[Card] = List()

  private var player2Goalkeeper: Option[Card] = None
  private var player2Defenders: List[Card] = List()

  /** 📌 ========== GETTERS & SETTERS ========== */

  def getPlayerField(player: Player): List[Card] =
    if (player == player1) player1Field else player2Field

  def setPlayerField(player: Player, newField: List[Card]): Unit =
    if (player == player1) player1Field = newField else player2Field = newField

  def getPlayerGoalkeeper(player: Player): Option[Card] =
    if (player == player1) player1Goalkeeper else player2Goalkeeper

  def setPlayerGoalkeeper(player: Player, goalkeeper: Option[Card]): Unit =
    if (player == player1) player1Goalkeeper = goalkeeper else player2Goalkeeper = goalkeeper

  def getPlayerDefenders(player: Player): List[Card] =
    if (player == player1) player1Defenders else player2Defenders

  def setPlayerDefenders(player: Player, newDefenderField: List[Card]): Unit =
    if (player == player1) player1Defenders = newDefenderField else player2Defenders = newDefenderField

  /** 📌 ========== PLAYER HANDS ========== */

  def getPlayerHand(player: Player): mutable.Queue[Card] =
    if (player == player1) player1Cards else player2Cards

  def setPlayerHand(player: Player, newHand: mutable.Queue[Card]): Unit =
    if (player == player1) player1Cards = newHand else player2Cards = newHand

  /** 📌 ========== FIELD MANAGEMENT ========== */

  def setGoalkeeperForAttacker(card: Card): Unit = {
    if (playingField.getAttacker == player1) player1Goalkeeper = Some(card)
    else player2Goalkeeper = Some(card)

    playingField.notifyObservers() // ✅ Ensure UI refreshes
  }

  def initializeFields(): Unit = {
    println("✅ initializeFields() was called!")
    refillField(player1, getPlayerHand(player1))
    refillField(player2, getPlayerHand(player2))
  }

  /** 📌 ========== REFILL STRATEGY ========== */

  def refillDefenderField(defender: Player): Unit =
    refillStrategy.refillDefenderField(this, defender)

  def refillField(player: Player, hand: mutable.Queue[Card]): Unit =
    refillStrategy.refillField(this, player, hand)

  /** 📌 ========== GAMEPLAY HELPERS ========== */

  def removeDefenderCard(currentDefender: Player, defenderCard: Card): Unit =
    if (currentDefender == player1) {
      player1Defenders = player1Defenders.filterNot(_ == defenderCard)
    } else {
      player2Defenders = player2Defenders.filterNot(_ == defenderCard)
    }

  def allDefendersBeaten(currentDefender: Player): Boolean =
    getPlayerDefenders(currentDefender).isEmpty

  def getDefenderCard(player: Player, index: Int): Card = {
    val defenders = if (player == player1) player1Defenders else player2Defenders
    if (index < 0 || index >= defenders.size) throw new IndexOutOfBoundsException("Invalid defender index")
    defenders(index)
  }
}
