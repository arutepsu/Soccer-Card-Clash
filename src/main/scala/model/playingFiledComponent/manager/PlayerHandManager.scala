package model.playingFiledComponent.manager

import model.cardComponent.ICard
import model.playerComponent.IPlayer
import model.playerComponent.factory.IPlayerFactory
import model.playingFiledComponent.IPlayingField
import model.playingFiledComponent.dataStructure.*
import model.playingFiledComponent.manager.IDataManager
import model.playingFiledComponent.strategy.refillStrategy.*
import model.playingFiledComponent.strategy.refillStrategy.base.StandardRefillStrategy

import javax.inject.{Inject, Singleton}

@Singleton
class PlayerHandManager @Inject()(handCardsQueueFactory: IHandCardsQueueFactory) extends IPlayerHandManager {

  private var playerHands: Map[IPlayer, IHandCardsQueue] = Map()

  override def initializePlayerHands(player1: IPlayer,
                                     player1Cards: List[ICard],
                                     player2: IPlayer,
                                     player2Cards: List[ICard]): Unit = {

    playerHands = playerHands.updated(player1, handCardsQueueFactory.create(player1Cards))
    playerHands = playerHands.updated(player2, handCardsQueueFactory.create(player2Cards))

  }

  override def getPlayerHand(player: IPlayer): IHandCardsQueue = {
    playerHands.find { case (p, _) => p.hashCode() == player.hashCode() } match {

      case Some((_, handQueue)) =>
        handQueue

      case None =>
        val newHandQueue = handCardsQueueFactory.create(List())
        playerHands += (player -> newHandQueue)

        newHandQueue
    }
  }

  override def setPlayerHand(player: IPlayer, newHand: IHandCardsQueue): Unit = {
    playerHands = playerHands.updated(player, newHand)
  }

  override def getAttackingCard(attacker: IPlayer): ICard = getPlayerHand(attacker).getCards.last

  override def getDefenderCard(defender: IPlayer): ICard = getPlayerHand(defender).getCards.last

  override def clearAll(): Unit = {
    playerHands = Map.empty
  }

}

trait IPlayerHandManager {

  def initializePlayerHands(player1: IPlayer, player1Cards: List[ICard], player2: IPlayer, player2Cards: List[ICard]): Unit
  def getPlayerHand(player: IPlayer): IHandCardsQueue
  def setPlayerHand(player: IPlayer, newHand: IHandCardsQueue): Unit
  def getAttackingCard(attacker: IPlayer): ICard
  def getDefenderCard(defender: IPlayer): ICard
  def clearAll(): Unit

}