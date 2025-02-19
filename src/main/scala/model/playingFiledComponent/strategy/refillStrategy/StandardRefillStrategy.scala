package model.playingFiledComponent.strategy.refillStrategy

import model.cardComponent.ICard
import model.cardComponent.base.Card
import model.playerComponent.IPlayer
import model.playingFiledComponent.FieldState

import scala.collection.mutable

class StandardRefillStrategy extends RefillStrategy {

  override def refillDefenderField(fieldState: FieldState, defender: IPlayer): Unit = {
    val defenderField = fieldState.getPlayerDefenders(defender)
    val goalkeeper = fieldState.getPlayerGoalkeeper(defender)
    val defenderHand = fieldState.getPlayerHand(defender)
    
    if (goalkeeper.isEmpty && defenderField.isEmpty) {
      
      val newFieldCards = defenderHand.takeRight(4).toList
      defenderHand.dropRightInPlace(4)
      val highestCard = newFieldCards.maxBy(card => card.valueToInt)
      fieldState.setPlayerGoalkeeper(defender, Some(highestCard))
      val newDefenders = newFieldCards.filterNot(_ == highestCard)
      fieldState.setPlayerDefenders(defender, newDefenders)
      
    } else if (defenderField.size < 3) {
      
      val neededDefenders = 3 - defenderField.size
      val additionalCards = defenderHand.takeRight(neededDefenders).toList
      defenderHand.dropRightInPlace(neededDefenders)
      val updatedDefenders = defenderField ++ additionalCards
      val highestDefenderCard = updatedDefenders.maxBy(card => card.valueToInt)
      
      if (highestDefenderCard.valueToInt > goalkeeper.get.valueToInt) {
        
        val currentGoalkeeperCard = goalkeeper.get
        fieldState.setPlayerGoalkeeper(defender, Some(highestDefenderCard))
        fieldState.setPlayerDefenders(defender, updatedDefenders.filterNot(_ == highestDefenderCard) :+ currentGoalkeeperCard)
        
      } else {
        
        fieldState.setPlayerDefenders(defender, updatedDefenders)
        
      }
    }
  }

  override def refillField(fieldState: FieldState, player: IPlayer, hand: mutable.Queue[ICard]): Unit = {
    val defenders = fieldState.getPlayerDefenders(player)
    val goalkeeper = fieldState.getPlayerGoalkeeper(player)

    val (defenderCount, goalkeeperCount) = (defenders.size, if (goalkeeper.isDefined) 1 else 0)

    var newField: List[ICard] = List()
    var newGoalkeeper: Option[ICard] = None

    (defenderCount, goalkeeperCount) match {
      case (0, 0) =>
        newField = hand.takeRight(4).toList
        hand.dropRightInPlace(4)
        newGoalkeeper = newField.maxByOption(card => card.valueToInt)
      case (1, _) =>
        newField = hand.takeRight(2).toList
        hand.dropRightInPlace(2)
      case (2, _) =>
        newField = hand.takeRight(1).toList
        hand.dropRightInPlace(1)
      case _ =>
        return
    }

    if (player == fieldState.getPlayer1) {
      fieldState.setPlayerField(fieldState.getPlayer1, newField)
      fieldState.setPlayerGoalkeeper(fieldState.getPlayer1, newGoalkeeper.orElse(newField.maxByOption(_.value)))
      fieldState.setPlayerDefenders(fieldState.getPlayer1, newField.filterNot(_ == fieldState.getPlayerGoalkeeper(fieldState.getPlayer1).getOrElse(newField.head)))

    } else {
      fieldState.setPlayerField(fieldState.getPlayer2, newField)
      fieldState.setPlayerGoalkeeper(fieldState.getPlayer2, newGoalkeeper.orElse(newField.maxByOption(_.value)))
      fieldState.setPlayerDefenders(fieldState.getPlayer2, newField.filterNot(_ == fieldState.getPlayerGoalkeeper(fieldState.getPlayer2).getOrElse(newField.head)))
    }
  }
}
