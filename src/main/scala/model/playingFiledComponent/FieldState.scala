package model.playingFiledComponent
import scala.collection.mutable
import util.Observable
import util.UndoManager
import model.cardComponent.{Card, Deck}
import model.playerComponent.Player
import model.cardComponent.Suit

class FieldState(player1: Player, player2: Player, playingField: PlayingField) {
  private var player1Field: List[Card] = List()
  private var player2Field: List[Card] = List()
  private var player1Goalkeeper: Option[Card] = None
  private var player1Defenders: List[Card] = List()

  private var player2Goalkeeper: Option[Card] = None
  private var player2Defenders: List[Card] = List()


  def playerDefenders(player: Player): List[Card] = {
    if (player == player1) player1Defenders else player2Defenders
  }

  def getGoalkeeper(player: Player): Option[Card] = {
    if (player == player1) player1Goalkeeper else player2Goalkeeper
  }

  def getDefenders(player: Player): List[Card] = {
    if (player == player1) player1Defenders else player2Defenders
  }

  def setPlayerDefenders(player: Player, newDefenderField: List[Card]): Unit = {
    if (player == player1) {
      player1Defenders = newDefenderField
    } else if (player == player2) {
      player2Defenders = newDefenderField
    }
  }

  //  def setGoalkeeperForAttacker(card: Card): Unit = {
  //    if (attacker == player1) {
  //      player1Goalkeeper = Some(card)
  //    } else {
  //      player2Goalkeeper = Some(card)
  //    }
  //    notifyObservers() // ✅ Ensure UI refreshes
  //  }
  def setPlayerGoalkeeper(player: Player, goalkeeper: Option[Card]): Unit = {
    if (player == player1) {
      player1Goalkeeper = goalkeeper
    } else if (player == player2) {
      player2Goalkeeper = goalkeeper
    }
  }

  def setGoalkeeperForAttacker(card: Card): Unit = {
    if (playingField.getAttacker == player1) {
      player1Goalkeeper = Some(card)
    } else {
      player2Goalkeeper = Some(card)
    }
    playingField.notifyObservers() // ✅ Ensure UI refreshes
  }
//
//  def getHand(player: Player): mutable.Queue[Card] = {
//    if (player == player1) player1Cards else player2Cards
//  }

  def initializeFields(): Unit = {
    println("✅ setPlayingField() was called!")
    refillField(player1, playingField.getHand(player1), player1Defenders, player1Field, player1Goalkeeper)
    refillField(player2, playingField.getHand(player1), player2Defenders, player2Field, player2Goalkeeper)
  }

  def refillDefenderField(defender: Player): Unit = {
        var defenderField = playerDefenders(playingField.getDefender) // Current defenders on the field
        var goalkeeper = getGoalkeeper(playingField.getDefender) // Current goalkeeper
        val defenderHand = playingField.getHand(playingField.getDefender) // Cards remaining in defender's hand

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

          setPlayerGoalkeeper(defender, Some(highestCard))

          // Remaining cards become defenders
          val newDefenders = newFieldCards.filterNot(_ == highestCard)
          println(s"Remaining cards set as defenders: $newDefenders")
          setPlayerDefenders(defender, newDefenders)

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
            setPlayerGoalkeeper(defender, Some(highestDefenderCard))

            // Update defenders, adding the previous goalkeeper back to the field
            setPlayerDefenders(defender, updatedDefenders.filterNot(_ == highestDefenderCard) :+ currentGoalkeeperCard)
          } else {
            // No swap needed, just set the updated defenders
            println("No swap needed; keeping the current goalkeeper")
            setPlayerDefenders(defender, updatedDefenders)
          }

        }

        // Final state after processing
        println(s"Final state -> Goalkeeper: ${getGoalkeeper(defender)}, Defender Field: ${playerDefenders(defender)}, Defender Hand: $defenderHand")
  }


  def removeDefenderCard(currentDefender: Player, defenderCard: Card): Unit = {
    if (currentDefender == player1) {
      player1Defenders = player1Defenders.filterNot(_ == defenderCard)
    } else {
      player2Defenders = player2Defenders.filterNot(_ == defenderCard)
    }
  }

  private def refillField(
                           player: Player,
                           hand: mutable.Queue[Card],
                           defenders: List[Card],
                           field: List[Card],
                           goalkeeper: Option[Card]
                         ): Unit = {
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

    if (player == player1) {
      player1Field = newField
      player1Goalkeeper = newGoalkeeper.orElse(newField.maxByOption(_.value))
      player1Defenders = newField.filterNot(_ == player1Goalkeeper.getOrElse(newField.head))

    } else {
      player2Field = newField
      player2Goalkeeper = newGoalkeeper.orElse(newField.maxByOption(_.value))
      player2Defenders = newField.filterNot(_ == player2Goalkeeper.getOrElse(newField.head))
    }
  }

  def allDefendersBeaten(currentDefender: Player): Boolean = {
    getDefenders(currentDefender).isEmpty
    }

  def getDefenderCard(player: Player, index: Int): Card = {
    val defenders = if (player == player1) player1Defenders else player2Defenders
    if (index < 0 || index >= defenders.size) throw new IndexOutOfBoundsException("Invalid defender index")
    defenders(index)
  }
}