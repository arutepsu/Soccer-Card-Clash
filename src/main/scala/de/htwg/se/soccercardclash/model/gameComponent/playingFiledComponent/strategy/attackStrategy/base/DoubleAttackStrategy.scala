package de.htwg.se.soccercardclash.model.gameComponent.playingFiledComponent.strategy.attackStrategy.base

import de.htwg.se.soccercardclash.model.playerComponent.playerAction.*
import de.htwg.se.soccercardclash.model.gameComponent.playingFiledComponent.IPlayingField
import de.htwg.se.soccercardclash.model.gameComponent.playingFiledComponent.strategy.attackStrategy.IAttackStrategy
import de.htwg.se.soccercardclash.model.gameComponent.playingFiledComponent.strategy.boostStrategy.{BoostManager, IRevertStrategy}
import de.htwg.se.soccercardclash.model.playerComponent.IPlayer
import de.htwg.se.soccercardclash.model.playerComponent.factory.IPlayerFactory
import de.htwg.se.soccercardclash.model.cardComponent.dataStructure.*
import de.htwg.se.soccercardclash.model.gameComponent.playingFiledComponent.manager.{IDataManager, IPlayerActionManager, IRolesManager}
import de.htwg.se.soccercardclash.model.cardComponent.ICard
import de.htwg.se.soccercardclash.model.gameComponent.playingFiledComponent.strategy.scoringStrategy.IPlayerScores
import de.htwg.se.soccercardclash.util.{AttackResultEvent, DoubleComparedCardsEvent, DoubleTieComparisonEvent, Events, NoDoubleAttacksEvent}


import scala.util.{Failure, Success, Try}

//TODO : Implement Goalkeeper tie case
class DoubleAttackStrategy(
                            defenderIndex: Int,
                            playerActionService: IPlayerActionManager
                          ) extends IAttackStrategy {

  override def execute(playingField: IPlayingField): Boolean = {
    val roles = playingField.getRoles
    val fieldState = playingField.getDataManager
    val boostManager = playingField.getActionManager.getBoostManager
    val revertStrategy = boostManager.getRevertStrategy
    val scores = playingField.getScores

    val attackerBeforeAction = roles.attacker
    val defender = roles.defender

    if (!playerActionService.canPerform(attackerBeforeAction, PlayerActionPolicies.DoubleAttack)) {
      playingField.notifyObservers(NoDoubleAttacksEvent(attackerBeforeAction))
      return false
    }

    val attackerAfterAction = playerActionService.performAction(attackerBeforeAction, PlayerActionPolicies.DoubleAttack)

    roles.setRoles(attackerAfterAction, defender)

    val attackerHand = fieldState.getPlayerHand(attackerAfterAction)
    val defenderHand = fieldState.getPlayerHand(defender)

    Try {
      if (attackerHand.getHandSize < 2)
        throw new IllegalStateException("Not enough cards for a double attack.")

      val firstResult = attackerHand.removeLastCard().get
      val (attackingCard1, handAfterFirst) = firstResult

      val secondResult = handAfterFirst.removeLastCard().get
      val (attackingCard2, handAfterSecond) = secondResult

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
          handAfterSecond, // <- updated hand
          defenderHand,
          attackingCard1,
          attackingCard2,
          attackValue,
          fieldState,
          revertStrategy,
          scores,
          roles,
          playingField
        )
      } else {
        processDefenderAttack(
          attackerAfterAction,
          defender,
          handAfterSecond, // <- updated hand
          defenderHand,
          attackingCard1,
          attackingCard2,
          attackValue,
          fieldState,
          revertStrategy,
          roles,
          playingField
        )
      }

      playingField.notifyObservers()
      playingField.notifyObservers(AttackResultEvent(attackerAfterAction, defender, result))
      result
    } match {
      case Success(result) => result
      case Failure(_) => false
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
    if (attackerHand.getHandSize > 0 && defenderHand.getHandSize > 0) {
      playingField.notifyObservers(Events.DoubleTieComparison)

      val attackerCardTry = attackerHand.removeLastCard()
      val defenderCardTry = defenderHand.removeLastCard()

      (attackerCardTry, defenderCardTry) match {
        case (Success((extraAttackerCard, updatedAttackerHand)),
        Success((extraDefenderCard, updatedDefenderHand))) =>

          val revertedExtraAttackerCard = revertStrategy.revertCard(extraAttackerCard)
          val revertedExtraDefenderCard = revertStrategy.revertCard(extraDefenderCard)

          val defenderCard = fieldState.getDefenderCard(defender, defenderIndex)
          val revertedDefenderCard = revertStrategy.revertCard(defenderCard)

          playingField.notifyObservers(DoubleTieComparisonEvent(
            attackingCard1, attackingCard2, defenderCard, extraAttackerCard, extraDefenderCard))

          val tiebreakerResult = extraAttackerCard.compare(extraDefenderCard)

          if (tiebreakerResult > 0) {
            attackerWins(
              updatedAttackerHand,
              playingField,
              attackingCard1,
              attackingCard2,
              revertedExtraAttackerCard,
              revertedExtraDefenderCard
            )
            fieldState.removeDefenderCard(defender, defenderCard)
            fieldState.removeDefenderCard(defender, revertedDefenderCard)
            true
          } else {
            defenderWins(
              updatedDefenderHand,
              playingField,
              attackingCard1,
              attackingCard2,
              revertedExtraAttackerCard,
              revertedExtraDefenderCard
            )
            fieldState.removeDefenderCard(defender, defenderCard)
            fieldState.removeDefenderCard(defender, revertedDefenderCard)
            fieldState.refillDefenderField(defender)
            roles.switchRoles()
            true
          }

        case _ =>
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
    val updatedHand = cards.foldLeft(hand)((h, card) => h.addCard(card))
    playingField.getDataManager.setPlayerHand(playingField.getRoles.attacker, updatedHand)

    playingField.notifyObservers(AttackResultEvent(
      playingField.getRoles.attacker,
      playingField.getRoles.defender,
      attackSuccess = true
    ))
  }

  private def defenderWins(
                            hand: IHandCardsQueue,
                            playingField: IPlayingField,
                            cards: ICard*
                          ): Unit = {
    val updatedHand = cards.foldLeft(hand)((h, card) => h.addCard(card))
    playingField.getDataManager.setPlayerHand(playingField.getRoles.defender, updatedHand)

    playingField.notifyObservers(AttackResultEvent(
      playingField.getRoles.attacker,
      playingField.getRoles.defender,
      attackSuccess = false
    ))
  }

}