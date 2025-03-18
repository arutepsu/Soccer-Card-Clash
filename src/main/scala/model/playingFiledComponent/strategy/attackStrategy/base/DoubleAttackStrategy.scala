package model.playingFiledComponent.strategy.attackStrategy.base

import controller.{AttackResultEvent, DoubleComparedCardsEvent, DoubleTieComparisonEvent, Events, NoDoubleAttacksEvent}
import model.playerComponent.playerAction.*
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

//TODO : Implement Goalkeeper tie case
class DoubleAttackStrategy(defenderIndex: Int) extends IAttackStrategy {

  override def execute(playingField: IPlayingField): Boolean = {
    val roles = playingField.getRoles
    val fieldState = playingField.getDataManager
    val boostManager = playingField.getActionManager.getBoostManager
    val revertStrategy = boostManager.getRevertStrategy
    val scores = playingField.getScores

    val attackerBeforeAction = roles.attacker
    val defender = roles.defender
    
    attackerBeforeAction.actionStates.get(PlayerActionPolicies.DoubleAttack) match {
      case Some(OutOfActions) => playingField.notifyObservers(NoDoubleAttacksEvent(attackerBeforeAction))
        return false
      case Some(CanPerformAction(remainingUses)) if remainingUses <= 0 =>
        return false
      case _ =>
    }
    
    val attackerAfterAction = attackerBeforeAction.performAction(PlayerActionPolicies.DoubleAttack)
    
    attackerAfterAction.actionStates.get(PlayerActionPolicies.DoubleAttack) match {
      case Some(CanPerformAction(remainingUses)) =>
      case Some(OutOfActions) => playingField.notifyObservers(NoDoubleAttacksEvent(attackerBeforeAction))
      case _ =>
    }
    
    roles.setRoles(attackerAfterAction, defender)

    val attackerHand = fieldState.getPlayerHand(attackerAfterAction)
    val defenderHand = fieldState.getPlayerHand(defender)

    Try {
      if (attackerHand.getHandSize < 2) {
        throw new IllegalStateException("Not enough cards for a double attack.")
      }

      val attackingCard1 = attackerHand.removeLastCard()
      val attackingCard2 = attackerHand.removeLastCard()
      val attackValue = attackingCard1.valueToInt + attackingCard2.valueToInt

      val defenderCard = if (fieldState.allDefendersBeaten(defender)) {
        fieldState.getPlayerGoalkeeper(defender).getOrElse(
          throw new NoSuchElementException("Goalkeeper not found")
        )
      } else {
        fieldState.getDefenderCard(defender, defenderIndex)
      }

      playingField.notifyObservers(DoubleComparedCardsEvent(attackingCard1, attackingCard2, defenderCard))

      val result = if (fieldState.allDefendersBeaten(defender)) {
        processGoalkeeperAttack(
          attackerAfterAction,
          defender,
          attackerHand,
          defenderHand,
          attackingCard1,
          attackingCard2,
          attackValue,
          fieldState,
          revertStrategy,
          scores,
          roles,
          playingField)
      } else {
        processDefenderAttack(
          attackerAfterAction,
          defender,
          attackerHand,
          defenderHand,
          attackingCard1,
          attackingCard2,
          attackValue,
          fieldState,
          revertStrategy,
          roles,
          playingField)
      }

      playingField.notifyObservers()
      playingField.notifyObservers(AttackResultEvent(attackerAfterAction, defender, result))
      result
    } match {
      case Success(result) => result
      case Failure(exception) =>
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
                                       roles: IRolesManager,
                                       playingField: IPlayingField
                                     ): Boolean = {
    val goalkeeper = fieldState.getPlayerGoalkeeper(defender).getOrElse(
      throw new NoSuchElementException("Goalkeeper not found.")
    )
    val revertedGoalkeeper = revertStrategy.revertCard(goalkeeper)
    val goalkeeperValue = goalkeeper.valueToInt

    val attackResult: Boolean = if (attackValue > goalkeeperValue) {
      attackerWins(attackerHand, playingField, attackingCard1, attackingCard2, revertStrategy.revertCard(goalkeeper))
      fieldState.removeDefenderGoalkeeper(defender)
      fieldState.setPlayerGoalkeeper(defender, None)
      fieldState.setPlayerDefenders(defender, List.empty)

      scores.scoreGoal(attacker)
      fieldState.refillDefenderField(defender)
      roles.switchRoles()
      true
    } else {
      defenderWins(defenderHand, playingField, attackingCard1, attackingCard2, revertStrategy.revertCard(goalkeeper))
      fieldState.removeDefenderGoalkeeper(defender)
      fieldState.refillDefenderField(defender)
      roles.switchRoles()
      false
    }
    true
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
                                     roles: IRolesManager,
                                     playingField: IPlayingField
                                   ): Boolean = {
    val defenderCard = fieldState.getDefenderCard(defender, defenderIndex)
    val defenseValue = defenderCard.valueToInt
    val revertedCard = revertStrategy.revertCard(defenderCard)
    val attackResult: Boolean = if (attackValue > defenseValue) {
      attackerWins(attackerHand, playingField, attackingCard1, attackingCard2, revertStrategy.revertCard(defenderCard))
      fieldState.removeDefenderCard(defender, defenderCard)
      fieldState.removeDefenderCard(defender, revertedCard)
      true
    } else if (attackValue < defenseValue) {
      defenderWins(defenderHand, playingField, attackingCard1, attackingCard2, revertStrategy.revertCard(defenderCard))
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
        roles,
        playingField)
    }
    true
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
                         roles: IRolesManager,
                         playingField: IPlayingField
                       ): Boolean = {
    if (attackerHand.nonEmpty && defenderHand.nonEmpty) {
      playingField.notifyObservers(Events.DoubleTieComparison)
      val extraAttackerCard = attackerHand.removeLastCard()
      val extraDefenderCard = defenderHand.removeLastCard()
      val revertedExtraAttackerCard = revertStrategy.revertCard(extraAttackerCard)
      val revertedExtraDefenderCard = revertStrategy.revertCard(extraDefenderCard)

      val defenderCard = fieldState.getDefenderCard(defender, defenderIndex)
      val revertedDefenderCard = revertStrategy.revertCard(defenderCard)
      playingField.notifyObservers(DoubleTieComparisonEvent(
        attackingCard1, attackingCard2, defenderCard, extraAttackerCard, extraDefenderCard))
      val tiebreakerResult = extraAttackerCard.compare(extraDefenderCard)

      if (tiebreakerResult > 0) {
        attackerWins(
          attackerHand,
          playingField,
          attackingCard1,
          attackingCard2,
          revertStrategy.revertCard(extraAttackerCard),
          revertStrategy.revertCard(extraDefenderCard))
        fieldState.removeDefenderCard(defender, fieldState.getDefenderCard(defender, defenderIndex))
        fieldState.removeDefenderCard(defender, revertedDefenderCard)
        fieldState.refillDefenderField(defender)
        true
      } else {
        defenderWins(
          defenderHand,
          playingField,
          attackingCard1,
          attackingCard2,
          revertStrategy.revertCard(extraAttackerCard),
          revertStrategy.revertCard(extraDefenderCard))
        fieldState.removeDefenderCard(defender, fieldState.getDefenderCard(defender, defenderIndex))
        fieldState.removeDefenderCard(defender, revertedDefenderCard)
        fieldState.refillDefenderField(defender)
        roles.switchRoles()
        true
      }
    } else {
      roles.switchRoles()
      true
    }
  }

  private def attackerWins(
                            hand: IHandCardsQueue,
                            playingField: IPlayingField,
                            cards: ICard*
                          ): Unit = {
    cards.foreach { card =>
      hand.addCard(card)
    }

    playingField.notifyObservers(AttackResultEvent(playingField.getAttacker, playingField.getDefender, attackSuccess = true))

  }


  private def defenderWins(
                            hand: IHandCardsQueue,
                            playingField: IPlayingField,
                            cards: ICard*
                          ): Unit = {
    cards.foreach(hand.addCard)

    playingField.notifyObservers(AttackResultEvent(playingField.getAttacker, playingField.getDefender, attackSuccess = false))

  }
}