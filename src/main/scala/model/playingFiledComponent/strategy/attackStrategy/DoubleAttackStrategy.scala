package model.playingFiledComponent.strategy.attackStrategy

import model.playingFiledComponent.PlayingField
import model.playingFiledComponent.strategy.attackStrategy.AttackStrategy

import scala.util.{Failure, Success, Try}
class DoubleAttackStrategy extends AttackStrategy {

   override def execute(playingField: PlayingField, defenderIndex: Int): Boolean = {
      val roles = playingField.roles
      val fieldState = playingField.fieldState
      val boostManager = playingField.boostManager
      val scores = playingField.scores

      val attacker = roles.attacker
      val defender = roles.defender
      val attackerHand = playingField.fieldState.getPlayerHand(attacker)
      val defenderHand = playingField.fieldState.getPlayerHand(defender)

      println(s"Executing attack - Attacker: ${attacker.name}, Defender: ${defender.name}")
      println(s"Attacker hand before attack: ${attackerHand.mkString(", ")}")
      println(s"Defender hand before attack: ${defenderHand.mkString(", ")}")
      println(s"Defender field: ${fieldState.getPlayerDefenders(defender).mkString(", ")}")

      Try {
        if (attackerHand.size < 2) {
          println("Invalid attack: Not enough cards or invalid defender index.")
          throw new IllegalAccessException()
        }

        val attackingCard1 = attackerHand.remove(attackerHand.size - 1)
        val attackingCard2 = attackerHand.remove(attackerHand.size - 1)
        val attackValue = attackingCard1.valueToInt + attackingCard2.valueToInt

        if (fieldState.allDefendersBeaten(defender)) {
          val goalkeeper = fieldState.getPlayerGoalkeeper(defender).getOrElse(throw new NoSuchElementException("Goalkeeper not found"))

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
