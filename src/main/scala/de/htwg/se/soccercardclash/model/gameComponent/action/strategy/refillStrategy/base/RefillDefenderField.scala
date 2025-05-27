package de.htwg.se.soccercardclash.model.gameComponent.action.strategy.refillStrategy.base

import de.htwg.se.soccercardclash.model.cardComponent.ICard
import de.htwg.se.soccercardclash.model.cardComponent.dataStructure.IHandCardsQueue
import de.htwg.se.soccercardclash.model.gameComponent.action.strategy.refillStrategy.IRefillStrategy
import de.htwg.se.soccercardclash.model.gameComponent.state.components.IGameCards
import de.htwg.se.soccercardclash.model.playerComponent.IPlayer

class RefillDefenderField {

  def refill(gameCards: IGameCards, defender: IPlayer): IGameCards = {
    val defenderHand = gameCards.getPlayerHand(defender)
    val defenders = gameCards.getPlayerDefenders(defender)
    val goalkeeper = gameCards.getPlayerGoalkeeper(defender)

    if (defenders.forall(_.isEmpty) && goalkeeper.isEmpty) {
      refillCompletely(gameCards, defender, defenderHand)
    } else if (defenders.count(_.isDefined) < 3) {
      refillPartial(gameCards, defender, defenderHand, defenders, goalkeeper)
    } else {
      gameCards
    }
  }

  private def refillCompletely(
                                gameCards: IGameCards,
                                defender: IPlayer,
                                defenderHand: IHandCardsQueue
                              ): IGameCards = {
    val (newFieldCards, updatedHand) = defenderHand.splitAtEnd(4)
    val (goalkeeper, defendersFlat) = extractGoalkeeper(newFieldCards)

    val defenders: List[Option[ICard]] =
      defendersFlat.map(Some(_)).padTo(3, None)

    gameCards
      .newPlayerGoalkeeper(defender, Some(goalkeeper))
      .newPlayerDefenders(defender, defenders)
      .newPlayerHand(defender, updatedHand)
  }

  private def refillPartial(
                             gameCards: IGameCards,
                             defender: IPlayer,
                             defenderHand: IHandCardsQueue,
                             defenderField: List[Option[ICard]],
                             goalkeeperOpt: Option[ICard]
                           ): IGameCards = {
    val neededSlots = defenderField.count(_.isEmpty)

    val (newCards, updatedHand) =
      if (neededSlots > 0) defenderHand.splitAtEnd(neededSlots)
      else (Nil, defenderHand)

    val cardIterator = newCards.iterator
    val updatedDefenders: List[Option[ICard]] = defenderField.map {
      case None => if (cardIterator.hasNext) Some(cardIterator.next()) else None
      case some => some
    }

    val (goalkeeper, updatedDefendersWithGoalieBack): (ICard, List[ICard]) =
      adjustGoalkeeper(updatedDefenders, goalkeeperOpt)

    val replacedCards = updatedDefendersWithGoalieBack.iterator
    val adjustedDefenders: List[Option[ICard]] = updatedDefenders.map {
      case Some(_) => if (replacedCards.hasNext) Some(replacedCards.next()) else None
      case None => if (replacedCards.hasNext) Some(replacedCards.next()) else None
    }

    gameCards
      .newPlayerGoalkeeper(defender, Some(goalkeeper))
      .newPlayerDefenders(defender, adjustedDefenders)
      .newPlayerHand(defender, updatedHand)
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

  private def extractGoalkeeper(cards: List[ICard]): (ICard, List[ICard]) = {
    val highestCard = cards.maxBy(_.valueToInt)
    (highestCard, cards.filterNot(_ == highestCard))
  }
}
