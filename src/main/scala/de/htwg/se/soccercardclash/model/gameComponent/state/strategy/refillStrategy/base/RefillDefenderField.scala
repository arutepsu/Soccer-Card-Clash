package de.htwg.se.soccercardclash.model.gameComponent.state.strategy.refillStrategy.base

import de.htwg.se.soccercardclash.model.cardComponent.ICard
import de.htwg.se.soccercardclash.model.cardComponent.dataStructure.IHandCardsQueue
import de.htwg.se.soccercardclash.model.gameComponent.state.components.IDataManager
import de.htwg.se.soccercardclash.model.gameComponent.state.strategy.refillStrategy.IRefillStrategy
import de.htwg.se.soccercardclash.model.playerComponent.IPlayer

class RefillDefenderField {

  def refill(fieldState: IDataManager, defender: IPlayer): IDataManager = {
    val defenderHand = fieldState.getPlayerHand(defender)
    val defenders = fieldState.getPlayerDefenders(defender) // List[Option[ICard]]
    val goalkeeper = fieldState.getPlayerGoalkeeper(defender)

    if (defenders.forall(_.isEmpty) && goalkeeper.isEmpty) {
      refillCompletely(fieldState, defender, defenderHand)
    } else if (defenders.count(_.isDefined) < 3) {
      refillPartial(fieldState, defender, defenderHand, defenders, goalkeeper)
    } else {
      fieldState
    }
  }

  private def refillCompletely(
                                fieldState: IDataManager,
                                defender: IPlayer,
                                defenderHand: IHandCardsQueue
                              ): IDataManager = {
    val (newFieldCards, updatedHand) = defenderHand.splitAtEnd(4)
    val (goalkeeper, defendersFlat) = extractGoalkeeper(newFieldCards)

    val defenders: List[Option[ICard]] =
      defendersFlat.map(Some(_)).padTo(3, None)

    fieldState
      .updatePlayerGoalkeeper(defender, Some(goalkeeper))
      .updatePlayerDefenders(defender, defenders)
      .updatePlayerHand(defender, updatedHand)
  }

  private def refillPartial(
                             fieldState: IDataManager,
                             defender: IPlayer,
                             defenderHand: IHandCardsQueue,
                             defenderField: List[Option[ICard]],
                             goalkeeperOpt: Option[ICard]
                           ): IDataManager = {
    val neededSlots = defenderField.count(_.isEmpty)

    val (newCards, updatedHand) =
      if (neededSlots > 0) defenderHand.splitAtEnd(neededSlots)
      else (Nil, defenderHand)

    // Fill only empty slots
    val cardIterator = newCards.iterator
    val updatedDefenders: List[Option[ICard]] = defenderField.map {
      case None => if (cardIterator.hasNext) Some(cardIterator.next()) else None
      case some => some
    }

    // Extract goalkeeper if needed
    val (goalkeeper, updatedDefendersWithGoalieBack): (ICard, List[ICard]) =
      adjustGoalkeeper(updatedDefenders, goalkeeperOpt)

    // Map flattened defenders back into original slots
    val replacedCards = updatedDefendersWithGoalieBack.iterator
    val adjustedDefenders: List[Option[ICard]] = updatedDefenders.map {
      case Some(_) => if (replacedCards.hasNext) Some(replacedCards.next()) else None
      case None    => if (replacedCards.hasNext) Some(replacedCards.next()) else None
    }

    fieldState
      .updatePlayerGoalkeeper(defender, Some(goalkeeper))
      .updatePlayerDefenders(defender, adjustedDefenders)
      .updatePlayerHand(defender, updatedHand)
  }

  private def extractGoalkeeper(cards: List[ICard]): (ICard, List[ICard]) = {
    val highestCard = cards.maxBy(_.valueToInt)
    (highestCard, cards.filterNot(_ == highestCard))
  }

  private def adjustGoalkeeper(
                                defenders: List[Option[ICard]],
                                goalkeeperOpt: Option[ICard]
                              ): (ICard, List[ICard]) = {
    val defenderCards = defenders.flatten

    goalkeeperOpt match {
      case Some(goalkeeper) =>
        val highestDefender = defenderCards.maxBy(_.valueToInt)
        if (highestDefender.valueToInt > goalkeeper.valueToInt) {
          (highestDefender, defenderCards.filterNot(_ == highestDefender) :+ goalkeeper)
        } else {
          (goalkeeper, defenderCards)
        }
      case None =>
        extractGoalkeeper(defenderCards)
    }
  }
}
