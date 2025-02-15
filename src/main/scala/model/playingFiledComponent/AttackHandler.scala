package model.playingFiledComponent
import scala.util.{Try, Success, Failure}

class AttackHandler(
                     playingField: PlayingField,
                     roles: PlayerRoles,
                     fieldState: FieldState,
                     boostManager: BoostManager,
                     scores: PlayerScores,
                   ) {

  def executeAttack(defenderIndex: Int): Boolean = {
    val attacker = roles.attacker
    val defender = roles.defender
    val attackerHand = playingField.getHand(attacker)
    val defenderHand = playingField.getHand(defender)

    Try {
      val attackingCard = attackerHand.remove(attackerHand.size - 1)

      if (fieldState.allDefendersBeaten(defender)) { // ✅ Check defenders from FieldState
        val goalkeeper = fieldState.getGoalkeeper(defender).getOrElse(
          throw new NoSuchElementException("Goalkeeper not found")
        )

        println(s"⚔️ Attacking Card: $attackingCard vs Goalkeeper: $goalkeeper")

        val comparisonResult = attackingCard.compare(attackingCard, goalkeeper)

        if (comparisonResult > 0) {
          println(s"🎯 ${attacker.name} scored a goal!")

          attackerHand.prepend(attackingCard)
          attackerHand.prepend(goalkeeper)
          boostManager.revertCard(goalkeeper)

          fieldState.setPlayerGoalkeeper(defender, None)
          fieldState.setPlayerDefenders(defender, List.empty)
          scores.scoreGoal(attacker) // ✅ Update score
          fieldState.refillDefenderField(defender) // ✅ Now handled inside FieldState
          roles.switchRoles()

          playingField.notifyObservers()
          true
        } else {
          println(s"🛡️ ${defender.name} defended successfully. Roles are switched.")

          defenderHand.prepend(attackingCard)
          defenderHand.prepend(goalkeeper)
          boostManager.revertCard(goalkeeper)
          fieldState.refillDefenderField(defender)
          roles.switchRoles()
          playingField.notifyObservers()
          false
        }
      } else {
        val defenderCard = fieldState.getDefenderCard(defender, defenderIndex)

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
              fieldState.removeDefenderCard(defender, defenderCard)
            } else {
              println(s"🛡️ ${defender.name} wins the tiebreaker and takes all four cards!")
              defenderHand.prepend(attackingCard)
              defenderHand.prepend(defenderCard)
              defenderHand.prepend(extraAttackerCard)
              defenderHand.prepend(extraDefenderCard)
              fieldState.removeDefenderCard(defender, defenderCard) //added not testet
            }
          } else {
            // Not enough cards → Switch roles
            println(s"⏳ Not enough cards for a tiebreaker! Switching roles...")
            roles.switchRoles()
          }
          playingField.notifyObservers()
          false
        } else if (comparisonResult > 0) {
          println(s"🎯 ${attacker.name} succeeded in the attack!")
          attackerHand.prepend(attackingCard)
          attackerHand.prepend(defenderCard)
          boostManager.revertCard(defenderCard)
          fieldState.removeDefenderCard(defender, defenderCard) // ✅ Delegate removal
          playingField.notifyObservers()
          true
        } else {
          println(s"🛡️ ${defender.name} defended successfully. Roles are switched.")

          defenderHand.prepend(attackingCard)
          defenderHand.prepend(defenderCard)
          boostManager.revertCard(defenderCard)
          fieldState.removeDefenderCard(defender, defenderCard) // ✅ Remove from FieldState
          fieldState.refillDefenderField(defender) // ✅ Refill via FieldState
          roles.switchRoles()
          playingField.notifyObservers()
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

  def executeDoubleAtack(defenderIndex: Int): Boolean = {
    val attacker = roles.attacker
    val defender = roles.defender
    val attackerHand = playingField.getHand(attacker)
    val defenderHand = playingField.getHand(defender)

    println(s"Executing attack - Attacker: ${attacker.name}, Defender: ${defender.name}")
    println(s"Attacker hand before attack: ${attackerHand.mkString(", ")}")
    println(s"Defender hand before attack: ${defenderHand.mkString(", ")}")
    println(s"Defender field: ${fieldState.getDefenders(defender).mkString(", ")}")

    Try {
      if (attackerHand.size < 2) {
        println("Invalid attack: Not enough cards or invalid defender index.")
        throw new IllegalAccessException()
      }

      val attackingCard1 = attackerHand.remove(attackerHand.size - 1)
      val attackingCard2 = attackerHand.remove(attackerHand.size - 1)
      val attackValue = attackingCard1.valueToInt + attackingCard2.valueToInt

      if (fieldState.allDefendersBeaten(defender)) {
        val goalkeeper = fieldState.getGoalkeeper(defender).getOrElse(throw new NoSuchElementException("Goalkeeper not found"))

        println(s"⚔️ Attacking Cards: $attackingCard1, $attackingCard2 vs Goalkeeper: $goalkeeper")
        val goalkeeperValue = goalkeeper.valueToInt

        if (attackValue > goalkeeperValue) {
          println("Attacker wins! Scores against the goalkeeper!")
          attackerHand.prepend(attackingCard1)
          attackerHand.prepend(attackingCard2)
          if (goalkeeper.wasBoosted) {
            boostManager.revertCard(goalkeeper)
          }
          attackerHand.prepend(goalkeeper)

          // Remove the goalkeeper after scoring
          fieldState.setPlayerGoalkeeper(defender, None)
          fieldState.setPlayerDefenders(defender, List.empty)

          scores.scoreGoal(attacker)
          fieldState.refillDefenderField(defender)
          roles.switchRoles()
          playingField.notifyObservers()
          println(s"Updated Attacker hand: ${attackerHand.mkString(", ")}")
          true
        } else {
          println("Goalkeeper saves! Defender wins!")
          defenderHand.prepend(attackingCard1)
          defenderHand.prepend(attackingCard2)
          if (goalkeeper.wasBoosted) {
            boostManager.revertCard(goalkeeper)
          }
          defenderHand.prepend(goalkeeper)
          fieldState.refillDefenderField(defender)
          roles.switchRoles()
          playingField.notifyObservers()
          println(s"Updated Defender hand: ${defenderHand.mkString(", ")}")
          false
        }
      } else {
        val defenderCard = fieldState.getDefenderCard(defender, defenderIndex)
        val defenseValue = defenderCard.valueToInt

        println(s"Attacking Cards: $attackingCard1, $attackingCard2 vs Defender: $defenderCard")

        if (attackValue > defenseValue) {
          println("Attacker wins! Takes all three cards back into hand.")
          attackerHand.prepend(attackingCard1)
          attackerHand.prepend(attackingCard2)
          if (defenderCard.wasBoosted) {
            boostManager.revertCard(defenderCard)
          }
          attackerHand.prepend(defenderCard)
          fieldState.removeDefenderCard(defender, defenderCard)
          println(s"Updated Attacker hand: ${attackerHand.mkString(", ")}")
          true
        } else {
          println("Defender wins! Takes all three cards.")
          defenderHand.prepend(attackingCard1)
          defenderHand.prepend(attackingCard2)
          if (defenderCard.wasBoosted) {
            boostManager.revertCard(defenderCard)
          }
          defenderHand.prepend(defenderCard)

          fieldState.removeDefenderCard(defender, defenderCard)
          fieldState.refillDefenderField(defender)
          roles.switchRoles()
          playingField.notifyObservers()
          println(s"Updated Defender hand: ${defenderHand.mkString(", ")}")
          false
        }
      }
    }.getOrElse {
      println("Error during attack execution.")
      false
    }
  }
}
