package de.htwg.se.soccercardclash.model.gameComponent.action.strategy.attackStrategy.base

import de.htwg.se.soccercardclash.model.cardComponent.ICard
import de.htwg.se.soccercardclash.model.cardComponent.dataStructure.*
import de.htwg.se.soccercardclash.model.gameComponent.IGameState
import de.htwg.se.soccercardclash.model.gameComponent.action.manager.IPlayerActionManager
import de.htwg.se.soccercardclash.model.gameComponent.action.strategy.IActionStrategy
import de.htwg.se.soccercardclash.model.gameComponent.action.strategy.attackStrategy.IAttackStrategy
import de.htwg.se.soccercardclash.model.gameComponent.action.strategy.boostStrategy.{BoostManager, IBoostManager, IRevertStrategy}
import de.htwg.se.soccercardclash.model.gameComponent.state.components.{IGameCards, IRoles, IScores}
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
                            revertStrategy: IRevertStrategy
                          ) extends IActionStrategy {

  override def execute(state: IGameState): (Boolean, IGameState, List[ObservableEvent]) = {
    val roles = state.getRoles
    val attackerBeforeAction = roles.attacker
    val defender = roles.defender
    val scores = state.getScores
    val gameCards = state.getGameCards

    if (!playerActionService.canPerform(attackerBeforeAction, PlayerActionPolicies.DoubleAttack)) {
      return (false, state, List(StateEvent.NoDoubleAttacksEvent(attackerBeforeAction)))
    }

    val attackerAfterAction = playerActionService.performAction(attackerBeforeAction, PlayerActionPolicies.DoubleAttack)
    val updatedRolesInit = roles.newRoles(attackerAfterAction, defender)

    Try {
      val attackerHand = gameCards.getPlayerHand(attackerAfterAction)
      if (attackerHand.getHandSize < 2)
        throw new IllegalStateException("Not enough cards for a double attack.")

      val (attackingCard1, handAfterFirst) = attackerHand.removeLastCard().get
      val (attackingCard2, updatedAttackerHand) = handAfterFirst.removeLastCard().get
      val gameCardsAfterDraw = gameCards.newPlayerHand(attackerAfterAction, updatedAttackerHand)

      val attackValue = attackingCard1.valueToInt + attackingCard2.valueToInt
      val defenderCard: Option[ICard] =
        if (gameCardsAfterDraw.allDefendersBeaten(defender))
          gameCardsAfterDraw.getPlayerGoalkeeper(defender)
        else
          gameCardsAfterDraw.getDefenderCard(defender, defenderIndex)

      val comparisonEvent =
        StateEvent.DoubleComparedCardsEvent(Some(attackingCard1), Some(attackingCard2), defenderCard)


      val (finalManager, updatedRoles, updatedScores, additionalEvents) =
        if (gameCardsAfterDraw.allDefendersBeaten(defender))
          processGoalkeeperAttack(
            attackerAfterAction,
            defender,
            updatedAttackerHand,
            gameCardsAfterDraw,
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
            gameCardsAfterDraw.getPlayerHand(defender),
            gameCardsAfterDraw,
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
        .newGameCards(finalManager)
        .newRoles(updatedRoles)
        .newScores(updatedScores)

      (true, updatedField, comparisonEvent :: additionalEvents)
    } match {
      case Success(result) => result
      case Failure(_) => (false, state, Nil)
    }
  }

  protected def processGoalkeeperAttack(
                                         attacker: IPlayer,
                                         defender: IPlayer,
                                         attackerHand: IHandCardsQueue,
                                         gameCards: IGameCards,
                                         attackingCard1: ICard,
                                         attackingCard2: ICard,
                                         attackValue: Int,
                                         revertStrategy: IRevertStrategy,
                                         scores: IScores,
                                         roles: IRoles
                                       ): (IGameCards, IRoles, IScores, List[ObservableEvent]) = {

    val goalkeeperOpt = gameCards.getPlayerGoalkeeper(defender)
    val revertedGoalkeeper = revertStrategy.revertCard(goalkeeperOpt)
    val goalkeeperValue = goalkeeperOpt.map(_.valueToInt).getOrElse(
      throw new NoSuchElementException("Goalkeeper not found.")
    )

    if (attackValue > goalkeeperValue) {
      val (updatedGameCards, resultEvent) = attackerWins(
        attackerHand,
        gameCards,
        attacker,
        defender,
        Some(attackingCard1), Some(attackingCard2),
        revertedGoalkeeper
      )

      val newGameCards = updatedGameCards
        .removeDefenderGoalkeeper(defender)
        .newPlayerGoalkeeper(defender, None)
        .newPlayerDefenders(defender, List.empty)
        .refillDefenderField(defender)

      val (updatedScores, scoreEvents) = scores.scoreGoal(attacker)
      val updatedRoles = roles.switchRoles()

      (
        newGameCards,
        updatedRoles,
        updatedScores,
        List(resultEvent, GameActionEvent.DoubleAttack) ++ scoreEvents
      )

    } else {
      val (updatedGameCards, resultEvent) = defenderWins(
        gameCards.getPlayerHand(defender),
        gameCards,
        attacker,
        defender,
        Some(attackingCard1), Some(attackingCard2),
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
        List(resultEvent, GameActionEvent.DoubleAttack)
      )
    }
  }

  protected def processDefenderAttack(
                                       attacker: IPlayer,
                                       defender: IPlayer,
                                       attackerHand: IHandCardsQueue,
                                       defenderHand: IHandCardsQueue,
                                       gameCards: IGameCards,
                                       attackingCard1: Option[ICard],
                                       attackingCard2: Option[ICard],
                                       defenderCard: Option[ICard],
                                       attackValue: Int,
                                       revertStrategy: IRevertStrategy,
                                       roles: IRoles
                                     ): (IGameCards, IRoles, List[ObservableEvent]) = {

    val revertedDefenderCard = revertStrategy.revertCard(defenderCard)
    val defenderValue = defenderCard.map(_.valueToInt).getOrElse(
      throw new NoSuchElementException("Defender card not found.")
    )

    if (attackValue > defenderValue) {
      val (updatedGameCards, resultEvent) = attackerWins(
        attackerHand,
        gameCards,
        attacker,
        defender,
        attackingCard1,
        attackingCard2,
        revertedDefenderCard
      )

      val newGameCards = updatedGameCards
        .removeDefenderCard(defender, defenderCard)
        .removeDefenderCard(defender, revertedDefenderCard)

      (newGameCards, roles, List(resultEvent, GameActionEvent.DoubleAttack))

    } else if (attackValue < defenderValue) {
      val (updatedGameCards, resultEvent) = defenderWins(
        defenderHand,
        gameCards,
        attacker,
        defender,
        attackingCard1,
        attackingCard2,
        revertedDefenderCard
      )

      val newGameCards = updatedGameCards
        .removeDefenderCard(defender, defenderCard)
        .removeDefenderCard(defender, revertedDefenderCard)
        .refillDefenderField(defender)

      val updatedRoles = roles.switchRoles()

      (newGameCards, updatedRoles, List(resultEvent, GameActionEvent.DoubleAttack))

    } else {
      handleTie(
        attacker,
        defender,
        attackerHand,
        defenderHand,
        attackingCard1,
        attackingCard2,
        gameCards,
        revertStrategy,
        roles
      )
    }
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

  protected def handleTie(
                           attacker: IPlayer,
                           defender: IPlayer,
                           attackerHand: IHandCardsQueue,
                           defenderHand: IHandCardsQueue,
                           attackingCard1: Option[ICard],
                           attackingCard2: Option[ICard],
                           gameCards: IGameCards,
                           revertStrategy: IRevertStrategy,
                           roles: IRoles
                         ): (IGameCards, IRoles, List[ObservableEvent]) = {

    if (attackerHand.getHandSize > 0 && defenderHand.getHandSize > 0) {

      (attackerHand.removeLastCard(), defenderHand.removeLastCard()) match {
        case (Success((extraAttackerCard, updatedAttackerHand)),
        Success((extraDefenderCard, updatedDefenderHand))) =>

          val revertedExtraAttackerCard = revertStrategy.revertCard(Some(extraAttackerCard))
          val revertedExtraDefenderCard = revertStrategy.revertCard(Some(extraDefenderCard))

          val defenderCard = gameCards.getDefenderCard(defender, defenderIndex)
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
            val (updatedGameCards, resultEvent) = attackerWins(
              updatedAttackerHand,
              gameCards,
              attacker,
              defender,
              attackingCard1,
              attackingCard2,
              revertedExtraAttackerCard,
              revertedExtraDefenderCard
            )
            val newGameCards = updatedGameCards
              .removeDefenderCard(defender, defenderCard)
              .removeDefenderCard(defender, revertedDefenderCard)

            (newGameCards, roles, resultEvent :: events)

          } else {
            val (updatedGameCards, resultEvent) = defenderWins(
              updatedDefenderHand,
              gameCards,
              attacker,
              defender,
              attackingCard1,
              attackingCard2,
              revertedExtraAttackerCard,
              revertedExtraDefenderCard
            )
            val newGameCards = updatedGameCards
              .removeDefenderCard(defender, defenderCard)
              .removeDefenderCard(defender, revertedDefenderCard)
              .refillDefenderField(defender)

            val updatedRoles = roles.switchRoles()
            (newGameCards, updatedRoles, resultEvent :: events)
          }

        case _ =>
          val updatedRoles = roles.switchRoles()
          (gameCards, updatedRoles, List(GameActionEvent.DoubleTieComparison))
      }

    } else {
      val updatedRoles = roles.switchRoles()
      (gameCards, updatedRoles, List(GameActionEvent.DoubleTieComparison))
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

}
