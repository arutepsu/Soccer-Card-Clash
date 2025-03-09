package model.playingFiledComponent.strategy.attackStrategy.base

import model.playerComponent.playerAction.PlayerActionPolicies
import model.playingFiledComponent.IPlayingField
import model.playingFiledComponent.strategy.attackStrategy.IAttackStrategy
import model.playingFiledComponent.strategy.boostStrategy.BoostManager
import model.playerComponent.IPlayer
import model.playerComponent.factory.IPlayerFactory
import model.playingFiledComponent.dataStructure.IHandCardsQueue
import model.playingFiledComponent.manager.IDataManager
import model.cardComponent.ICard
import model.playingFiledComponent.strategy.boostStrategy.IRevertStrategy
import model.playingFiledComponent.strategy.scoringStrategy.IPlayerScores
import model.playerComponent.playerRole.IRolesManager

import scala.util.{Failure, Success, Try}

import scala.util.{Try, Success, Failure}

class DoubleAttackStrategy(defenderIndex: Int) extends IAttackStrategy {

  override def execute(playingField: IPlayingField): Boolean = {
    val roles = playingField.getRoles
    val fieldState = playingField.getDataManager
    val boostManager = playingField.getActionManager.getBoostManager
    val revertStrategy = boostManager.getRevertStrategy
    val scores = playingField.getScores

    val attacker = roles.attacker
    val defender = roles.defender
    attacker.performAction(PlayerActionPolicies.DoubleAttack)

    val attackerHand = fieldState.getPlayerHand(attacker)
    val defenderHand = fieldState.getPlayerHand(defender)

    Try {
      if (attackerHand.getHandSize < 2) {
        throw new IllegalStateException("Not enough cards for a double attack.")
      }

      val attackingCard1 = attackerHand.removeLastCard()
      val attackingCard2 = attackerHand.removeLastCard()
      val attackValue = attackingCard1.valueToInt + attackingCard2.valueToInt

      val result = if (fieldState.allDefendersBeaten(defender)) {
        processGoalkeeperAttack(
          attacker,
          defender,
          attackerHand,
          defenderHand,
          attackingCard1,
          attackingCard2,
          attackValue,
          fieldState,
          revertStrategy,
          scores,
          roles)
      } else {
        processDefenderAttack(
          attacker,
          defender,
          attackerHand,
          defenderHand,
          attackingCard1,
          attackingCard2,
          attackValue,
          fieldState,
          revertStrategy,
          roles)
      }

      playingField.notifyObservers()
      result
    } match {
      case Success(result) => result
      case Failure(exception) =>
        println(s"Error during double attack execution: ${exception.getMessage}")
        false
    }
  }

  private def processGoalkeeperAttack(
                                       attacker: IPlayer,
                                       defender: IPlayer,
                                       attackerHand: IHandCardsQueue,
                                       defenderHand: IHandCardsQueue,
                                       attackingCard1: ICard,
                                       attackingCard2: ICard,
                                       attackValue: Int,
                                       fieldState: IDataManager,
                                       revertStrategy: IRevertStrategy,
                                       scores: IPlayerScores,
                                       roles: IRolesManager
                                     ): Boolean = {
    val goalkeeper = fieldState.getPlayerGoalkeeper(defender).getOrElse(
      throw new NoSuchElementException("Goalkeeper not found.")
    )
    val revertedGoalkeeper = revertStrategy.revertCard(goalkeeper)
    val goalkeeperValue = goalkeeper.valueToInt

    if (attackValue > goalkeeperValue) {
      attackerWins(attackerHand, attackingCard1, attackingCard2, revertStrategy.revertCard(goalkeeper))
      fieldState.removeDefenderGoalkeeper(defender)
      fieldState.setPlayerGoalkeeper(defender, None)
      fieldState.setPlayerDefenders(defender, List.empty)

      scores.scoreGoal(attacker)
      fieldState.refillDefenderField(defender)
      roles.switchRoles()
      true
    } else {
      defenderWins(defenderHand, attackingCard1, attackingCard2, revertStrategy.revertCard(goalkeeper))
      fieldState.removeDefenderGoalkeeper(defender)
      fieldState.refillDefenderField(defender)
      roles.switchRoles()
      false
    }
  }

  private def processDefenderAttack(
                                     attacker: IPlayer,
                                     defender: IPlayer,
                                     attackerHand: IHandCardsQueue,
                                     defenderHand: IHandCardsQueue,
                                     attackingCard1: ICard,
                                     attackingCard2: ICard,
                                     attackValue: Int,
                                     fieldState: IDataManager,
                                     revertStrategy: IRevertStrategy,
                                     roles: IRolesManager
                                   ): Boolean = {
    val defenderCard = fieldState.getDefenderCard(defender, defenderIndex)
    val defenseValue = defenderCard.valueToInt
    val revertedCard = revertStrategy.revertCard(defenderCard)
    if (attackValue > defenseValue) {
      attackerWins(attackerHand, attackingCard1, attackingCard2, revertStrategy.revertCard(defenderCard))
      fieldState.removeDefenderCard(defender, defenderCard)
      fieldState.removeDefenderCard(defender, revertedCard)
      true
    } else if (attackValue < defenseValue) {
      defenderWins(defenderHand, attackingCard1, attackingCard2, revertStrategy.revertCard(defenderCard))
      fieldState.removeDefenderCard(defender, defenderCard)
      fieldState.removeDefenderCard(defender, revertedCard)
      fieldState.refillDefenderField(defender)
      roles.switchRoles()
      false
    } else {
      handleTie(
        attacker,
        defender,
        attackerHand,
        defenderHand,
        attackingCard1,
        attackingCard2,
        fieldState,
        revertStrategy,
        roles)
    }
  }

  private def handleTie(
                         attacker: IPlayer,
                         defender: IPlayer,
                         attackerHand: IHandCardsQueue,
                         defenderHand: IHandCardsQueue,
                         attackingCard1: ICard,
                         attackingCard2: ICard,
                         fieldState: IDataManager,
                         revertStrategy: IRevertStrategy,
                         roles: IRolesManager
                       ): Boolean = {
    if (attackerHand.nonEmpty && defenderHand.nonEmpty) {
      val extraAttackerCard = attackerHand.removeLastCard()
      val extraDefenderCard = defenderHand.removeLastCard()
      val revertedExtraAttackerCard = revertStrategy.revertCard(extraAttackerCard)
      val revertedExtraDefenderCard = revertStrategy.revertCard(extraDefenderCard)

      val defenderCard = fieldState.getDefenderCard(defender, defenderIndex) // ✅ Retrieve defender card
      val revertedDefenderCard = revertStrategy.revertCard(defenderCard) // ✅
      val tiebreakerResult = extraAttackerCard.compare(extraDefenderCard)

      if (tiebreakerResult > 0) {
        attackerWins(
          attackerHand,
          attackingCard1,
          attackingCard2,
          revertStrategy.revertCard(extraAttackerCard),
          revertStrategy.revertCard(extraDefenderCard))
        fieldState.removeDefenderCard(defender, fieldState.getDefenderCard(defender, defenderIndex))
        fieldState.removeDefenderCard(defender, revertedDefenderCard)
        true
      } else {
        defenderWins(
          defenderHand,
          attackingCard1,
          attackingCard2,
          revertStrategy.revertCard(extraAttackerCard),
          revertStrategy.revertCard(extraDefenderCard))
        fieldState.removeDefenderCard(defender, fieldState.getDefenderCard(defender, defenderIndex))
        fieldState.removeDefenderCard(defender, revertedDefenderCard)
        fieldState.refillDefenderField(defender)
        roles.switchRoles()
        false
      }
    } else {
      roles.switchRoles()
      false
    }
  }

  private def attackerWins(hand: IHandCardsQueue, cards: ICard*): Unit = {
    cards.foreach(hand.addCard)
  }

  private def defenderWins(hand: IHandCardsQueue, cards: ICard*): Unit = {
    cards.foreach(hand.addCard)
  }
}