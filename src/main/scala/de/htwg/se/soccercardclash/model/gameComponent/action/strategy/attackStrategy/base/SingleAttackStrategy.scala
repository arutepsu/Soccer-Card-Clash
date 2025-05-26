package de.htwg.se.soccercardclash.model.gameComponent.action.strategy.attackStrategy.base

import de.htwg.se.soccercardclash.model.cardComponent.ICard
import de.htwg.se.soccercardclash.model.cardComponent.dataStructure.*
import de.htwg.se.soccercardclash.model.cardComponent.dataStructure.IHandCardsQueue
import de.htwg.se.soccercardclash.model.gameComponent.state.IGameState
import de.htwg.se.soccercardclash.model.gameComponent.state.components.{IGameCards, IRoles, IScores}
import de.htwg.se.soccercardclash.model.gameComponent.action.strategy.attackStrategy.IAttackStrategy
import de.htwg.se.soccercardclash.model.gameComponent.action.strategy.boostStrategy.{BoostManager, IBoostManager, IRevertStrategy}
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
    val gameCards = state.getGameCards

    Try {
      val attackerHand = gameCards.getPlayerHand(attacker)
      val (attackingCard, updatedAttackerHand) = attackerHand.removeLastCard().get
      val updatedGameCards = gameCards.newPlayerHand(attacker, updatedAttackerHand)

      val defenderCard: Option[ICard] =
        if (updatedGameCards.allDefendersBeaten(defender))
          updatedGameCards.getPlayerGoalkeeper(defender)
        else
          updatedGameCards.getDefenderCard(defender, defenderIndex)

      val comparisonEvent =
        StateEvent.ComparedCardsEvent(Some(attackingCard), defenderCard)

      val (newGameCards, updatedRoles, updatedScores, additionalEvents) =
        if (updatedGameCards.allDefendersBeaten(defender))
          processGoalkeeperAttack(
            attacker,
            defender,
            updatedAttackerHand,
            updatedGameCards,
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
            updatedGameCards.getPlayerHand(defender),
            attackingCard,
            defenderCard,
            updatedGameCards,
            revertStrategy,
            roles
          )
          (mgr, rls, scores, events)
        }

      val newState = state
        .newGameCards(newGameCards)
        .newRoles(updatedRoles)
        .newScores(updatedScores)

      (true, newState, comparisonEvent :: additionalEvents)
    } match {
      case Success(result) => result
      case Failure(_)      => (false, state, Nil)
    }
  }


  protected def processGoalkeeperAttack(
                                       attacker: IPlayer,
                                       defender: IPlayer,
                                       attackerHand: IHandCardsQueue,
                                       gameCards: IGameCards,
                                       attackingCard: ICard,
                                       revertStrategy: IRevertStrategy,
                                       scores: IScores,
                                       roles: IRoles
                                     ): (IGameCards, IRoles, IScores, List[ObservableEvent]) = {

    val goalkeeperOpt = gameCards.getPlayerGoalkeeper(defender)

    val goalkeeper = goalkeeperOpt.getOrElse(
      throw new NoSuchElementException("Goalkeeper not found")
    )

    val revertedGoalkeeper: Option[ICard] = revertStrategy.revertCard(goalkeeperOpt)
    val comparisonResult = attackingCard.compare(goalkeeper)

    if (comparisonResult > 0) {
      val (updatedGameCards, resultEvent) = attackerWins(
        attackerHand,
        gameCards,
        attacker,
        defender,
        Some(attackingCard),
        revertedGoalkeeper
      )

      val newGameCards = updatedGameCards
        .removeDefenderGoalkeeper(defender)
        .newPlayerGoalkeeper(defender, None)
        .newPlayerDefenders(defender, List.fill(3)(None)) // keep empty slots
        .refillDefenderField(defender)

      val (updatedScores, scoreEvents) = scores.scoreGoal(attacker)
      val updatedRoles = roles.switchRoles()

      (
        newGameCards,
        updatedRoles,
        updatedScores,
        List(resultEvent, GameActionEvent.RegularAttack) ++ scoreEvents
      )

    } else {
      val (updatedGameCards, resultEvent) = defenderWins(
        gameCards.getPlayerHand(defender),
        gameCards,
        attacker,
        defender,
        Some(attackingCard),
        revertedGoalkeeper
      )

      val newGameCards = updatedGameCards
        .removeDefenderGoalkeeper(defender)
        .refillDefenderField(defender)

      val updatedRoles = roles.switchRoles()

      (
        newGameCards,
        updatedRoles,
        scores,
        List(resultEvent, GameActionEvent.RegularAttack)
      )
    }
  }


  protected def attackerWins(
                            hand: IHandCardsQueue,
                            gameCards: IGameCards,
                            attacker: IPlayer,
                            defender: IPlayer,
                            cards: Option[ICard]*
                          ): (IGameCards, ObservableEvent) = {
    val updatedHand = cards.flatten.foldLeft(hand)((h, card) => h.addCard(card))
    val updatedGameCards = gameCards.newPlayerHand(attacker, updatedHand)

    val event = StateEvent.AttackResultEvent(attacker, defender, attackSuccess = true)
    (updatedGameCards, event)
  }


  protected def defenderWins(
                            hand: IHandCardsQueue,
                            gameCards: IGameCards,
                            attacker: IPlayer,
                            defender: IPlayer,
                            cards: Option[ICard]*
                          ): (IGameCards, ObservableEvent) = {
    val updatedHand = cards.flatten.foldLeft(hand)((h, card) => h.addCard(card))
    val updatedGameCards = gameCards.newPlayerHand(defender, updatedHand)

    val event = StateEvent.AttackResultEvent(attacker, defender, attackSuccess = false)
    (updatedGameCards, event)
  }


  protected def processDefenderAttack(
                                     attacker: IPlayer,
                                     defender: IPlayer,
                                     attackerHand: IHandCardsQueue,
                                     defenderHand: IHandCardsQueue,
                                     attackingCard: ICard,
                                     defenderCard: Option[ICard],
                                     gameCards: IGameCards,
                                     revertStrategy: IRevertStrategy,
                                     roles: IRoles
                                   ): (IGameCards, IRoles, List[ObservableEvent]) = {

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
          gameCards,
          revertStrategy,
          roles
        )

      case r if r > 0 =>
        val (updatedGameCards, resultEvent) = attackerWins(
          attackerHand,
          gameCards,
          attacker,
          defender,
          Some(attackingCard),
          revertedCard
        )

        val newGameCards = updatedGameCards
          .removeDefenderCard(defender, defenderCard)
          .removeDefenderCard(defender, revertedCard)

        (newGameCards, roles, List(resultEvent, GameActionEvent.RegularAttack))

      case _ =>
        val (updatedGameCards, resultEvent) = defenderWins(
          defenderHand,
          gameCards,
          attacker,
          defender,
          Some(attackingCard),
          revertedCard
        )

        val newGameCards = updatedGameCards
          .removeDefenderCard(defender, defenderCard)
          .removeDefenderCard(defender, revertedCard)
          .refillDefenderField(defender)

        val updatedRoles = roles.switchRoles()
        (newGameCards, updatedRoles, List(resultEvent, GameActionEvent.RegularAttack))
    }
  }

  protected def handleTie(
                         attacker: IPlayer,
                         defender: IPlayer,
                         attackerHand: IHandCardsQueue,
                         defenderHand: IHandCardsQueue,
                         attackingCard: ICard,
                         defenderCard: Option[ICard],
                         gameCards: IGameCards,
                         revertStrategy: IRevertStrategy,
                         roles: IRoles
                       ): (IGameCards, IRoles, List[ObservableEvent]) = {

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
          val (updatedGameCards, _) = attackerWins(
            updatedAttackerHand,
            gameCards,
            attacker,
            defender,
            Some(attackingCard),
            revertedDefenderCard,
            Some(extraAttackerCard),
            Some(extraDefenderCard)
          )
          val newGameCards = updatedGameCards
            .removeDefenderCard(defender, defenderCard)
            .removeDefenderCard(defender, revertedDefenderCard)

          (newGameCards, roles, events)

        } else {
          val (updatedGameCards, _) = defenderWins(
            updatedDefenderHand,
            gameCards,
            attacker,
            defender,
            Some(attackingCard),
            revertedDefenderCard,
            Some(extraAttackerCard),
            Some(extraDefenderCard)
          )
          val newGameCards = updatedGameCards
            .removeDefenderCard(defender, defenderCard)
            .removeDefenderCard(defender, revertedDefenderCard)
            .refillDefenderField(defender)

          val updatedRoles = roles.switchRoles()
          (newGameCards, updatedRoles, events)
        }
      case _ =>
        val updatedRoles = roles.switchRoles()
        (gameCards, updatedRoles, List(GameActionEvent.TieComparison))
    }
  }


}
