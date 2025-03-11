package model.playingFiledComponent.manager.base
import model.cardComponent.ICard
import model.playerComponent.IPlayer
import model.playerComponent.factory.IPlayerFactory
import model.playingFiledComponent.IPlayingField
import model.playingFiledComponent.dataStructure._
import model.playingFiledComponent.manager.IDataManager
import model.playingFiledComponent.strategy.refillStrategy.*
import model.playingFiledComponent.strategy.refillStrategy.base.StandardRefillStrategy

import javax.inject.{Inject, Singleton}

import javax.inject.{Inject, Singleton}

@Singleton
class PlayerHandManager @Inject()(handCardsQueueFactory: IHandCardsQueueFactory) extends IPlayerHandManager {

  private var playerHands: Map[IPlayer, IHandCardsQueue] = Map()

  override def initializePlayerHands(player1: IPlayer,
                                     player1Cards: List[ICard],
                                     player2: IPlayer,
                                     player2Cards: List[ICard]): Unit = {
    println(s"🔄 Initializing hands for players: ${player1.name} (${player1.hashCode()}), ${player2.name} (${player2.hashCode()})")

    playerHands = Map(
      player1 -> handCardsQueueFactory.create(player1Cards),  // ✅ Use Factory
      player2 -> handCardsQueueFactory.create(player2Cards)   // ✅ Use Factory
    )

    println("✅ Player hands initialized successfully!")
    println(s"Existing players in Map: ${playerHands.keys.map(p => s"${p.name} (${p.hashCode()})").mkString(", ")}")
  }

  override def getPlayerHand(player: IPlayer): IHandCardsQueue = {
    if (!playerHands.contains(player)) {
      println(s"❌ ERROR: Player hand not found for player: ${player.name} (HashCode: ${player.hashCode()})")
      println(s"Existing Players in Map: ${playerHands.keys.map(p => s"${p.name} (Hash: ${p.hashCode()})").mkString(", ")}")
      throw new NoSuchElementException(s"Player hand not found: ${player.name}")
    }
    playerHands(player)
  }

  override def setPlayerHand(player: IPlayer, newHand: IHandCardsQueue): Unit =
    playerHands = playerHands.updated(player, newHand)

  override def getAttackingCard(attacker: IPlayer): ICard = getPlayerHand(attacker).getCards.last

  override def getDefenderCard(defender: IPlayer): ICard = getPlayerHand(defender).getCards.last
}

trait IPlayerHandManager {

  def initializePlayerHands(player1: IPlayer, player1Cards: List[ICard], player2: IPlayer, player2Cards: List[ICard]): Unit
  def getPlayerHand(player: IPlayer): IHandCardsQueue
  def setPlayerHand(player: IPlayer, newHand: IHandCardsQueue): Unit
  def getAttackingCard(attacker: IPlayer): ICard
  def getDefenderCard(defender: IPlayer): ICard

}