package model.playingFiledComponent.strategy.refillStrategy

import model.playerComponent.Player
import model.playingFiledComponent.FieldState
import model.cardComponent.base.Card
import scala.collection.mutable

class StandardRefillStrategy extends RefillStrategy {

  override def refillDefenderField(fieldState: FieldState, defender: Player): Unit = {
    val defenderField = fieldState.getPlayerDefenders(defender)
    val goalkeeper = fieldState.getPlayerGoalkeeper(defender)
    val defenderHand = fieldState.getPlayerHand(defender)
    println(s"Initial state -> Goalkeeper: $goalkeeper, Defender Field: $defenderField, Defender Hand: $defenderHand")

    // Case 1: If no goalkeeper exists and defender field is empty, set up with 4 cards from the hand
    if (goalkeeper.isEmpty && defenderField.isEmpty) {
      // Take the last 4 cards from the hand
      val newFieldCards = defenderHand.takeRight(4).toList
      defenderHand.dropRightInPlace(4) // Remove these cards from the hand

      println(s"Case 1: No goalkeeper and empty field. New field cards taken: $newFieldCards")

      // Find the highest card to set as the goalkeeper
      val highestCard = newFieldCards.maxBy(card => card.valueToInt)
      println(s"Highest card chosen as goalkeeper: $highestCard")

      fieldState.setPlayerGoalkeeper(defender, Some(highestCard))

      // Remaining cards become defenders
      val newDefenders = newFieldCards.filterNot(_ == highestCard)
      println(s"Remaining cards set as defenders: $newDefenders")
      fieldState.setPlayerDefenders(defender, newDefenders)

    } else if (defenderField.size < 3) {
      // Case 2: The defender field has fewer than 3 cards and goalkeeper exists (unsuccessful attack)

      // Calculate how many additional defenders are needed
      val neededDefenders = 3 - defenderField.size
      println(s"Case 2: Existing goalkeeper. Additional defenders needed: $neededDefenders")

      // Take the required number of cards from the end of the hand
      val additionalCards = defenderHand.takeRight(neededDefenders).toList
      defenderHand.dropRightInPlace(neededDefenders)

      println(s"Additional cards taken from hand: $additionalCards")

      // Update defender field with these additional cards
      val updatedDefenders = defenderField ++ additionalCards
      println(s"Updated defender field after adding additional cards: $updatedDefenders")

      // Find the highest card in the updated defender field
      val highestDefenderCard = updatedDefenders.maxBy(card => card.valueToInt)
      println(s"Highest card among defenders: $highestDefenderCard, Current goalkeeper: $goalkeeper")

      // Check if this highest defender card is stronger than the current goalkeeper
      if (highestDefenderCard.valueToInt > goalkeeper.get.valueToInt) {
        // Swap the current goalkeeper with the highest defender card
        val currentGoalkeeperCard = goalkeeper.get // Store the entire goalkeeper card
        println(s"Swapping goalkeeper with higher defender card: $highestDefenderCard")

        // Set the new goalkeeper
        fieldState.setPlayerGoalkeeper(defender, Some(highestDefenderCard))

        // Update defenders, adding the previous goalkeeper back to the field
        fieldState.setPlayerDefenders(defender, updatedDefenders.filterNot(_ == highestDefenderCard) :+ currentGoalkeeperCard)
      } else {
        // No swap needed, just set the updated defenders
        println("No swap needed; keeping the current goalkeeper")
        fieldState.setPlayerDefenders(defender, updatedDefenders)
      }

    }

    // Final state after processing
    println(s"Final state -> Goalkeeper: ${fieldState.getPlayerGoalkeeper(defender)}, " +
      s"Defender Field: ${fieldState.getPlayerDefenders(defender)}, Defender Hand: $defenderHand")
  }

  override def refillField(fieldState: FieldState, player: Player, hand: mutable.Queue[Card]): Unit = {
    val defenders = fieldState.getPlayerDefenders(player)
    val goalkeeper = fieldState.getPlayerGoalkeeper(player)

    val (defenderCount, goalkeeperCount) = (defenders.size, if (goalkeeper.isDefined) 1 else 0)

    var newField: List[Card] = List()
    var newGoalkeeper: Option[Card] = None

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
