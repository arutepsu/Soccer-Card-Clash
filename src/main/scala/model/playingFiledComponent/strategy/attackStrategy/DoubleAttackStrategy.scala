package model.playingFiledComponent.strategy.attackStrategy

import model.playerComponent.playerAction.PlayerActionPolicies
import model.playingFiledComponent.IPlayingField
import model.playingFiledComponent.strategy.attackStrategy.AttackStrategy
import model.playingFiledComponent.strategy.boostStrategy.BoostManager

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

        val goalkeeperValue = goalkeeper.valueToInt

        if (attackValue > goalkeeperValue) {
          attackerHand.addCard(attackingCard1)
          attackerHand.addCard(attackingCard2)
          attackerHand.addCard(boostManager.revertCard(goalkeeper))
          fieldState.removeDefenderGoalkeeper(defender)
          fieldState.setPlayerGoalkeeper(defender, None)
          fieldState.setPlayerDefenders(defender, List.empty)

          scores.scoreGoal(attacker)
          fieldState.refillDefenderField(defender)
          roles.switchRoles()
          playingField.notifyObservers()
          true
        } else {
          defenderHand.addCard(attackingCard1)
          defenderHand.addCard(attackingCard2)
          defenderHand.addCard(boostManager.revertCard(goalkeeper))
          fieldState.removeDefenderGoalkeeper(defender)
          fieldState.refillDefenderField(defender)
          roles.switchRoles()
          playingField.notifyObservers()
          false
        }
      } else {
        val defenderCard = fieldState.getDefenderCard(defender, defenderIndex)
        val defenseValue = defenderCard.valueToInt


        if (attackValue > defenseValue) {
          attackerHand.addCard(attackingCard1)
          attackerHand.addCard(attackingCard2)
          attackerHand.addCard(boostManager.revertCard(defenderCard))
          fieldState.removeDefenderCard(defender, defenderCard)
          true
        } else {
          defenderHand.addCard(attackingCard1)
          defenderHand.addCard(attackingCard2)
          defenderHand.addCard(boostManager.revertCard(defenderCard))

          fieldState.removeDefenderCard(defender, defenderCard)
          fieldState.refillDefenderField(defender)
          roles.switchRoles()
          playingField.notifyObservers()
          false
        }
      }
    }.getOrElse {
      false
    }
  }
}