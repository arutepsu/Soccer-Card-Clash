package model.playingFiledComponent

import model.cardComponent.ICard

import scala.collection.mutable
import model.playerComponent.IPlayer
import model.playerComponent.base.Player
import model.playerComponent.playerFactory.PlayerHandFactory
import model.playingFiledComponent.dataStructure.HandCardsQueue
import model.playingFiledComponent.strategy.refillStrategy.*
class FieldState(val playingField: PlayingField, val player1: IPlayer, val player2: IPlayer) {

  /** 📌 ========== CORE GETTERS ========== */
  def getPlayingField: PlayingField = playingField
  def getPlayer1: IPlayer = player1
  def getPlayer2: IPlayer = player2

  /** 📌 ========== PLAYER HANDS (Using `ActiveHandState`) ========== */
  private var player1Hand: HandCardsQueue = new HandCardsQueue(List())
  private var player2Hand: HandCardsQueue = new HandCardsQueue(List())

//  def initializePlayerHands(player1Cards: List[Card], player2Cards: List[Card]): Unit = {
//    player1Hand = new HandCardsQueue(player1Cards)
//    player2Hand = new HandCardsQueue(player2Cards)
//  }
  def initializePlayerHands(player1Cards: List[ICard], player2Cards: List[ICard]): Unit = {
    player1Hand = new HandCardsQueue(player1Cards)
    player2Hand = new HandCardsQueue(player2Cards)
  }

  def getAttackingCard: ICard = getPlayerHand(playingField.getAttacker).getCards.last
  def getDefenderCard: ICard = getPlayerHand(playingField.getDefender).getCards.last

  def getPlayerHand(player: IPlayer): HandCardsQueue =
    if (player == player1) player1Hand else player2Hand

  def setPlayerHand(player: IPlayer, newHand: HandCardsQueue): Unit =
    if (player == player1) player1Hand = newHand else player2Hand = newHand

  /** 📌 ========== REFILL STRATEGY ========== */
  private var refillStrategy: RefillStrategy = new StandardRefillStrategy()

  def setRefillStrategy(strategy: RefillStrategy): Unit = refillStrategy = strategy

  def refillDefenderField(defender: IPlayer): Unit =
    refillStrategy.refillDefenderField(this, defender)

  def refillField(player: IPlayer, hand: HandCardsQueue): Unit =
    refillStrategy.refillField(this, player, hand.getCards)

  /** 📌 ========== PLAYER FIELD STATE ========== */
  private var player1Field: List[ICard] = List()
  private var player2Field: List[ICard] = List()

  private var player1Goalkeeper: Option[ICard] = None
  private var player1Defenders: List[ICard] = List()

  private var player2Goalkeeper: Option[ICard] = None
  private var player2Defenders: List[ICard] = List()

  def getPlayerField(player: IPlayer): List[ICard] =
    if (player == player1) player1Field else player2Field

  def setPlayerField(player: IPlayer, newField: List[ICard]): Unit =
    if (player == player1) player1Field = newField else player2Field = newField

  def getPlayerGoalkeeper(player: IPlayer): Option[ICard] =
    if (player == player1) player1Goalkeeper else player2Goalkeeper

  def setPlayerGoalkeeper(player: IPlayer, goalkeeper: Option[ICard]): Unit =
    if (player == player1) player1Goalkeeper = goalkeeper else player2Goalkeeper = goalkeeper

  def getPlayerDefenders(player: IPlayer): List[ICard] =
    if (player == player1) player1Defenders else player2Defenders

  def setPlayerDefenders(player: IPlayer, newDefenderField: List[ICard]): Unit =
    if (player == player1) player1Defenders = newDefenderField else player2Defenders = newDefenderField

  /** 📌 ========== FIELD MANAGEMENT ========== */
  def setGoalkeeperForAttacker(card: ICard): Unit = {
    if (playingField.getAttacker == player1) player1Goalkeeper = Some(card)
    else player2Goalkeeper = Some(card)

    playingField.notifyObservers() // ✅ Ensure UI refreshes
  }

  def initializeFields(): Unit = {
    println("✅ initializeFields() was called!")
    refillField(player1, player1Hand)
    refillField(player2, player2Hand)
  }

  /** 📌 ========== GAMEPLAY HELPERS ========== */
  def removeDefenderCard(currentDefender: IPlayer, defenderCard: ICard): Unit =
    if (currentDefender == player1) {
      player1Defenders = player1Defenders.filterNot(_ == defenderCard)
    } else {
      player2Defenders = player2Defenders.filterNot(_ == defenderCard)
    }

  def allDefendersBeaten(currentDefender: IPlayer): Boolean =
    getPlayerDefenders(currentDefender).isEmpty

  def getDefenderCard(player: IPlayer, index: Int): ICard = {
    val defenders = if (player == player1) player1Defenders else player2Defenders
    if (index < 0 || index >= defenders.size)
      throw new IndexOutOfBoundsException("Invalid defender index")
    defenders(index)
  }
}
