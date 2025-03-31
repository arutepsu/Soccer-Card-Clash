package de.htwg.se.soccercardclash.model.gameComponent.playingFiledComponent.manager

import de.htwg.se.soccercardclash.model.cardComponent.ICard
import de.htwg.se.soccercardclash.model.cardComponent.dataStructure.*
import de.htwg.se.soccercardclash.model.playerComponent.IPlayer
import de.htwg.se.soccercardclash.model.playerComponent.factory.IPlayerFactory
import de.htwg.se.soccercardclash.model.gameComponent.playingFiledComponent.IPlayingField
import de.htwg.se.soccercardclash.model.gameComponent.playingFiledComponent.manager.IDataManager
import de.htwg.se.soccercardclash.model.gameComponent.playingFiledComponent.strategy.refillStrategy.*
import de.htwg.se.soccercardclash.model.gameComponent.playingFiledComponent.strategy.refillStrategy.base.StandardRefillStrategy

import javax.inject.{Inject, Singleton}

@Singleton
class PlayerHandManager @Inject()(handCardsQueueFactory: IHandCardsQueueFactory) extends IPlayerHandManager {

  private var playerHands: Map[String, IHandCardsQueue] = Map()

  override def initializePlayerHands(player1: IPlayer,
                                     player1Cards: List[ICard],
                                     player2: IPlayer,
                                     player2Cards: List[ICard]): Unit = {
    playerHands = Map(
      player1.name -> handCardsQueueFactory.create(player1Cards),
      player2.name -> handCardsQueueFactory.create(player2Cards)
    )
  }

  override def getPlayerHand(player: IPlayer): IHandCardsQueue = {
    playerHands.getOrElse(player.name, {
      val newHand = handCardsQueueFactory.create(List())
      playerHands += (player.name -> newHand)
      newHand
    })
  }

  override def setPlayerHand(player: IPlayer, newHand: IHandCardsQueue): Unit = {
    playerHands += (player.name -> newHand)
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