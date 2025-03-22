package de.htwg.se.soccercardclash.model.playingFiledComponent.strategy.refillStrategy.base

import de.htwg.se.soccercardclash.model.cardComponent.ICard
import de.htwg.se.soccercardclash.model.playerComponent.IPlayer
import de.htwg.se.soccercardclash.model.playingFiledComponent.manager.IDataManager
import de.htwg.se.soccercardclash.model.playingFiledComponent.strategy.refillStrategy.IRefillStrategy
import de.htwg.se.soccercardclash.model.cardComponent.dataStructure.IHandCardsQueue

class RefillDefenderField {

  def refill(fieldState: IDataManager, defender: IPlayer): Unit = {
    val defenderField = fieldState.getPlayerDefenders(defender)
    val goalkeeperOpt = fieldState.getPlayerGoalkeeper(defender)
    val defenderHand = fieldState.getPlayerHand(defender)

    if (goalkeeperOpt.isEmpty && defenderField.isEmpty) {
      refillCompletely(fieldState, defender, defenderHand)
    } else if (defenderField.size < 3) {
      refillPartial(fieldState, defender, defenderHand, defenderField, goalkeeperOpt)
    }
  }

  private def refillCompletely(
                                fieldState: IDataManager,
                                defender: IPlayer,
                                defenderHand: IHandCardsQueue
                              ): Unit = {
    val newFieldCards = defenderHand.takeRight(4).toList
    defenderHand.dropRightInPlace(4)

    val (goalkeeper, defenders) = extractGoalkeeper(newFieldCards)

    fieldState.setPlayerGoalkeeper(defender, Some(goalkeeper))
    fieldState.setPlayerDefenders(defender, defenders)
  }

  private def refillPartial(
                             fieldState: IDataManager,
                             defender: IPlayer,
                             defenderHand: IHandCardsQueue,
                             defenderField: List[ICard],
                             goalkeeperOpt: Option[ICard]
                           ): Unit = {
    val neededDefenders = 3 - defenderField.size
    val additionalCards = defenderHand.takeRight(neededDefenders).toList
    defenderHand.dropRightInPlace(neededDefenders)

    val updatedDefenders = defenderField ++ additionalCards
    val (goalkeeper, defenders) = adjustGoalkeeper(updatedDefenders, goalkeeperOpt)

    fieldState.setPlayerGoalkeeper(defender, Some(goalkeeper))
    fieldState.setPlayerDefenders(defender, defenders)
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
