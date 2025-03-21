package model.playingFiledComponent.strategy.attackStrategy.base

import controller.{AttackResultEvent, ComparedCardsEvent, Events, TieComparisonEvent}
import model.playingFiledComponent.IPlayingField
import model.playingFiledComponent.strategy.attackStrategy.IAttackStrategy
import model.playingFiledComponent.strategy.boostStrategy.BoostManager
import model.playerComponent.IPlayer
import model.playerComponent.factory.IPlayerFactory
import model.playingFiledComponent.dataStructure.IHandCardsQueue
import model.playingFiledComponent.manager.{IDataManager, IRolesManager}
import model.cardComponent.ICard
import model.playingFiledComponent.strategy.boostStrategy.IRevertStrategy
import model.playingFiledComponent.strategy.scoringStrategy.IPlayerScores
import util.ObservableEvent
import view.gui.scenes.PlayingFieldScene

import scala.util.{Failure, Success, Try}

//TODO : Implement Goalkeeper tie case
class SingleAttackStrategy(defenderIndex: Int) extends IAttackStrategy {

  override def execute(playingField: IPlayingField): Boolean = {
    val roles = playingField.getRoles
    val fieldState = playingField.getDataManager
    val boostManager = playingField.getActionManager.getBoostManager
    val revertStrategy = boostManager.getRevertStrategy
    val scores = playingField.getScores

    val attacker = roles.attacker
    val defender = roles.defender
    val attackerHand = fieldState.getPlayerHand(attacker)
    val defenderHand = fieldState.getPlayerHand(defender)
    Try {
      val attackingCard = attackerHand.removeLastCard()

      val defenderCard = if (fieldState.allDefendersBeaten(defender)) {
        fieldState.getPlayerGoalkeeper(defender).getOrElse(
          throw new NoSuchElementException("Goalkeeper not found")
        )
      } else {
        fieldState.getDefenderCard(defender, defenderIndex)
      }

      playingField.notifyObservers(ComparedCardsEvent(attackingCard, defenderCard))


      val result = if (fieldState.allDefendersBeaten(defender)) {
        processGoalkeeperAttack(
          attacker,
          defender,
          attackerHand,
          defenderHand,
          attackingCard,
          fieldState,
          revertStrategy,
          scores,
          roles,
          playingField)
      } else {
        processDefenderAttack(
          attacker,
          defender,
          attackerHand,
          defenderHand,
          attackingCard,
          fieldState,
          revertStrategy,
          roles,
          playingField)
      }

      playingField.notifyObservers()
      result
    } match {
      case Success(result) => result
      case Failure(exception) => false
    }
  }

  private def processGoalkeeperAttack(
                                       attacker: IPlayer,
                                       defender: IPlayer,
                                       attackerHand: IHandCardsQueue,
                                       defenderHand: IHandCardsQueue,
                                       attackingCard: ICard,
                                       fieldState: IDataManager,
                                       revertStrategy: IRevertStrategy,
                                       scores: IPlayerScores,
                                       roles: IRolesManager,
                                       playingField: IPlayingField
                                     ): Boolean = {

    val goalkeeper = fieldState.getPlayerGoalkeeper(defender).getOrElse(
      throw new NoSuchElementException("Goalkeeper not found")
    )
    val revertedGoalkeeper = revertStrategy.revertCard(goalkeeper)
    val comparisonResult = attackingCard.compare(goalkeeper)

    // ✅ Store attack success result
    val attackResult: Boolean = if (comparisonResult > 0) {
      attackerWins(attackerHand, playingField, attackingCard, revertStrategy.revertCard(goalkeeper))
      fieldState.removeDefenderGoalkeeper(defender)
      fieldState.setPlayerGoalkeeper(defender, None)
      fieldState.setPlayerDefenders(defender, List.empty)
      scores.scoreGoal(attacker)
      fieldState.refillDefenderField(defender)
      roles.switchRoles()
      true
    } else {
      defenderWins(defenderHand, playingField, attackingCard, revertStrategy.revertCard(goalkeeper))
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
                                     attackingCard: ICard,
                                     fieldState: IDataManager,
                                     revertStrategy: IRevertStrategy,
                                     roles: IRolesManager,
                                     playingField: IPlayingField
                                   ): Boolean = {

    val defenderCard = fieldState.getDefenderCard(defender, defenderIndex)
    val revertedCard = revertStrategy.revertCard(defenderCard)
    val comparisonResult = attackingCard.compare(defenderCard)

    val attackResult: Boolean = comparisonResult match {
      case 0 =>
        handleTie(attackerHand, defenderHand, attackingCard, defenderCard, fieldState, revertStrategy, roles, playingField)
        false

      case r if r > 0 =>
        attackerWins(attackerHand, playingField, attackingCard, revertStrategy.revertCard(defenderCard))
        fieldState.removeDefenderCard(defender, defenderCard)
        fieldState.removeDefenderCard(defender, revertedCard)
        true

      case _ =>
        defenderWins(defenderHand, playingField, attackingCard, revertStrategy.revertCard(defenderCard))
        fieldState.removeDefenderCard(defender, defenderCard)
        fieldState.removeDefenderCard(defender, revertedCard)
        fieldState.refillDefenderField(defender)
        roles.switchRoles()
        false
    }

    true
  }

  private def handleTie(
                         attackerHand: IHandCardsQueue,
                         defenderHand: IHandCardsQueue,
                         attackingCard: ICard,
                         defenderCard: ICard,
                         fieldState: IDataManager,
                         revertStrategy: IRevertStrategy,
                         roles: IRolesManager,
                         playingField: IPlayingField
                       ): Boolean = {

    val revertedCard = revertStrategy.revertCard(defenderCard)
    if (attackerHand.nonEmpty && defenderHand.nonEmpty) {
      val extraAttackerCard = attackerHand.removeLastCard()
      val extraDefenderCard = defenderHand.removeLastCard()
      playingField.notifyObservers(TieComparisonEvent(attackingCard, defenderCard, extraAttackerCard, extraDefenderCard))
      playingField.notifyObservers(Events.TieComparison)
      val tiebreakerResult = extraAttackerCard.compare(extraDefenderCard)

      if (tiebreakerResult > 0) {
        attackerWins(
          attackerHand,
          playingField,
          attackingCard,
          revertStrategy.revertCard(defenderCard),
          extraAttackerCard,
          extraDefenderCard)
        fieldState.removeDefenderCard(roles.defender, defenderCard)
        fieldState.removeDefenderCard(roles.defender, revertedCard)
      } else {
        defenderWins(
          defenderHand,
          playingField,
          attackingCard,
          revertStrategy.revertCard(defenderCard),
          extraAttackerCard,
          extraDefenderCard)
        fieldState.removeDefenderCard(roles.defender, defenderCard)
        fieldState.removeDefenderCard(roles.defender, revertedCard)
        fieldState.refillDefenderField(roles.defender)
        roles.switchRoles()
      }
    } else {
      roles.switchRoles()
    }
    true
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
