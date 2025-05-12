package de.htwg.se.soccercardclash.model.gameComponent.state.strategy.refillStrategy.base

import de.htwg.se.soccercardclash.model.cardComponent.ICard
import de.htwg.se.soccercardclash.model.cardComponent.dataStructure.*
import de.htwg.se.soccercardclash.model.gameComponent.state.components.IDataManager
import de.htwg.se.soccercardclash.model.gameComponent.state.strategy.refillStrategy.IRefillStrategy
import de.htwg.se.soccercardclash.model.playerComponent.IPlayer

import scala.collection.mutable

class RefillField {

  def refill(fieldState: IDataManager, player: IPlayer, hand: IHandCardsQueue): IDataManager = {
    val defenders = fieldState.getPlayerDefenders(player)
    val goalkeeper = fieldState.getPlayerGoalkeeper(player)

    val (defenderCount, goalkeeperCount) = (defenders.size, if (goalkeeper.isDefined) 1 else 0)
    val (newField, updatedHand) = determineFieldCards(hand, defenderCount, goalkeeperCount)

    if (newField.isEmpty || defenders.nonEmpty) {
      fieldState
    } else {
      val newGoalkeeper = goalkeeper.orElse(newField.maxByOption(_.valueToInt))
      val newDefenders = newField.filterNot(_ == newGoalkeeper.getOrElse(newField.head))

      fieldState
        .setPlayerField(player, newField)
        .setPlayerGoalkeeper(player, newGoalkeeper)
        .setPlayerDefenders(player, newDefenders)
        .setPlayerHand(player, updatedHand)
    }
  }

  private def determineFieldCards(hand: IHandCardsQueue, defenderCount: Int, goalkeeperCount: Int): (List[ICard], IHandCardsQueue) = {
    val cardsNeeded = (defenderCount, goalkeeperCount) match {
      case (0, 0) => 4
      case (1, _) => 2
      case (2, _) => 1
      case _      => 0
    }

    if cardsNeeded > 0 then
      hand.splitAtEnd(cardsNeeded)
    else
      (Nil, hand)
  }
}
