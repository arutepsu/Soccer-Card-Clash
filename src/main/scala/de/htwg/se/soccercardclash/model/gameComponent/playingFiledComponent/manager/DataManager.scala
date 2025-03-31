package de.htwg.se.soccercardclash.model.gameComponent.playingFiledComponent.manager

import com.google.inject.{Inject, Singleton}
import de.htwg.se.soccercardclash.model.cardComponent.ICard
import de.htwg.se.soccercardclash.model.cardComponent.dataStructure.*
import de.htwg.se.soccercardclash.model.playerComponent.IPlayer
import de.htwg.se.soccercardclash.model.playerComponent.factory.IPlayerFactory
import de.htwg.se.soccercardclash.model.gameComponent.playingFiledComponent.IPlayingField
import de.htwg.se.soccercardclash.model.gameComponent.playingFiledComponent.manager.IDataManager
import de.htwg.se.soccercardclash.model.gameComponent.playingFiledComponent.strategy.refillStrategy.*
import de.htwg.se.soccercardclash.model.gameComponent.playingFiledComponent.strategy.refillStrategy.base.StandardRefillStrategy

import scala.collection.mutable

@Singleton
class DataManager @Inject()(
                             playingField: IPlayingField,
                             handManager: IPlayerHandManager,
                             fieldManager: IPlayerFieldManager
                           ) extends IDataManager {


  private var refillStrategy: IRefillStrategy = new StandardRefillStrategy()

  override def getPlayingField: IPlayingField = playingField

  override def initializePlayerHands(player1Cards: List[ICard], player2Cards: List[ICard]): Unit = {
    handManager.initializePlayerHands(getPlayer1, player1Cards, getPlayer2, player2Cards)
  }

  override def getDefenderCard(player: IPlayer, index: Int): ICard = {
    fieldManager.getDefenderCard(player, index)
  }

  override def getAttackingCard: ICard = handManager.getAttackingCard(playingField.getRoles.attacker)

  override def getDefenderCard: ICard = handManager.getDefenderCard(playingField.getRoles.defender)

  override def getPlayerHand(player: IPlayer): IHandCardsQueue = handManager.getPlayerHand(player)

  override def setPlayerHand(player: IPlayer, newHand: IHandCardsQueue): Unit = handManager.setPlayerHand(player, newHand)

  override def getPlayerField(player: IPlayer): List[ICard] = fieldManager.getPlayerField(player)

  override def setPlayerField(player: IPlayer, newField: List[ICard]): Unit = fieldManager.setPlayerField(player, newField)

  override def getPlayerGoalkeeper(player: IPlayer): Option[ICard] = fieldManager.getPlayerGoalkeeper(player)

  override def setPlayerGoalkeeper(player: IPlayer, goalkeeper: Option[ICard]): Unit = fieldManager.setPlayerGoalkeeper(player, goalkeeper)

  override def getPlayerDefenders(player: IPlayer): List[ICard] = fieldManager.getPlayerDefenders(player)

  override def setPlayerDefenders(player: IPlayer, defenders: List[ICard]): Unit = {
    fieldManager.setPlayerField(player, defenders)
  }

  override def removeDefenderCard(currentDefender: IPlayer, defenderCard: ICard): Unit =
    fieldManager.removeDefenderCard(currentDefender, defenderCard)

  override def removeDefenderGoalkeeper(currentDefender: IPlayer): Unit =
    fieldManager.removeDefenderGoalkeeper(currentDefender)

  override def allDefendersBeaten(currentDefender: IPlayer): Boolean =
    fieldManager.allDefendersBeaten(currentDefender)

  override def setRefillStrategy(strategy: IRefillStrategy): Unit = refillStrategy = strategy

  override def refillDefenderField(defender: IPlayer): Unit =
    refillStrategy.refillDefenderField(this, defender)

  override def initializeFields(): Unit = {
    refillStrategy.refillField(this, getPlayer1, handManager.getPlayerHand(getPlayer1).getCards)
    refillStrategy.refillField(this, getPlayer2, handManager.getPlayerHand(getPlayer2).getCards)
  }

  override def getPlayer1: IPlayer = playingField.getRoles.attacker

  override def getPlayer2: IPlayer = playingField.getRoles.defender

  override def setGoalkeeperForAttacker(card: ICard): Unit = {
    fieldManager.setGoalkeeperForAttacker(playingField, card)
  }

  override def clearAll(): Unit = {
    handManager.clearAll()
    fieldManager.clearAll()
  }
}

trait IDataManager {

  def getPlayingField: IPlayingField

  def getPlayer1: IPlayer

  def getPlayer2: IPlayer

  def initializePlayerHands(player1Cards: List[ICard], player2Cards: List[ICard]): Unit

  def getPlayerHand(player: IPlayer): IHandCardsQueue

  def setPlayerHand(player: IPlayer, newHand: IHandCardsQueue): Unit

  def getAttackingCard: ICard

  def getDefenderCard: ICard

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

  def initializeFields(): Unit

  def refillDefenderField(defender: IPlayer): Unit

  def setRefillStrategy(strategy: IRefillStrategy): Unit

  def clearAll(): Unit
}
