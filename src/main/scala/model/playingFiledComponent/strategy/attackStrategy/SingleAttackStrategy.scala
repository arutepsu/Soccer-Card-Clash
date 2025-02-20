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

      if (fieldState.allDefendersBeaten(defender)) {
        val goalkeeper = fieldState.getPlayerGoalkeeper(defender).getOrElse(
          throw new NoSuchElementException("Goalkeeper not found")
        )


        val comparisonResult = attackingCard.compare(goalkeeper)

        if (comparisonResult > 0) {

          attackerHand.addCard(attackingCard)
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

        val comparisonResult = attackingCard.compare(defenderCard)

        if (comparisonResult == 0) {

          if (attackerHand.nonEmpty && defenderHand.nonEmpty) {
            val extraAttackerCard = attackerHand.removeLastCard()
            val extraDefenderCard = defenderHand.removeLastCard()

            val tiebreakerResult = extraAttackerCard.compare(extraDefenderCard)

            if (tiebreakerResult > 0) {
              attackerHand.addCard(attackingCard)
              attackerHand.addCard(boostManager.revertCard(defenderCard))
              attackerHand.addCard(extraAttackerCard)
              attackerHand.addCard(extraDefenderCard)
              fieldState.removeDefenderCard(defender, defenderCard)
            } else {
              defenderHand.addCard(attackingCard)
              defenderHand.addCard(boostManager.revertCard(defenderCard))
              defenderHand.addCard(extraAttackerCard)
              defenderHand.addCard(extraDefenderCard)
              fieldState.removeDefenderCard(defender, defenderCard)
            }
          } else {
            roles.switchRoles()
          }
          playingField.notifyObservers()
          false
        } else if (comparisonResult > 0) {
          attackerHand.addCard(attackingCard)
          attackerHand.addCard(boostManager.revertCard(defenderCard))
          fieldState.removeDefenderCard(defender, defenderCard)
          playingField.notifyObservers()
          true
        } else {
          defenderHand.addCard(attackingCard)
          defenderHand.addCard(boostManager.revertCard(defenderCard))
          fieldState.removeDefenderCard(defender, defenderCard)
          fieldState.refillDefenderField(defender)
          roles.switchRoles()
          playingField.notifyObservers()
          false
        }
      }
    } match {
      case Success(result) => result
      case Failure(exception) =>
        false
    }
  }
}