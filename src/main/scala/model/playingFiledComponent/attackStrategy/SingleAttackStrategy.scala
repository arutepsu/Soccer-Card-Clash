package model.playingFiledComponent.attackStrategy

import model.playingFiledComponent.PlayingField
import model.playingFiledComponent.attackStrategy.AttackStrategy

import scala.util.{Failure, Success, Try}

class SingleAttackStrategy extends AttackStrategy{
  override def execute(playingField: PlayingField, defenderIndex: Int): Boolean = {
    val roles = playingField.roles
    val fieldState = playingField.fieldState
    val boostManager = playingField.boostManager
    val scores = playingField.scores

    val attacker = roles.attacker
    val defender = roles.defender
    val attackerHand = playingField.getHand(attacker)
    val defenderHand = playingField.getHand(defender)

    Try {
      val attackingCard = attackerHand.remove(attackerHand.size - 1)

      if (fieldState.allDefendersBeaten(defender)) { // ✅ Check defenders from FieldState
        val goalkeeper = fieldState.getPlayerGoalkeeper(defender).getOrElse(
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
}
