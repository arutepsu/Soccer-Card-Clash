package model.playingFiledComponent

import model.cardComponent.{Card, Deck}
import model.playerComponent.Player

import scala.collection.mutable
import scala.collection.mutable
import scala.util.Try
import scala.collection.mutable
import scala.collection.mutable
import util.Observable
import util.UndoManager
import model.cardComponent.{Card, Deck}
import model.playerComponent.Player
import model.cardComponent.Suit

import scala.collection.mutable
import model.cardComponent.Value
import util.Command
class PlayingField(
                    private val player1: Player,
                    private var player1Cards: mutable.Queue[Card],
                    private val player2: Player,
                    private var player2Cards: mutable.Queue[Card]
                  ) extends Observable{
  private var attacker: Player = player1
  private var defender: Player = player2
  def getAttacker: Player = attacker
  def getDefender: Player = defender


  def setAttacker(newAttacker: Player): Unit = {
    attacker = newAttacker
  }

  def setDefender(newDefender: Player): Unit = {
    defender = newDefender
  }

  private var scorePlayer1: Int = 0
  private var scorePlayer2: Int = 0

  def getScorePlayer1: Int = scorePlayer1

  def getScorePlayer2: Int = scorePlayer2

  def setScorePlayer1(score: Int): Unit = {
    scorePlayer1 = score
    notifyObservers()
  }

  def setScorePlayer2(score: Int): Unit = {
    scorePlayer2 = score
    notifyObservers()
  }

  private var player1Field: List[Card] = List()
  private var player2Field: List[Card] = List()

  def setPlayingField(): Unit = {
    println("✅ setPlayingField() was called!") // Debugging line
    refillField(player1, player1Cards, player1Defenders, player1Field, player1Goalkeeper)
    refillField(player2, player2Cards, player2Defenders, player2Field, player2Goalkeeper)
  }

  def playerField(player: Player): List[Card] = {
    if (player == player1) player1Field else player2Field
  }

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

  def setPlayerGoalkeeper(player: Player, goalkeeper: Option[Card]): Unit = {
    if (player == player1) {
      player1Goalkeeper = goalkeeper
    } else if (player == player2) {
      player2Goalkeeper = goalkeeper
    }
  }

  def setGoalkeeperForAttacker(card: Card): Unit = {
    if (getAttacker == player1) {
      player1Goalkeeper = Some(card)
    } else {
      player2Goalkeeper = Some(card)
    }
    notifyObservers() // ✅ Ensure UI refreshes
  }

  def getHand(player: Player): mutable.Queue[Card] = {
    if (player == player1) player1Cards else player2Cards
  }

  def getAttackingCard: Card = getHand(attacker).last

  def getDefenderCard: Card = getHand(defender).last

  def allDefendersBeaten(currentDefender: Player): Boolean = {
    getDefenders(currentDefender).isEmpty
  }
  //-------------------------roles
  def initializeRoles(): Unit = {
    attacker = player1
    defender = player2
  }

  def switchRoles(): Unit = {

    val temp = attacker
    attacker = defender
    defender = temp

    notifyObservers()
  }

  def setRoles(newAttacker: Player, newDefender: Player): Unit
  = {
    attacker = newAttacker
    defender = newDefender
    println(s"Roles set manually. Attacker: ${attacker.name}, Defender: ${defender.name}")
  }

  //-------------------------roles


  //  val goalkeeperCard: Card = getGoalkeeper(attacker) match {
  //    case Some(goalkeeper) => goalkeeper
  //    case None => throw new NoSuchElementException(s"No goalkeeper found for attacker: $attacker")
  //  }
  val goalkeeperCard: Option[Card] =
  if (attacker != null) getGoalkeeper(attacker)
  else None


  // ------------------ Score


  private def scoreGoal(): Unit = {
    if (attacker == player1) scorePlayer1 += 1
    else scorePlayer2 += 1
    notifyObservers()
  }
  // ------------------ Score

  // --- Getter for the attacking card ---



  // Main attack logic that now takes a defenderIndex parameter for position selection

  import scala.util.{Try, Success, Failure}

  def attack(defenderIndex: Int): Boolean = {
    val attackerHand = getHand(attacker)
    val defenderHand = getHand(defender)
    val defenderField = playerDefenders(defender)

    Try {
      // Take the last card from the attacker's hand (queue principle)
      val attackingCard = attackerHand.remove(attackerHand.size - 1)

      if (allDefendersBeaten(defender)) {
        // Attacker attempts to attack the goalkeeper
        val goalkeeper = getGoalkeeper(defender).getOrElse(throw new NoSuchElementException("Goalkeeper not found"))

        println(s"⚔️ Attacking Card: $attackingCard vs Goalkeeper: $goalkeeper")

        val comparisonResult = attackingCard.compare(attackingCard, goalkeeper)

        if (comparisonResult > 0) {
          // ✅ Successful attack on goalkeeper
          println(s"🎯 ${attacker.name} succeeded in attacking the goalkeeper and scored a goal!")

          // Prepend cards to the start of the attacker's hand
          attackerHand.prepend(attackingCard)
          attackerHand.prepend(goalkeeper)
          revertCard(goalkeeper)

          // Remove the goalkeeper after scoring
          setPlayerGoalkeeper(defender, None)
          setPlayerDefenders(defender, List.empty)

          scoreGoal()
          refillDefenderField(defender)
          switchRoles()
          notifyObservers()

          true
        } else {
          // ❌ Failed attack on goalkeeper
          println(s"🛡️ ${defender.name} defended successfully against the goalkeeper attack. Roles are switched.")

          // Prepend cards to the start of the defender's hand
          defenderHand.prepend(attackingCard)
          defenderHand.prepend(goalkeeper)
          revertCard(goalkeeper)
          refillDefenderField(defender)
          switchRoles()
          notifyObservers()
          false
        }
      } else {
        // Regular attack against defender
        val defenderCard = defenderField(defenderIndex)

        println(s"⚔️ Attacking Card: $attackingCard vs Defender Card: $defenderCard")

        val comparisonResult = attackingCard.compare(attackingCard, defenderCard)

        if (comparisonResult == 0) {
          // ✅ "Double Clash" Rule: Both players must play an extra card
          println(s"🔥 Tie! Both players must play another card!")

          if (attackerHand.nonEmpty && defenderHand.nonEmpty) {
            val extraAttackerCard = attackerHand.remove(attackerHand.size - 1)
            val extraDefenderCard = defenderHand.remove(defenderHand.size - 1)

            println(s"⚔️ Tiebreaker! ${attacker.name} plays $extraAttackerCard, ${defender.name} plays $extraDefenderCard")

            val tiebreakerResult = extraAttackerCard.compare(extraAttackerCard, extraDefenderCard)

            if (tiebreakerResult > 0) {
              println(s"🎉 ${attacker.name} wins the tiebreaker and takes all four cards!")
              attackerHand.prepend(attackingCard)
              attackerHand.prepend(defenderCard)
              attackerHand.prepend(extraAttackerCard)
              attackerHand.prepend(extraDefenderCard)
              removeDefenderCard(defender, defenderCard)
            } else {
              println(s"🛡️ ${defender.name} wins the tiebreaker and takes all four cards!")
              defenderHand.prepend(attackingCard)
              defenderHand.prepend(defenderCard)
              defenderHand.prepend(extraAttackerCard)
              defenderHand.prepend(extraDefenderCard)
              removeDefenderCard(defender, defenderCard) //added not testet
            }
          } else {
            // Not enough cards → Switch roles
            println(s"⏳ Not enough cards for a tiebreaker! Switching roles...")
            switchRoles()
          }
          notifyObservers()
          false
        } else if (comparisonResult > 0) {
          // ✅ Normal attack success
          println(s"🎯 ${attacker.name} succeeded in the attack!")

          val boostedValue = defenderCard.additionalValue // ✅ Store the boost before moving

          // ✅ Remove the defender's card from the field
          removeDefenderCard(defender, defenderCard)

          // ✅ Only reset if this card is NOT moving to attacker's hand
          if (!attackerHand.contains(defenderCard)) {
            revertCard(defenderCard)
          }

          // ✅ Modify `defenderCard` directly instead of using `.copy()`
          defenderCard.additionalValue = boostedValue
          defenderCard.wasBoosted = true

          // ✅ Move defender's card to attacker's hand
          attackerHand.prepend(attackingCard)
          attackerHand.prepend(defenderCard) // ✅ Keeps the same object reference

          true
        } else {
          // ❌ Normal attack failure
          println(s"🛡️ ${defender.name} defended successfully. Roles are switched.")

          // Prepend cards to the start of the defender's hand
          defenderHand.prepend(attackingCard)
          defenderHand.prepend(defenderCard)
          revertCard(defenderCard)
          removeDefenderCard(defender, defenderCard)
          refillDefenderField(defender)
          switchRoles()
          notifyObservers()
          false
        }
      }
    } match {
      case Success(result) => result
      case Failure(exception) =>
        println(s"❌ An error occurred during the attack: ${exception.getMessage}")
        false
    }
  }


  def refillDefenderField(defender: Player): Unit = {
    var defenderField = playerDefenders(defender) // Current defenders on the field
    var goalkeeper = getGoalkeeper(defender) // Current goalkeeper
    val defenderHand = getHand(defender) // Cards remaining in defender's hand

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


  private def removeDefenderCard(currentDefender: Player, defenderCard: Card): Unit = {
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

    // Determine the number of cards to draw based on the current defenders and goalkeeper
    val (defenderCount, goalkeeperCount) = (defenders.size, if (goalkeeper.isDefined) 1 else 0)

    // Initialize variables for new field and goalkeeper
    var newField: List[Card] = List()
    var newGoalkeeper: Option[Card] = None

    (defenderCount, goalkeeperCount) match {
      case (0, 0) =>
//        println("🚀 Case (0,0): Drawing 4 cards")
        newField = hand.takeRight(4).toList
        hand.dropRightInPlace(4)
//        println(s"🃏 Drawn cards: $newField")
//        newGoalkeeper = newField.maxByOption(_.value)
//        println(s"🃏 Available candidates for goalkeeper: " +
//          newField.map(c => s"${c.valueToString(c.value)} of ${Suit.suitToString(c.suit)} (Value: ${c.valueToInt})").mkString(", ")
//        )

        newGoalkeeper = newField.maxByOption(card => card.valueToInt)

//        println(s"🧤 Corrected goalkeeper selection: $newGoalkeeper")


      case (1, _) =>
//        println("🚀 Case (1,X): Drawing 2 cards")
        newField = hand.takeRight(2).toList
        hand.dropRightInPlace(2)
//        println(s"🃏 Drawn cards: $newField")

      case (2, _) =>
//        println("🚀 Case (2,X): Drawing 1 card")
        newField = hand.takeRight(1).toList
        hand.dropRightInPlace(1)
//        println(s"🃏 Drawn card: $newField")

      case _ =>
//        println("✅ Case (3,X): All defenders present, no new cards drawn.")
        return
    }


    // Update player's field, goalkeeper, and defenders
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



  def doubleAtack(defenderIndex: Int): Boolean = {
    val attackerHand = getHand(attacker)
    val defenderHand = getHand(defender)
    val defenderField = playerDefenders(defender)

    println(s"Executing attack - Attacker: ${attacker.name}, Defender: ${defender.name}")
    println(s"Attacker hand before attack: ${attackerHand.mkString(", ")}")
    println(s"Defender hand before attack: ${defenderHand.mkString(", ")}")
    println(s"Defender field: ${defenderField.mkString(", ")}")

    Try {
      if (attackerHand.size < 2) {
        println("Invalid attack: Not enough cards or invalid defender index.")
        throw new IllegalAccessException()
      }

      val attackingCard1 = attackerHand.remove(attackerHand.size - 1)
      val attackingCard2 = attackerHand.remove(attackerHand.size - 1)
      val attackValue = attackingCard1.valueToInt + attackingCard2.valueToInt

      if (allDefendersBeaten(defender)) {
        val goalkeeper = getGoalkeeper(defender).getOrElse(throw new NoSuchElementException("Goalkeeper not found"))

        println(s"⚔️ Attacking Cards: $attackingCard1, $attackingCard2 vs Goalkeeper: $goalkeeper")
        val goalkeeperValue = goalkeeper.valueToInt

        if (attackValue > goalkeeperValue) {
          println("Attacker wins! Scores against the goalkeeper!")
          attackerHand.prepend(attackingCard1)
          attackerHand.prepend(attackingCard2)
          if (goalkeeper.wasBoosted) {
            revertCard(goalkeeper)
          }
          attackerHand.prepend(goalkeeper)

          // Remove the goalkeeper after scoring
          setPlayerGoalkeeper(defender, None)
          setPlayerDefenders(defender, List.empty)

          scoreGoal()
          refillDefenderField(defender)
          switchRoles()
          notifyObservers()
          println(s"Updated Attacker hand: ${attackerHand.mkString(", ")}")
          true
        } else {
          println("Goalkeeper saves! Defender wins!")
          defenderHand.prepend(attackingCard1)
          defenderHand.prepend(attackingCard2)
          if (goalkeeper.wasBoosted) {
            revertCard(goalkeeper)
          }
          defenderHand.prepend(goalkeeper)
          refillDefenderField(defender)
          switchRoles()
          notifyObservers()
          println(s"Updated Defender hand: ${defenderHand.mkString(", ")}")
          false
        }
      } else {
        val defenderCard = defenderField(defenderIndex)
        val defenseValue = defenderCard.valueToInt

        println(s"Attacking Cards: $attackingCard1, $attackingCard2 vs Defender: $defenderCard")

        if (attackValue > defenseValue) {
          println("Attacker wins! Takes all three cards back into hand.")
          attackerHand.prepend(attackingCard1)
          attackerHand.prepend(attackingCard2)
          if (defenderCard.wasBoosted) {
            revertCard(defenderCard)
          }
          attackerHand.prepend(defenderCard)
          removeDefenderCard(defender, defenderCard)
          println(s"Updated Attacker hand: ${attackerHand.mkString(", ")}")
          true
        } else {
          println("Defender wins! Takes all three cards.")
          defenderHand.prepend(attackingCard1)
          defenderHand.prepend(attackingCard2)
          if (defenderCard.wasBoosted) {
            revertCard(defenderCard)
          }
          defenderHand.prepend(defenderCard)

          removeDefenderCard(defender, defenderCard)
          refillDefenderField(defender)
          switchRoles()
          notifyObservers()
          println(s"Updated Defender hand: ${defenderHand.mkString(", ")}")
          false
        }
      }
    }.getOrElse {
      println("Error during attack execution.")
      false
    }
  }
  //-----------bosoting
  def chooseBoostCardDefender(index: Int): Unit = {
    val attackersDefenderField = if (attacker == player1) player1Defenders else player2Defenders
    if (index < 0 || index >= attackersDefenderField.size) {
      println("Invalid defender index for boosting.")
      return
    }

    val originalCard = attackersDefenderField(index)
    println(s"Original Card Before Boosting: $originalCard") // ✅ Correctly prints before boosting

    // ✅ Ensure wasBoosted is checked correctly before generating a new boost
    if (originalCard.wasBoosted) {
      println(s"⚠️ Boost prevented! ${originalCard} has already been boosted once.")
      return
    }

    val boostValue = originalCard.getBoostingPolicies
    originalCard.setAdditionalValue(boostValue) // ✅ Modify the card directly

    println(s"Boosted Defender Card: ${attackersDefenderField(index)} (Boosted by: $boostValue)")

    notifyObservers()
  }

  def chooseBoostCardGoalkeeper(): Unit = {
    val attackersGoalkeeperOpt = getGoalkeeper(attacker) // ✅ Ensure goalkeeper is fetched as Option[Card]

    attackersGoalkeeperOpt match {
      case Some(attackersGoalkeeper) =>
//        if (attackersGoalkeeper.wasBoosted) { // ✅ Prevent multiple boosts
//          println(s"⚠️ Boost prevented! ${attackersGoalkeeper} has already been boosted once.")
//          return
//        }
        val boostValue = attackersGoalkeeper.getBoostingPolicies
        val boostedCard = attackersGoalkeeper.setAdditionalValue(boostValue) // ✅ Boosts goalkeeper

        setGoalkeeperForAttacker(boostedCard) // ✅ Updates the playing field

        println(s"Boosted Goalkeeper Card: $attackersGoalkeeper -> $boostedCard (Boosted by: $boostValue)")
        notifyObservers()

      case None =>
        println("⚠️ No goalkeeper available to boost!")
    }

  }

  def revertCard(card: Card): Card = {
    val revertedCard = card.revertAdditionalValue()
//
//    // ✅ If the reverted card is in the attacker's or defender's field, update it
    val attackerField = getDefenders(attacker)
    val defenderField = getDefenders(defender)

    val updatedAttackerField = attackerField.map(c => if (c == card) revertedCard else c)
    val updatedDefenderField = defenderField.map(c => if (c == card) revertedCard else c)

    setPlayerDefenders(attacker, updatedAttackerField)
    setPlayerDefenders(defender, updatedDefenderField)

    notifyObservers() // ✅ Notify after updating game state
    revertedCard
  }

//  def setGoalkeeperForAttacker(card: Card): Unit = {
//    if (attacker == player1) {
//      player1Goalkeeper = Some(card)
//    } else {
//      player2Goalkeeper = Some(card)
//    }
//    notifyObservers() // ✅ Ensure UI refreshes
//  }

//-----------bosoting
  def swapAttacker(index: Int): Unit = {
    val attackerHand = getHand(attacker) // ✅ Get attacker's hand

    if (index < 0 || index >= attackerHand.size) { // ✅ Validate index
      println(s"⚠️ Invalid swap index: $index. No swap performed.")
      return
    }

    if (attackerHand.size < 2) { // ✅ Ensure at least two cards exist
      println(s"⚠️ Not enough cards to swap in hand. No swap performed.")
      return
    }

    val lastIndex = attackerHand.size - 1 // ✅ Get last card index
    val chosenCard = attackerHand(index) // ✅ Get chosen card
    val lastCard = attackerHand(lastIndex) // ✅ Get last card

    // ✅ Swap the two cards in the attacker's hand
    attackerHand.update(index, lastCard)
    attackerHand.update(lastIndex, chosenCard)

    println(s"🔄 Swapped Attacker's Cards: ${chosenCard} <--> ${lastCard}")

    notifyObservers() // ✅ Update UI and notify changes
  }
}