package de.htwg.se.soccercardclash.model.gameComponent.playingFiledComponent.strategy.refillStrategy.base

import de.htwg.se.soccercardclash.model.cardComponent.ICard
import de.htwg.se.soccercardclash.model.cardComponent.dataStructure.*
import de.htwg.se.soccercardclash.model.playerComponent.IPlayer
import de.htwg.se.soccercardclash.model.gameComponent.playingFiledComponent.manager.IDataManager
import de.htwg.se.soccercardclash.model.gameComponent.playingFiledComponent.strategy.refillStrategy.IRefillStrategy
import scala.collection.mutable

class RefillField {

  def refill(fieldState: IDataManager, player: IPlayer, hand: IHandCardsQueue): IHandCardsQueue = {
    val defenders = fieldState.getPlayerDefenders(player)
    val goalkeeper = fieldState.getPlayerGoalkeeper(player)

    val (defenderCount, goalkeeperCount) = (defenders.size, if (goalkeeper.isDefined) 1 else 0)

    val (newField, updatedHand) = determineFieldCards(hand, defenderCount, goalkeeperCount)

    if (newField.isEmpty) {
      hand
    } else {
      val newGoalkeeper = goalkeeper.orElse(newField.maxByOption(_.valueToInt))
      val newDefenders = newField.filterNot(_ == newGoalkeeper.getOrElse(newField.head))

      updateFieldState(fieldState, player, newField, newGoalkeeper, newDefenders)
      updatedHand
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


  private def updateFieldState(
                                fieldState: IDataManager,
                                player: IPlayer,
                                field: List[ICard],
                                goalkeeper: Option[ICard],
                                defenders: List[ICard]
                              ): Unit = {
    
    val existingDefenders = fieldState.getPlayerDefenders(player)
    if (existingDefenders.nonEmpty) {
      return
    }
    
    fieldState.setPlayerField(player, field)
    fieldState.setPlayerGoalkeeper(player, goalkeeper)
    fieldState.setPlayerDefenders(player, defenders)
  }

}
