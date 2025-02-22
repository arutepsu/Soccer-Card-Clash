package model.playingFiledComponent.strategy.refillStrategy.base
import model.cardComponent.ICard
import model.playerComponent.IPlayer
import model.playingFiledComponent.manager.base.DataManager
import model.playingFiledComponent.strategy.refillStrategy.IRefillStrategy
import model.playingFiledComponent.dataStructure.IHandCardsQueue
import scala.collection.mutable

class RefillField {

  def refill(fieldState: DataManager, player: IPlayer, hand: mutable.Queue[ICard]): Unit = {
    val defenders = fieldState.getPlayerDefenders(player)
    val goalkeeper = fieldState.getPlayerGoalkeeper(player)

    val (defenderCount, goalkeeperCount) = (defenders.size, if (goalkeeper.isDefined) 1 else 0)

    val newField = determineFieldCards(hand, defenderCount, goalkeeperCount)
    if (newField.isEmpty) return

    val newGoalkeeper = goalkeeper.orElse(newField.maxByOption(_.valueToInt))
    val newDefenders = newField.filterNot(_ == newGoalkeeper.getOrElse(newField.head))

    updateFieldState(fieldState, player, newField, newGoalkeeper, newDefenders)
  }

  private def determineFieldCards(hand: mutable.Queue[ICard], defenderCount: Int, goalkeeperCount: Int): List[ICard] = {
    val cardsNeeded = (defenderCount, goalkeeperCount) match {
      case (0, 0) => 4
      case (1, _) => 2
      case (2, _) => 1
      case _      => 0
    }

    if (cardsNeeded > 0) {
      val fieldCards = hand.takeRight(cardsNeeded).toList
      hand.dropRightInPlace(cardsNeeded)
      fieldCards
    } else {
      List()
    }
  }

  private def updateFieldState(
                                fieldState: DataManager,
                                player: IPlayer,
                                field: List[ICard],
                                goalkeeper: Option[ICard],
                                defenders: List[ICard]): Unit = {
    fieldState.setPlayerField(player, field)
    fieldState.setPlayerGoalkeeper(player, goalkeeper)
    fieldState.setPlayerDefenders(player, defenders)
  }
}
