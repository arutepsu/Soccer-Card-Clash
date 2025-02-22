package model.playingFiledComponent.manager.base

import com.google.inject.{Inject, Singleton}
import model.cardComponent.ICard
import model.playerComponent.IPlayer
import model.playerComponent.factory.IPlayerFactory
import model.playingFiledComponent.IPlayingField
import model.playingFiledComponent.dataStructure._
import model.playingFiledComponent.manager.IDataManager
import model.playingFiledComponent.strategy.refillStrategy.*
import model.playingFiledComponent.strategy.refillStrategy.base.StandardRefillStrategy

import scala.collection.mutable
@Singleton
class DataManager @Inject() (
                              playingField: IPlayingField,
                              player1: IPlayer,
                              player2: IPlayer,
                              handManager: IPlayerHandManager,
                              fieldManager: IPlayerFieldManager
                            ) extends IDataManager {

  private var refillStrategy: IRefillStrategy = new StandardRefillStrategy()

  override def getPlayingField: IPlayingField = playingField
  override def getPlayer1: IPlayer = player1
  override def getPlayer2: IPlayer = player2

  /** Hand Management **/
  override def initializePlayerHands(player1Cards: List[ICard], player2Cards: List[ICard]): Unit = {
    handManager.initializePlayerHands(player1, player1Cards, player2, player2Cards)
  }

  override def getDefenderCard(player: IPlayer, index: Int): ICard = {
    fieldManager.getDefenderCard(player, index)
  }

  override def getAttackingCard: ICard = handManager.getAttackingCard(playingField.getAttacker)
  override def getDefenderCard: ICard = handManager.getDefenderCard(playingField.getDefender)

  override def getPlayerHand(player: IPlayer): IHandCardsQueue = handManager.getPlayerHand(player)
  override def setPlayerHand(player: IPlayer, newHand: IHandCardsQueue): Unit = handManager.setPlayerHand(player, newHand)

  /** Field Management **/
  override def getPlayerField(player: IPlayer): List[ICard] = fieldManager.getPlayerField(player)
  override def setPlayerField(player: IPlayer, newField: List[ICard]): Unit = fieldManager.setPlayerField(player, newField)

  override def getPlayerGoalkeeper(player: IPlayer): Option[ICard] = fieldManager.getPlayerGoalkeeper(player)
  override def setPlayerGoalkeeper(player: IPlayer, goalkeeper: Option[ICard]): Unit = fieldManager.setPlayerGoalkeeper(player, goalkeeper)

  override def getPlayerDefenders(player: IPlayer): List[ICard] = fieldManager.getPlayerDefenders(player)
  override def setPlayerDefenders(player: IPlayer, newDefenders: List[ICard]): Unit = fieldManager.setPlayerDefenders(player, newDefenders)

  override def removeDefenderCard(currentDefender: IPlayer, defenderCard: ICard): Unit =
    fieldManager.removeDefenderCard(currentDefender, defenderCard)

  override def removeDefenderGoalkeeper(currentDefender: IPlayer): Unit =
    fieldManager.removeDefenderGoalkeeper(currentDefender)

  override def allDefendersBeaten(currentDefender: IPlayer): Boolean =
    fieldManager.allDefendersBeaten(currentDefender)

  /** Refill Strategies **/
  override def setRefillStrategy(strategy: IRefillStrategy): Unit = refillStrategy = strategy

  override def refillDefenderField(defender: IPlayer): Unit =
    refillStrategy.refillDefenderField(this, defender)

  override def initializeFields(): Unit = {
    refillStrategy.refillField(this, player1, handManager.getPlayerHand(player1).getCards)
    refillStrategy.refillField(this, player2, handManager.getPlayerHand(player2).getCards)
  }

  override def setGoalkeeperForAttacker(card: ICard): Unit = {
    fieldManager.setGoalkeeperForAttacker(playingField, card)
  }

}
