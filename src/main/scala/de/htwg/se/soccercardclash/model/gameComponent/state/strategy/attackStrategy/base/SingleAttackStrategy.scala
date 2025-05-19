package de.htwg.se.soccercardclash.model.gameComponent.state.strategy.attackStrategy.base

import de.htwg.se.soccercardclash.model.cardComponent.ICard
import de.htwg.se.soccercardclash.model.cardComponent.dataStructure.*
import de.htwg.se.soccercardclash.model.gameComponent.state.IGameState
import de.htwg.se.soccercardclash.model.gameComponent.state.components.{IDataManager, IRoles, IScores}
import de.htwg.se.soccercardclash.model.gameComponent.state.strategy.attackStrategy.IAttackStrategy
import de.htwg.se.soccercardclash.model.gameComponent.state.strategy.boostStrategy.{BoostManager, IBoostManager, IRevertStrategy}
import de.htwg.se.soccercardclash.model.playerComponent.IPlayer
import de.htwg.se.soccercardclash.model.playerComponent.factory.IPlayerFactory
import de.htwg.se.soccercardclash.model.playerComponent.playerAction.*
import de.htwg.se.soccercardclash.util.{EventDispatcher, GameActionEvent, ObservableEvent, StateEvent}

import scala.util.{Failure, Success, Try}

//TODO : Implement Goalkeeper tie case
class SingleAttackStrategy(defenderIndex: Int, boostManager: IBoostManager) extends IAttackStrategy {

  override def execute(state: IGameState): (Boolean, IGameState, List[ObservableEvent]) = {
    val roles = state.getRoles
    val attacker = roles.attacker
    val defender = roles.defender
    val revertStrategy = boostManager.getRevertStrategy(state)
    val scores = state.getScores
    val dataManager = state.getDataManager

    Try {
      val attackerHand = dataManager.getPlayerHand(attacker)
      val (attackingCard, updatedAttackerHand) = attackerHand.removeLastCard().get
      val updatedDataManager = dataManager.setPlayerHand(attacker, updatedAttackerHand)

      val defenderCard: Option[ICard] =
        if (updatedDataManager.allDefendersBeaten(defender))
          updatedDataManager.getPlayerGoalkeeper(defender)
        else
          updatedDataManager.getDefenderCard(defender, defenderIndex)

      val comparisonEvent =
        StateEvent.ComparedCardsEvent(Some(attackingCard), defenderCard)

      val (finalManager, updatedRoles, updatedScores, additionalEvents) =
        if (updatedDataManager.allDefendersBeaten(defender))
          processGoalkeeperAttack(
            attacker,
            defender,
            updatedAttackerHand,
            updatedDataManager,
            attackingCard,
            revertStrategy,
            scores,
            roles
          )
        else {
          val (mgr, rls, events) = processDefenderAttack(
            attacker,
            defender,
            updatedAttackerHand,
            updatedDataManager.getPlayerHand(defender),
            attackingCard,
            defenderCard,
            updatedDataManager,
            revertStrategy,
            roles
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
      case Failure(_)      => (false, state, Nil)
    }
  }


  private def processGoalkeeperAttack(
                                       attacker: IPlayer,
                                       defender: IPlayer,
                                       attackerHand: IHandCardsQueue,
                                       dataManager: IDataManager,
                                       attackingCard: ICard,
                                       revertStrategy: IRevertStrategy,
                                       scores: IScores,
                                       roles: IRoles
                                     ): (IDataManager, IRoles, IScores, List[ObservableEvent]) = {

    val goalkeeperOpt = dataManager.getPlayerGoalkeeper(defender)

    val goalkeeper = goalkeeperOpt.getOrElse(
      throw new NoSuchElementException("Goalkeeper not found")
    )

    val revertedGoalkeeper: Option[ICard] = revertStrategy.revertCard(goalkeeperOpt)
    val comparisonResult = attackingCard.compare(goalkeeper)

    if (comparisonResult > 0) {
      val (updatedManager0, resultEvent) = attackerWins(
        attackerHand,
        dataManager,
        attacker,
        defender,
        Some(attackingCard),
        revertedGoalkeeper
      )

      val updatedManager = updatedManager0
        .removeDefenderGoalkeeper(defender)
        .setPlayerGoalkeeper(defender, None)
        .setPlayerDefenders(defender, List.fill(3)(None)) // keep empty slots
        .refillDefenderField(defender)

      val (updatedScores, scoreEvents) = scores.scoreGoal(attacker)
      val updatedRoles = roles.switchRoles()

      (
        updatedManager,
        updatedRoles,
        updatedScores,
        List(resultEvent, GameActionEvent.RegularAttack) ++ scoreEvents
      )

    } else {
      val (updatedManager0, resultEvent) = defenderWins(
        dataManager.getPlayerHand(defender),
        dataManager,
        attacker,
        defender,
        Some(attackingCard),
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
        List(resultEvent, GameActionEvent.RegularAttack)
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
                                     attackingCard: ICard,
                                     defenderCard: Option[ICard],
                                     dataManager: IDataManager,
                                     revertStrategy: IRevertStrategy,
                                     roles: IRoles
                                   ): (IDataManager, IRoles, List[ObservableEvent]) = {

    val revertedCard: Option[ICard] = revertStrategy.revertCard(defenderCard)

    val comparisonResult = attackingCard.compare(
      defenderCard.getOrElse(throw new NoSuchElementException("Defender card missing"))
    )

    comparisonResult match {
      case 0 =>
        handleTie(
          attacker,
          defender,
          attackerHand,
          defenderHand,
          attackingCard,
          defenderCard,
          dataManager,
          revertStrategy,
          roles
        )

      case r if r > 0 =>
        val (updatedManager0, resultEvent) = attackerWins(
          attackerHand,
          dataManager,
          attacker,
          defender,
          Some(attackingCard),
          revertedCard
        )

        val updatedManager = updatedManager0
          .removeDefenderCard(defender, defenderCard)
          .removeDefenderCard(defender, revertedCard)

        (updatedManager, roles, List(resultEvent, GameActionEvent.RegularAttack))

      case _ =>
        val (updatedManager0, resultEvent) = defenderWins(
          defenderHand,
          dataManager,
          attacker,
          defender,
          Some(attackingCard),
          revertedCard
        )

        val updatedManager = updatedManager0
          .removeDefenderCard(defender, defenderCard)
          .removeDefenderCard(defender, revertedCard)
          .refillDefenderField(defender)

        val updatedRoles = roles.switchRoles()
        (updatedManager, updatedRoles, List(resultEvent, GameActionEvent.RegularAttack))
    }
  }

  private def handleTie(
                         attacker: IPlayer,
                         defender: IPlayer,
                         attackerHand: IHandCardsQueue,
                         defenderHand: IHandCardsQueue,
                         attackingCard: ICard,
                         defenderCard: Option[ICard],
                         dataManager: IDataManager,
                         revertStrategy: IRevertStrategy,
                         roles: IRoles
                       ): (IDataManager, IRoles, List[ObservableEvent]) = {

    val revertedDefenderCard: Option[ICard] = revertStrategy.revertCard(defenderCard)

    (attackerHand.removeLastCard(), defenderHand.removeLastCard()) match {
      case (
        Success((extraAttackerCard, updatedAttackerHand)),
        Success((extraDefenderCard, updatedDefenderHand))
        ) =>
        val tiebreakerResult = extraAttackerCard.compare(extraDefenderCard)

        val events = List(
          StateEvent.TieComparisonEvent(
            Some(attackingCard),
            defenderCard,
            Some(extraAttackerCard),
            Some(extraDefenderCard)
          )
        )

        if (tiebreakerResult > 0) {
          val (updatedManager, _) = attackerWins(
            updatedAttackerHand,
            dataManager,
            attacker,
            defender,
            Some(attackingCard),
            revertedDefenderCard,
            Some(extraAttackerCard),
            Some(extraDefenderCard)
          )
          val resultManager = updatedManager
            .removeDefenderCard(defender, defenderCard)
            .removeDefenderCard(defender, revertedDefenderCard)

          (resultManager, roles, events)

        } else {
          val (updatedManager, _) = defenderWins(
            updatedDefenderHand,
            dataManager,
            attacker,
            defender,
            Some(attackingCard),
            revertedDefenderCard,
            Some(extraAttackerCard),
            Some(extraDefenderCard)
          )
          val resultManager = updatedManager
            .removeDefenderCard(defender, defenderCard)
            .removeDefenderCard(defender, revertedDefenderCard)
            .refillDefenderField(defender)

          val updatedRoles = roles.switchRoles()
          (resultManager, updatedRoles, events)
        }
      case _ =>
        val updatedRoles = roles.switchRoles()
        (dataManager, updatedRoles, List(GameActionEvent.TieComparison))
    }
  }


}
