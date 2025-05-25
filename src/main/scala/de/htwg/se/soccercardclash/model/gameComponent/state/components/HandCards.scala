package de.htwg.se.soccercardclash.model.gameComponent.state.components

import de.htwg.se.soccercardclash.model.cardComponent.ICard
import de.htwg.se.soccercardclash.model.cardComponent.dataStructure.*
import de.htwg.se.soccercardclash.model.cardComponent.dataStructure.{IHandCardsQueue, IHandCardsQueueFactory}
import de.htwg.se.soccercardclash.model.gameComponent.state.IGameState
import de.htwg.se.soccercardclash.model.gameComponent.action.strategy.refillStrategy.*
import de.htwg.se.soccercardclash.model.gameComponent.action.strategy.refillStrategy.base.StandardRefillStrategy
import de.htwg.se.soccercardclash.model.playerComponent.IPlayer
import de.htwg.se.soccercardclash.model.playerComponent.factory.IPlayerFactory


class HandCardsFactory(val queueFactory: IHandCardsQueueFactory
                              ) extends IHandCardsFactory {

  override def empty: IHandCards =
    HandCards(Map.empty, queueFactory)

  override def create(
                       player1: IPlayer,
                       player1Cards: List[ICard],
                       player2: IPlayer,
                       player2Cards: List[ICard]
                     ): IHandCards = {
    HandCards(
      Map(
        player1.name -> queueFactory.create(player1Cards),
        player2.name -> queueFactory.create(player2Cards)
      ),
      queueFactory
    )
  }
}

trait IHandCardsFactory {
  def empty: IHandCards

  def create(
              player1: IPlayer,
              player1Cards: List[ICard],
              player2: IPlayer,
              player2Cards: List[ICard]
            ): IHandCards
}

case class HandCards(
                              val playerHands: Map[String, IHandCardsQueue],
                              handCardsQueueFactory: IHandCardsQueueFactory
                            ) extends IHandCards {

  override def initializePlayerHands(
                                      player1: IPlayer,
                                      player1Cards: List[ICard],
                                      player2: IPlayer,
                                      player2Cards: List[ICard]
                                    ): IHandCards = {
    HandCards(
      Map(
        player1.name -> handCardsQueueFactory.create(player1Cards),
        player2.name -> handCardsQueueFactory.create(player2Cards)
      ),
      handCardsQueueFactory
    )
  }

  override def newPlayerHand(player: IPlayer, newHand: IHandCardsQueue): IHandCards = {
    HandCards(playerHands + (player.name -> newHand), handCardsQueueFactory)
  }

  override def getAttackingCard(attacker: IPlayer): ICard =
    getPlayerHand(attacker).toList.last

  override def getPlayerHand(player: IPlayer): IHandCardsQueue =
    playerHands.getOrElse(player.name, handCardsQueueFactory.create(List()))

  override def getDefenderCard(defender: IPlayer): ICard =
    getPlayerHand(defender).toList.last

}


trait IHandCards {
  def initializePlayerHands(player1: IPlayer, player1Cards: List[ICard], player2: IPlayer, player2Cards: List[ICard]): IHandCards

  def getPlayerHand(player: IPlayer): IHandCardsQueue

  def newPlayerHand(player: IPlayer, newHand: IHandCardsQueue): IHandCards

  def getAttackingCard(attacker: IPlayer): ICard

  def getDefenderCard(defender: IPlayer): ICard
}
