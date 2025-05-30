package de.htwg.se.soccercardclash.model.gameComponent.action.strategy.flow.refillStrategy.base

import de.htwg.se.soccercardclash.model.cardComponent.ICard
import de.htwg.se.soccercardclash.model.cardComponent.dataStructure.*
import de.htwg.se.soccercardclash.model.gameComponent.action.strategy.flow.refillStrategy.IRefillStrategy
import de.htwg.se.soccercardclash.model.gameComponent.components.IGameCards
import de.htwg.se.soccercardclash.model.playerComponent.IPlayer

import scala.collection.mutable

class FieldRefillStrategy extends IFieldRefillStrategy {

  override def refill(gameCards: IGameCards, player: IPlayer, hand: IHandCardsQueue): IGameCards = {
    val defenders = gameCards.getPlayerDefenders(player)
    val goalkeeper = gameCards.getPlayerGoalkeeper(player)

    val emptySlots = defenders.count(_.isEmpty)
    val needsFullRefill = defenders.forall(_.isEmpty) && goalkeeper.isEmpty

    if (!needsFullRefill) return gameCards

    val (drawnCards, updatedHand) = hand.splitAtEnd(4)

    val newGoalkeeper: Option[ICard] =
      goalkeeper.orElse(drawnCards.maxByOption(_.valueToInt))

    val defendersWithoutGK: List[ICard] =
      drawnCards.filterNot(_ == newGoalkeeper.getOrElse(drawnCards.head))

    val paddedDefenders: List[Option[ICard]] =
      defendersWithoutGK.map(Some(_)).padTo(3, None)

    gameCards
      .newPlayerDefenders(player, paddedDefenders)
      .newPlayerGoalkeeper(player, newGoalkeeper)
      .newPlayerHand(player, updatedHand)
  }

  private def determineFieldCards(hand: IHandCardsQueue, defenderCount: Int, goalkeeperCount: Int): (List[ICard], IHandCardsQueue) = {
    val cardsNeeded = (defenderCount, goalkeeperCount) match {
      case (0, 0) => 4
      case (1, _) => 2
      case (2, _) => 1
      case _ => 0
    }

    if cardsNeeded > 0 then
      hand.splitAtEnd(cardsNeeded)
    else
      (Nil, hand)
  }
}

trait IFieldRefillStrategy {
  def refill(gameCards: IGameCards, player: IPlayer, hand: IHandCardsQueue): IGameCards
}