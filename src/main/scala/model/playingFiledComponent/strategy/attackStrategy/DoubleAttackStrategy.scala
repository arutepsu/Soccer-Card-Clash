package model.playingFiledComponent.strategy.attackStrategy

import model.playerComponent.playerAction.PlayerActionPolicies
import model.playingFiledComponent.IPlayingField
import model.playingFiledComponent.strategy.attackStrategy.AttackStrategy

import scala.util.{Failure, Success, Try}
class DoubleAttackStrategy extends AttackStrategy {

  override def execute(playingField: IPlayingField, defenderIndex: Int): Boolean = {
    val roles = playingField.getRoles
    val fieldState = playingField.getDataManager
    val boostManager = playingField.getActionManager.boostManager
    val scores = playingField.getScores

    val attacker = roles.attacker
    val defender = roles.defender
    attacker.performAction(PlayerActionPolicies.DoubleAttack)
    val attackerHand = playingField.getDataManager.getPlayerHand(attacker)
    val defenderHand = playingField.getDataManager.getPlayerHand(defender)
    
    Try {
      if (attackerHand.getHandSize < 2) {
        throw new IllegalAccessException()
      }

      val attackingCard1 = attackerHand.removeLastCard()
      val attackingCard2 = attackerHand.removeLastCard()
      val attackValue = attackingCard1.valueToInt + attackingCard2.valueToInt

      if (fieldState.allDefendersBeaten(defender)) {
        val goalkeeper = fieldState.getPlayerGoalkeeper(defender).getOrElse(throw new NoSuchElementException("Goalkeeper not found"))

        println(s"⚔️ Attacking Cards: $attackingCard1, $attackingCard2 vs Goalkeeper: $goalkeeper")
        val goalkeeperValue = goalkeeper.valueToInt

        if (attackValue > goalkeeperValue) {
          println("Attacker wins! Scores against the goalkeeper!")
          attackerHand.addCard(attackingCard1)
          attackerHand.addCard(attackingCard2)
          attackerHand.addCard(boostManager.revertCard(goalkeeper))
          fieldState.removeDefenderGoalkeeper(defender)
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
          defenderHand.addCard(attackingCard1)
          defenderHand.addCard(attackingCard2)
          defenderHand.addCard(boostManager.revertCard(goalkeeper))
          fieldState.removeDefenderGoalkeeper(defender)
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
          attackerHand.addCard(attackingCard1)
          attackerHand.addCard(attackingCard2)
          attackerHand.addCard(boostManager.revertCard(defenderCard))
          fieldState.removeDefenderCard(defender, defenderCard)
          println(s"Updated Attacker hand: ${attackerHand.mkString(", ")}")
          true
        } else {
          println("Defender wins! Takes all three cards.")
          defenderHand.addCard(attackingCard1)
          defenderHand.addCard(attackingCard2)
          defenderHand.addCard(boostManager.revertCard(defenderCard))

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