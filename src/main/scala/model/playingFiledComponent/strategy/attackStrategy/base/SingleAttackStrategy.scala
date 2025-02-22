package model.playingFiledComponent.strategy.attackStrategy.base

import model.playingFiledComponent.IPlayingField
import model.playingFiledComponent.strategy.attackStrategy.IAttackStrategy
import model.playingFiledComponent.strategy.boostStrategy.BoostManager

import scala.util.{Failure, Success, Try}

class SingleAttackStrategy(defenderIndex: Int) extends IAttackStrategy{
  override def execute(playingField: IPlayingField): Boolean = {
    val roles = playingField.getRoles
    val fieldState = playingField.getDataManager
    val boostManager = playingField.getActionManager.getBoostManager
    val revertStrategy = boostManager.getRevertStrategy
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
          attackerHand.addCard(revertStrategy.revertCard(goalkeeper))
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
          defenderHand.addCard(revertStrategy.revertCard(goalkeeper))
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
              attackerHand.addCard(revertStrategy.revertCard(defenderCard))
              attackerHand.addCard(extraAttackerCard)
              attackerHand.addCard(extraDefenderCard)
              fieldState.removeDefenderCard(defender, defenderCard)
            } else {
              defenderHand.addCard(attackingCard)
              defenderHand.addCard(revertStrategy.revertCard(defenderCard))
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
          attackerHand.addCard(revertStrategy.revertCard(defenderCard))
          fieldState.removeDefenderCard(defender, defenderCard)
          playingField.notifyObservers()
          true
        } else {
          defenderHand.addCard(attackingCard)
          defenderHand.addCard(revertStrategy.revertCard(defenderCard))
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