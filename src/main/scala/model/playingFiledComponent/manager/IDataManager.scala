package model.playingFiledComponent.manager

import model.playingFiledComponent.IPlayingField
import model.playerComponent.IPlayer
import model.cardComponent.ICard
import model.playingFiledComponent.dataStructure.IHandCardsQueue
import model.playingFiledComponent.strategy.refillStrategy.IRefillStrategy

trait IDataManager {

  /** Playing Field Management **/
  def getPlayingField: IPlayingField
  def getPlayer1: IPlayer
  def getPlayer2: IPlayer

  /** Hand Management **/
  def initializePlayerHands(player1Cards: List[ICard], player2Cards: List[ICard]): Unit
  def getPlayerHand(player: IPlayer): IHandCardsQueue
  def setPlayerHand(player: IPlayer, newHand: IHandCardsQueue): Unit
  def getAttackingCard: ICard
  def getDefenderCard: ICard

  /** Field Management **/
  def getPlayerGoalkeeper(player: IPlayer): Option[ICard]
  def setPlayerGoalkeeper(player: IPlayer, goalkeeper: Option[ICard]): Unit
  def getPlayerDefenders(player: IPlayer): List[ICard]
  def setPlayerDefenders(player: IPlayer, newDefenderField: List[ICard]): Unit
  def setGoalkeeperForAttacker(card: ICard): Unit
  def removeDefenderCard(currentDefender: IPlayer, defenderCard: ICard): Unit
  def removeDefenderGoalkeeper(currentDefender: IPlayer): Unit
  def allDefendersBeaten(currentDefender: IPlayer): Boolean
  def getDefenderCard(player: IPlayer, index: Int): ICard
  def getPlayerField(player: IPlayer): List[ICard]
  def setPlayerField(player: IPlayer, newField: List[ICard]): Unit

  /** Refill Strategies **/
  def initializeFields(): Unit
  def refillDefenderField(defender: IPlayer): Unit
  def setRefillStrategy(strategy: IRefillStrategy): Unit
  
}