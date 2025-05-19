package de.htwg.se.soccercardclash.model.gameComponent.state.strategy.attackStrategy.base

import de.htwg.se.soccercardclash.model.cardComponent.ICard
import de.htwg.se.soccercardclash.model.cardComponent.dataStructure.*
import de.htwg.se.soccercardclash.model.gameComponent.state.IGameState
import de.htwg.se.soccercardclash.model.gameComponent.state.components.{IDataManager, IRoles, IScores}
import de.htwg.se.soccercardclash.model.gameComponent.state.manager.IPlayerActionManager
import de.htwg.se.soccercardclash.model.gameComponent.state.strategy.attackStrategy.IAttackStrategy
import de.htwg.se.soccercardclash.model.gameComponent.state.strategy.boostStrategy.{BoostManager, IBoostManager, IRevertStrategy}
import de.htwg.se.soccercardclash.model.playerComponent.IPlayer
import de.htwg.se.soccercardclash.model.playerComponent.factory.IPlayerFactory
import de.htwg.se.soccercardclash.model.playerComponent.playerAction.*
import de.htwg.se.soccercardclash.util
import de.htwg.se.soccercardclash.util.{EventDispatcher, GameActionEvent, ObservableEvent, StateEvent}

import scala.util.{Failure, Success, Try}

// TODO : Implement Goalkeeper tie case
class DoubleAttackStrategy(
                            defenderIndex: Int,
                            playerActionService: IPlayerActionManager,
                            boostManager: IBoostManager
                          ) extends IAttackStrategy {

  override def execute(state: IGameState): (Boolean, IGameState, List[ObservableEvent]) = {
    val roles = state.getRoles
    val attackerBeforeAction = roles.attacker
    val defender = roles.defender
    val revertStrategy = boostManager.getRevertStrategy(state)
    val scores = state.getScores
    val dataManager = state.getDataManager

    if (!playerActionService.canPerform(attackerBeforeAction, PlayerActionPolicies.DoubleAttack)) {
      return (false, state, List(StateEvent.NoDoubleAttacksEvent(attackerBeforeAction)))
    }

    val attackerAfterAction = playerActionService.performAction(attackerBeforeAction, PlayerActionPolicies.DoubleAttack)
    val updatedRolesInit = roles.setRoles(attackerAfterAction, defender)

    Try {
      val attackerHand = dataManager.getPlayerHand(attackerAfterAction)
      if (attackerHand.getHandSize < 2)
        throw new IllegalStateException("Not enough cards for a double attack.")

      val (attackingCard1, handAfterFirst) = attackerHand.removeLastCard().get
      val (attackingCard2, updatedAttackerHand) = handAfterFirst.removeLastCard().get
      val dataManagerAfterDraw = dataManager.setPlayerHand(attackerAfterAction, updatedAttackerHand)

      val attackValue = attackingCard1.valueToInt + attackingCard2.valueToInt
      val defenderCard: Option[ICard] =
        if (dataManagerAfterDraw.allDefendersBeaten(defender))
          dataManagerAfterDraw.getPlayerGoalkeeper(defender)
        else
          dataManagerAfterDraw.getDefenderCard(defender, defenderIndex)

      val comparisonEvent =
        StateEvent.DoubleComparedCardsEvent(Some(attackingCard1), Some(attackingCard2), defenderCard)



      val (finalManager, updatedRoles, updatedScores, additionalEvents) =
        if (dataManagerAfterDraw.allDefendersBeaten(defender))
          processGoalkeeperAttack(
            attackerAfterAction,
            defender,
            updatedAttackerHand,
            dataManagerAfterDraw,
            attackingCard1,
            attackingCard2,
            attackValue,
            revertStrategy,
            scores,
            updatedRolesInit
          )
        else {
          val (mgr, rls, events) = processDefenderAttack(
            attackerAfterAction,
            defender,
            updatedAttackerHand,
            dataManagerAfterDraw.getPlayerHand(defender),
            dataManagerAfterDraw,
            Some(attackingCard1),
            Some(attackingCard2),
            defenderCard,
            attackValue,
            revertStrategy,
            updatedRolesInit
          )
          (mgr, rls, scores, events)
        }

      val updatedField = state
        .withDataManager(finalManager)
        .withRoles(updatedRoles)
        .withScores(updatedScores)

      (true, updatedField, comparisonEvent :: additionalEvents)
    } match {
      case Success(result) => result
      case Failure(_) => (false, state, Nil)
    }
  }

  private def processGoalkeeperAttack(
                                       attacker: IPlayer,
                                       defender: IPlayer,
                                       attackerHand: IHandCardsQueue,
                                       dataManager: IDataManager,
                                       attackingCard1: ICard,
                                       attackingCard2: ICard,
                                       attackValue: Int,
                                       revertStrategy: IRevertStrategy,
                                       scores: IScores,
                                       roles: IRoles
                                     ): (IDataManager, IRoles, IScores, List[ObservableEvent]) = {

    val goalkeeperOpt = dataManager.getPlayerGoalkeeper(defender)
    val revertedGoalkeeper = revertStrategy.revertCard(goalkeeperOpt)
    val goalkeeperValue = goalkeeperOpt.map(_.valueToInt).getOrElse(
      throw new NoSuchElementException("Goalkeeper not found.")
    )

    if (attackValue > goalkeeperValue) {
      val (updatedManager0, resultEvent) = attackerWins(
        attackerHand,
        dataManager,
        attacker,
        defender,
        Some(attackingCard1), Some(attackingCard2),
        revertedGoalkeeper
      )

      val updatedManager = updatedManager0
        .removeDefenderGoalkeeper(defender)
        .setPlayerGoalkeeper(defender, None)
        .setPlayerDefenders(defender, List.empty)
        .refillDefenderField(defender)

      val (updatedScores, scoreEvents) = scores.scoreGoal(attacker)
      val updatedRoles = roles.switchRoles()

      (
        updatedManager,
        updatedRoles,
        updatedScores,
        List(resultEvent, GameActionEvent.DoubleAttack) ++ scoreEvents
      )

    } else {
      val (updatedManager0, resultEvent) = defenderWins(
        dataManager.getPlayerHand(defender),
        dataManager,
        attacker,
        defender,
        Some(attackingCard1), Some(attackingCard2),
        revertedGoalkeeper
      )

      val updatedManager = updatedManager0
        .removeDefenderGoalkeeper(defender)
        .refillDefenderField(defender)

      val updatedRoles = roles.switchRoles()

      (
        updatedManager,
        updatedRoles,
        scores,
        List(resultEvent, GameActionEvent.DoubleAttack)
      )
    }
  }

  private def attackerWins(
                            hand: IHandCardsQueue,
                            dataManager: IDataManager,
                            attacker: IPlayer,
                            defender: IPlayer,
                            cards: Option[ICard]*
                          ): (IDataManager, ObservableEvent) = {
    val updatedHand = cards.flatten.foldLeft(hand)((h, card) => h.addCard(card))
    val updatedManager = dataManager.setPlayerHand(attacker, updatedHand)
    val event = StateEvent.AttackResultEvent(attacker, defender, attackSuccess = true)

    (updatedManager, event)
  }


  private def defenderWins(
                            hand: IHandCardsQueue,
                            dataManager: IDataManager,
                            attacker: IPlayer,
                            defender: IPlayer,
                            cards: Option[ICard]*
                          ): (IDataManager, ObservableEvent) = {
    val updatedHand = cards.flatten.foldLeft(hand)((h, card) => h.addCard(card))
    val updatedManager = dataManager.setPlayerHand(defender, updatedHand)
    val event = StateEvent.AttackResultEvent(attacker, defender, attackSuccess = false)
    (updatedManager, event)
  }


  private def processDefenderAttack(
                                     attacker: IPlayer,
                                     defender: IPlayer,
                                     attackerHand: IHandCardsQueue,
                                     defenderHand: IHandCardsQueue,
                                     dataManager: IDataManager,
                                     attackingCard1: Option[ICard],
                                     attackingCard2: Option[ICard],
                                     defenderCard: Option[ICard],
                                     attackValue: Int,
                                     revertStrategy: IRevertStrategy,
                                     roles: IRoles
                                   ): (IDataManager, IRoles, List[ObservableEvent]) = {

    val revertedDefenderCard = revertStrategy.revertCard(defenderCard)
    val defenderValue = defenderCard.map(_.valueToInt).getOrElse(
      throw new NoSuchElementException("Defender card not found.")
    )

    if (attackValue > defenderValue) {
      val (updatedManager0, resultEvent) = attackerWins(
        attackerHand,
        dataManager,
        attacker,
        defender,
        attackingCard1,
        attackingCard2,
        revertedDefenderCard
      )

      val updatedManager = updatedManager0
        .removeDefenderCard(defender, defenderCard)
        .removeDefenderCard(defender, revertedDefenderCard)

      (updatedManager, roles, List(resultEvent, GameActionEvent.DoubleAttack))

    } else if (attackValue < defenderValue) {
      val (updatedManager0, resultEvent) = defenderWins(
        defenderHand,
        dataManager,
        attacker,
        defender,
        attackingCard1,
        attackingCard2,
        revertedDefenderCard
      )

      val updatedManager = updatedManager0
        .removeDefenderCard(defender, defenderCard)
        .removeDefenderCard(defender, revertedDefenderCard)
        .refillDefenderField(defender)

      val updatedRoles = roles.switchRoles()

      (updatedManager, updatedRoles, List(resultEvent, GameActionEvent.DoubleAttack))

    } else {
      handleTie(
        attacker,
        defender,
        attackerHand,
        defenderHand,
        attackingCard1,
        attackingCard2,
        dataManager,
        revertStrategy,
        roles
      )
    }
  }

  private def handleTie(
                         attacker: IPlayer,
                         defender: IPlayer,
                         attackerHand: IHandCardsQueue,
                         defenderHand: IHandCardsQueue,
                         attackingCard1: Option[ICard],
                         attackingCard2: Option[ICard],
                         dataManager: IDataManager,
                         revertStrategy: IRevertStrategy,
                         roles: IRoles
                       ): (IDataManager, IRoles, List[ObservableEvent]) = {

    if (attackerHand.getHandSize > 0 && defenderHand.getHandSize > 0) {
      
      (attackerHand.removeLastCard(), defenderHand.removeLastCard()) match {
        case (Success((extraAttackerCard, updatedAttackerHand)),
        Success((extraDefenderCard, updatedDefenderHand))) =>

          // Wrap ICard values in Some(...) when needed
          val revertedExtraAttackerCard = revertStrategy.revertCard(Some(extraAttackerCard))
          val revertedExtraDefenderCard = revertStrategy.revertCard(Some(extraDefenderCard))

          val defenderCard = dataManager.getDefenderCard(defender, defenderIndex)
          val revertedDefenderCard = revertStrategy.revertCard(defenderCard)

          val tiebreakerResult = extraAttackerCard.compare(extraDefenderCard)

          val events = List(
            StateEvent.DoubleTieComparisonEvent(
              attackingCard1, attackingCard2,
              defenderCard,
              Some(extraAttackerCard),
              Some(extraDefenderCard)
            )
          )
          if (tiebreakerResult > 0) {
            val (updatedManager0, resultEvent) = attackerWins(
              updatedAttackerHand,
              dataManager,
              attacker,
              defender,
              attackingCard1,
              attackingCard2,
              revertedExtraAttackerCard,
              revertedExtraDefenderCard
            )
            val updatedManager = updatedManager0
              .removeDefenderCard(defender, defenderCard)
              .removeDefenderCard(defender, revertedDefenderCard)

            (updatedManager, roles, resultEvent :: events)

          } else {
            val (updatedManager0, resultEvent) = defenderWins(
              updatedDefenderHand,
              dataManager,
              attacker,
              defender,
              attackingCard1,
              attackingCard2,
              revertedExtraAttackerCard,
              revertedExtraDefenderCard
            )
            val updatedManager = updatedManager0
              .removeDefenderCard(defender, defenderCard)
              .removeDefenderCard(defender, revertedDefenderCard)
              .refillDefenderField(defender)

            val updatedRoles = roles.switchRoles()
            (updatedManager, updatedRoles, resultEvent :: events)
          }

        case _ =>
          val updatedRoles = roles.switchRoles()
          (dataManager, updatedRoles, List(GameActionEvent.DoubleTieComparison))
      }

    } else {
      val updatedRoles = roles.switchRoles()
      (dataManager, updatedRoles, List(GameActionEvent.DoubleTieComparison))
    }
  }

}
