package model.playingFiledComponent.strategy.attackStrategy

import model.playingFiledComponent.IPlayingField
import model.playingFiledComponent.strategy.attackStrategy.AttackStrategy

import scala.util.{Failure, Success, Try}

class SingleAttackStrategy extends AttackStrategy{
  override def execute(playingField: IPlayingField, defenderIndex: Int): Boolean = {
    val roles = playingField.getRoles
    val fieldState = playingField.getDataManager
    val boostManager = playingField.getActionManager.boostManager
    val scores = playingField.getScores

    val attacker = roles.attacker
    val defender = roles.defender
    val attackerHand = playingField.getDataManager.getPlayerHand(attacker)
    val defenderHand = playingField.getDataManager.getPlayerHand(defender)

    Try {
      val attackingCard = attackerHand.removeLastCard()

      if (fieldState.allDefendersBeaten(defender)) { // ✅ Check defenders from FieldState
        val goalkeeper = fieldState.getPlayerGoalkeeper(defender).getOrElse(
          throw new NoSuchElementException("Goalkeeper not found")
        )

        println(s"⚔️ Attacking Card: $attackingCard vs Goalkeeper: $goalkeeper")

        val comparisonResult = attackingCard.compare(goalkeeper)

        if (comparisonResult > 0) {
          println(s"🎯 ${attacker.name} scored a goal!")

          attackerHand.addCard(attackingCard)
          attackerHand.addCard(boostManager.revertCard(goalkeeper))
          fieldState.removeDefenderGoalkeeper(defender)
          fieldState.setPlayerGoalkeeper(defender, None)
          fieldState.setPlayerDefenders(defender, List.empty)
          scores.scoreGoal(attacker) // ✅ Update score
          fieldState.refillDefenderField(defender) // ✅ Now handled inside FieldState
          roles.switchRoles()

          playingField.notifyObservers()
          true
        } else {
          println(s"🛡️ ${defender.name} defended successfully. Roles are switched.")

          defenderHand.addCard(attackingCard)
          defenderHand.addCard(boostManager.revertCard(goalkeeper))
          fieldState.removeDefenderGoalkeeper(defender)
          fieldState.refillDefenderField(defender)
          roles.switchRoles()
          playingField.notifyObservers()
          false
        }
      } else {
        val defenderCard = fieldState.getDefenderCard(defender, defenderIndex)

        println(s"⚔️ Attacking Card: $attackingCard vs Defender Card: $defenderCard")

        val comparisonResult = attackingCard.compare(defenderCard)

        if (comparisonResult == 0) {
          // ✅ "Double Clash" Rule: Both players must play an extra card
          println(s"🔥 Tie! Both players must play another card!")

          if (attackerHand.nonEmpty && defenderHand.nonEmpty) {
            val extraAttackerCard = attackerHand.removeLastCard()
            val extraDefenderCard = defenderHand.removeLastCard()

            println(s"⚔️ Tiebreaker! ${attacker.name} plays $extraAttackerCard, ${defender.name} plays $extraDefenderCard")

            val tiebreakerResult = extraAttackerCard.compare(extraDefenderCard)

            if (tiebreakerResult > 0) {
              println(s"🎉 ${attacker.name} wins the tiebreaker and takes all four cards!")
              attackerHand.addCard(attackingCard)
              attackerHand.addCard(boostManager.revertCard(defenderCard))
              attackerHand.addCard(extraAttackerCard)
              attackerHand.addCard(extraDefenderCard)
              fieldState.removeDefenderCard(defender, defenderCard)
            } else {
              println(s"🛡️ ${defender.name} wins the tiebreaker and takes all four cards!")
              defenderHand.addCard(attackingCard)
              defenderHand.addCard(boostManager.revertCard(defenderCard))
              defenderHand.addCard(extraAttackerCard)
              defenderHand.addCard(extraDefenderCard)
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
          attackerHand.addCard(attackingCard)
          attackerHand.addCard(boostManager.revertCard(defenderCard))
          fieldState.removeDefenderCard(defender, defenderCard) // ✅ Delegate removal
          playingField.notifyObservers()
          true
        } else {
          println(s"🛡️ ${defender.name} defended successfully. Roles are switched.")

          defenderHand.addCard(attackingCard)
          defenderHand.addCard(boostManager.revertCard(defenderCard))
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