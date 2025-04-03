package de.htwg.se.soccercardclash.model.gameComponent.playingFiledComponent.strategy.refillStrategy.base

import de.htwg.se.soccercardclash.model.cardComponent.ICard
import de.htwg.se.soccercardclash.model.playerComponent.IPlayer
import de.htwg.se.soccercardclash.model.gameComponent.playingFiledComponent.manager.IDataManager
import de.htwg.se.soccercardclash.model.gameComponent.playingFiledComponent.strategy.refillStrategy.IRefillStrategy
import de.htwg.se.soccercardclash.model.cardComponent.dataStructure.IHandCardsQueue

class RefillDefenderField {

  def refill(fieldState: IDataManager, defender: IPlayer): IHandCardsQueue = {
    val defenderHand = fieldState.getPlayerHand(defender)
    val defenders = fieldState.getPlayerDefenders(defender)
    val goalkeeper = fieldState.getPlayerGoalkeeper(defender)

    if (defenders.isEmpty && goalkeeper.isEmpty) {
      refillCompletely(fieldState, defender, defenderHand)
    } else if (defenders.size < 3) {
      refillPartial(fieldState, defender, defenderHand, defenders, goalkeeper)
    } else {
      // No refill needed
      defenderHand
    }
  }



  private def refillCompletely(
                                fieldState: IDataManager,
                                defender: IPlayer,
                                defenderHand: IHandCardsQueue
                              ): IHandCardsQueue = {
    val (newFieldCards, updatedHand) = defenderHand.splitAtEnd(4)
    val (goalkeeper, defenders) = extractGoalkeeper(newFieldCards)

    fieldState.setPlayerGoalkeeper(defender, Some(goalkeeper))
    fieldState.setPlayerDefenders(defender, defenders)

    updatedHand
  }

  private def refillPartial(
                             fieldState: IDataManager,
                             defender: IPlayer,
                             defenderHand: IHandCardsQueue,
                             defenderField: List[ICard],
                             goalkeeperOpt: Option[ICard]
                           ): IHandCardsQueue = {
    val neededDefenders = 3 - defenderField.size

    val (additionalCards, updatedHand) =
      if (neededDefenders > 0)
        defenderHand.splitAtEnd(neededDefenders)
      else
        (Nil, defenderHand)

    val updatedDefenders = defenderField ++ additionalCards
    val (goalkeeper, defenders) = adjustGoalkeeper(updatedDefenders, goalkeeperOpt)

    fieldState.setPlayerGoalkeeper(defender, Some(goalkeeper))
    fieldState.setPlayerDefenders(defender, defenders)

    updatedHand
  }

  private def extractGoalkeeper(cards: List[ICard]): (ICard, List[ICard]) = {
    val highestCard = cards.maxBy(_.valueToInt)
    (highestCard, cards.filterNot(_ == highestCard))
  }

  private def adjustGoalkeeper(updatedDefenders: List[ICard], goalkeeperOpt: Option[ICard]): (ICard, List[ICard]) = {
    goalkeeperOpt match {
      case Some(goalkeeper) =>
        val highestDefender = updatedDefenders.maxBy(_.valueToInt)
        if (highestDefender.valueToInt > goalkeeper.valueToInt) {
          (highestDefender, updatedDefenders.filterNot(_ == highestDefender) :+ goalkeeper)
        } else {
          (goalkeeper, updatedDefenders)
        }
      case None =>
        extractGoalkeeper(updatedDefenders)
    }
  }
}
