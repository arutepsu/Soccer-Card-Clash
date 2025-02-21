package model.playingFiledComponent.manager.base

import com.google.inject.{Inject, Singleton}
import model.cardComponent.ICard
import model.playerComponent.IPlayer
import model.playerComponent.base.factories.IPlayerFactory
import model.playingFiledComponent.IPlayingField
import model.playingFiledComponent.dataStructure.HandCardsQueue
import model.playingFiledComponent.manager.IDataManager
import model.playingFiledComponent.strategy.refillStrategy.*

import scala.collection.mutable
@Singleton
class DataManager @Inject() (
                              playingField: IPlayingField,
                              player1: IPlayer,
                              player2: IPlayer
                            ) extends IDataManager {
  override def getPlayingField: IPlayingField = playingField
  override def getPlayer1: IPlayer = player1
  override def getPlayer2: IPlayer = player2

  private var player1Hand: HandCardsQueue = new HandCardsQueue(List())
  private var player2Hand: HandCardsQueue = new HandCardsQueue(List())

  override def initializePlayerHands(player1Cards: List[ICard], player2Cards: List[ICard]): Unit = {
    player1Hand = new HandCardsQueue(player1Cards)
    player2Hand = new HandCardsQueue(player2Cards)
  }

  override def getAttackingCard: ICard = getPlayerHand(playingField.getAttacker).getCards.last
  override def getDefenderCard: ICard = getPlayerHand(playingField.getDefender).getCards.last

  override def getPlayerHand(player: IPlayer): HandCardsQueue =
    if (player == player1) player1Hand else player2Hand

  override def setPlayerHand(player: IPlayer, newHand: HandCardsQueue): Unit =
    if (player == player1) player1Hand = newHand else player2Hand = newHand

  private var refillStrategy: RefillStrategy = new StandardRefillStrategy()

  override def setRefillStrategy(strategy: RefillStrategy): Unit = refillStrategy = strategy

  override def refillDefenderField(defender: IPlayer): Unit =
    refillStrategy.refillDefenderField(this, defender)

  private def refillField(player: IPlayer, hand: HandCardsQueue): Unit =
    refillStrategy.refillField(this, player, hand.getCards)

  private var player1Field: List[ICard] = List()
  private var player2Field: List[ICard] = List()

  private var player1Goalkeeper: Option[ICard] = None
  private var player1Defenders: List[ICard] = List()

  private var player2Goalkeeper: Option[ICard] = None
  private var player2Defenders: List[ICard] = List()

  override def getPlayerField(player: IPlayer): List[ICard] =
    if (player == player1) player1Field else player2Field

  override  def setPlayerField(player: IPlayer, newField: List[ICard]): Unit =
    if (player == player1) player1Field = newField else player2Field = newField

  override def getPlayerGoalkeeper(player: IPlayer): Option[ICard] =
    if (player == player1) player1Goalkeeper else player2Goalkeeper

  override def setPlayerGoalkeeper(player: IPlayer, goalkeeper: Option[ICard]): Unit =
    if (player == player1) player1Goalkeeper = goalkeeper else player2Goalkeeper = goalkeeper

  override def getPlayerDefenders(player: IPlayer): List[ICard] =
    if (player == player1) player1Defenders else player2Defenders

  override def setPlayerDefenders(player: IPlayer, newDefenderField: List[ICard]): Unit =
    if (player == player1) player1Defenders = newDefenderField else player2Defenders = newDefenderField

  override def setGoalkeeperForAttacker(card: ICard): Unit = {
    if (playingField.getAttacker == player1) player1Goalkeeper = Some(card)
    else player2Goalkeeper = Some(card)

    playingField.notifyObservers()
  }

  override def initializeFields(): Unit = {
    refillField(player1, player1Hand)
    refillField(player2, player2Hand)
  }
  override def removeDefenderCard(currentDefender: IPlayer, defenderCard: ICard): Unit = {
    val cardExists = (if (currentDefender == player1) player1Defenders else player2Defenders).contains(defenderCard)

    if (currentDefender == player1) {
      player1Defenders = player1Defenders.filterNot(_ == defenderCard)
    } else {
      player2Defenders = player2Defenders.filterNot(_ == defenderCard)
    }
  }
  override def removeDefenderGoalkeeper(currentDefender: IPlayer): Unit = {
    if (currentDefender == player1) {
      player1Goalkeeper = None
    } else {
      player2Goalkeeper = None
    }
  }

  override def allDefendersBeaten(currentDefender: IPlayer): Boolean =
    getPlayerDefenders(currentDefender).isEmpty

  override def getDefenderCard(player: IPlayer, index: Int): ICard = {
    val defenders = if (player == player1) player1Defenders else player2Defenders
    if (index < 0 || index >= defenders.size)
      throw new IndexOutOfBoundsException("Invalid defender index")
    defenders(index)
  }
}