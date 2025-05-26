package de.htwg.se.soccercardclash.model.gameComponent.action.strategy.attackStrategy.base
import de.htwg.se.soccercardclash.model.cardComponent.ICard
import de.htwg.se.soccercardclash.model.cardComponent.dataStructure.IHandCardsQueue
import de.htwg.se.soccercardclash.model.gameComponent.action.manager.IPlayerActionManager
import de.htwg.se.soccercardclash.model.gameComponent.state.IGameState
import de.htwg.se.soccercardclash.model.gameComponent.state.components.{IGameCards, IRoles, IScores}
import de.htwg.se.soccercardclash.model.playerComponent.IPlayer
import de.htwg.se.soccercardclash.model.gameComponent.action.strategy.boostStrategy.{IBoostManager, IRevertStrategy}
import de.htwg.se.soccercardclash.util.StateEvent
import org.mockito.Mockito.*
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.mockito.MockitoSugar
import org.mockito.ArgumentMatchers.any
import org.mockito.ArgumentMatchers.{any, eq as eqTo}

import scala.util.Success
import de.htwg.se.soccercardclash.util.*

class DoubleAttackStrategySpec extends AnyWordSpec with Matchers {

  class TestableDoubleAttackStrategy(
                                      index: Int,
                                      playerActionService: IPlayerActionManager,
                                      boostManager: IBoostManager
                                    ) extends DoubleAttackStrategy(index, playerActionService, boostManager) {

    def testProcessDefenderAttack(
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
      processDefenderAttack(
        attacker, defender,
        attackerHand, defenderHand,
        gameCards,
        attackingCard1, attackingCard2, defenderCard,
        attackValue,
        revertStrategy, roles
      )

    }
  }


  "processDefenderAttack" should {
    "return updated state when attacker wins in double attack" in {
      val attacker = mock(classOf[IPlayer])
      val defender = mock(classOf[IPlayer])
      val attackingCard1 = mock(classOf[ICard])
      val attackingCard2 = mock(classOf[ICard])
      val defenderCard = mock(classOf[ICard])
      val revertedCard = mock(classOf[ICard])
      val attackerHand = mock(classOf[IHandCardsQueue])
      val defenderHand = mock(classOf[IHandCardsQueue])
      val gameCards = mock(classOf[IGameCards])
      val updatedGameCards = mock(classOf[IGameCards])
      val finalGameCards = mock(classOf[IGameCards])
      val roles = mock(classOf[IRoles])
      val revertStrategy = mock(classOf[IRevertStrategy])
      val resultEvent = mock(classOf[ObservableEvent])
      val dummyBoostManager = mock(classOf[IBoostManager])
      val dummyPlayerActionManager = mock(classOf[IPlayerActionManager])

      // Simulate defender card revert
      when(revertStrategy.revertCard(Some(defenderCard))).thenReturn(Some(revertedCard))
      when(defenderCard.valueToInt).thenReturn(5) // defender strength
      val attackValue = 10                        // attacker total strength

      // Simulate attacker winning path
      when(attackerHand.addCard(attackingCard1)).thenReturn(attackerHand)
      when(attackerHand.addCard(attackingCard2)).thenReturn(attackerHand)
      when(attackerHand.addCard(revertedCard)).thenReturn(attackerHand)

      when(gameCards.newPlayerHand(attacker, attackerHand)).thenReturn(updatedGameCards)
      when(updatedGameCards.removeDefenderCard(defender, Some(defenderCard))).thenReturn(updatedGameCards)
      when(updatedGameCards.removeDefenderCard(defender, Some(revertedCard))).thenReturn(finalGameCards)

      val strategy = new TestableDoubleAttackStrategy(0, dummyPlayerActionManager, dummyBoostManager)
      val (newCards, newRoles, events) = strategy.testProcessDefenderAttack(
        attacker,
        defender,
        attackerHand,
        defenderHand,
        gameCards,
        Some(attackingCard1),
        Some(attackingCard2),
        Some(defenderCard),
        attackValue,
        revertStrategy,
        roles
      )

      newCards shouldBe finalGameCards
      newRoles shouldBe roles // roles unchanged on attacker win
      events should contain(GameActionEvent.DoubleAttack)
    }
  }

  class TestableDoubleAttackStrategy2(
                                      index: Int,
                                      playerActionService: IPlayerActionManager,
                                      boostManager: IBoostManager
                                    ) extends DoubleAttackStrategy(index, playerActionService, boostManager) {

    def testProcessGoalkeeperAttack(
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
      processGoalkeeperAttack(
        attacker, defender, attackerHand,
        gameCards, attackingCard1, attackingCard2, attackValue,
        revertStrategy, scores, roles
      )
    }
  }

  "processGoalkeeperAttack" should {
    "return updated state when attacker beats goalkeeper" in {
      val attacker = mock(classOf[IPlayer])
      val defender = mock(classOf[IPlayer])
      val attackingCard1 = mock(classOf[ICard])
      val attackingCard2 = mock(classOf[ICard])
      val goalkeeper = mock(classOf[ICard])
      val revertedGoalkeeper = mock(classOf[ICard])
      val attackerHand = mock(classOf[IHandCardsQueue])
      val gameCards = mock(classOf[IGameCards])
      val updatedGameCards = mock(classOf[IGameCards])
      val newGameCards = mock(classOf[IGameCards])
      val scores = mock(classOf[IScores])
      val updatedScores = mock(classOf[IScores])
      val roles = mock(classOf[IRoles])
      val updatedRoles = mock(classOf[IRoles])
      val resultEvent = mock(classOf[ObservableEvent])
      val scoreEvent = mock(classOf[ObservableEvent])

      val revertStrategy = mock(classOf[IRevertStrategy])
      val dummyBoostManager = mock(classOf[IBoostManager])
      val dummyActionManager = mock(classOf[IPlayerActionManager])

      when(gameCards.getPlayerGoalkeeper(defender)).thenReturn(Some(goalkeeper))
      when(revertStrategy.revertCard(Some(goalkeeper))).thenReturn(Some(revertedGoalkeeper))
      when(goalkeeper.valueToInt).thenReturn(3)

      val attackValue = 5

      // attackerWins → adds cards, returns updatedGameCards
      when(attackerHand.addCard(Some(attackingCard1).get)).thenReturn(attackerHand)
      when(attackerHand.addCard(Some(attackingCard2).get)).thenReturn(attackerHand)
      when(attackerHand.addCard(revertedGoalkeeper)).thenReturn(attackerHand)

      when(gameCards.newPlayerHand(attacker, attackerHand)).thenReturn(updatedGameCards)
      when(updatedGameCards.removeDefenderGoalkeeper(defender)).thenReturn(updatedGameCards)
      when(updatedGameCards.newPlayerGoalkeeper(defender, None)).thenReturn(updatedGameCards)
      when(updatedGameCards.newPlayerDefenders(defender, List.empty)).thenReturn(updatedGameCards)
      when(updatedGameCards.refillDefenderField(defender)).thenReturn(newGameCards)

      // scoring logic
      when(scores.scoreGoal(attacker)).thenReturn((updatedScores, List(scoreEvent)))
      when(roles.switchRoles()).thenReturn(updatedRoles)

      val strategy = new TestableDoubleAttackStrategy2(0, dummyActionManager, dummyBoostManager)

      val (finalCards, finalRoles, finalScores, events) = strategy.testProcessGoalkeeperAttack(
        attacker,
        defender,
        attackerHand,
        gameCards,
        attackingCard1,
        attackingCard2,
        attackValue,
        revertStrategy,
        scores,
        roles
      )

      finalCards shouldBe newGameCards
      finalRoles shouldBe updatedRoles
      finalScores shouldBe updatedScores
      events should contain allOf(GameActionEvent.DoubleAttack, scoreEvent)
    }
  }

  class TestableDoubleAttackStrategy3(
                                      index: Int,
                                      playerActionService: IPlayerActionManager,
                                      boostManager: IBoostManager
                                    ) extends DoubleAttackStrategy(index, playerActionService, boostManager) {

    def testAttackerWins(
                          hand: IHandCardsQueue,
                          gameCards: IGameCards,
                          attacker: IPlayer,
                          defender: IPlayer,
                          cards: Option[ICard]*
                        ): (IGameCards, ObservableEvent) = {
      attackerWins(hand, gameCards, attacker, defender, cards: _*)
    }

    def testDefenderWins(
                          hand: IHandCardsQueue,
                          gameCards: IGameCards,
                          attacker: IPlayer,
                          defender: IPlayer,
                          cards: Option[ICard]*
                        ): (IGameCards, ObservableEvent) = {
      defenderWins(hand, gameCards, attacker, defender, cards: _*)
    }

  }
  "attackerWins" should {
    "add all non-empty cards to hand and return updated game state with success event" in {
      val attacker = mock(classOf[IPlayer])
      val defender = mock(classOf[IPlayer])
      val gameCards = mock(classOf[IGameCards])

      val hand = mock(classOf[IHandCardsQueue])
      val c1 = mock(classOf[ICard])
      val c2 = mock(classOf[ICard])

      val h1 = mock(classOf[IHandCardsQueue])
      val h2 = mock(classOf[IHandCardsQueue])
      val updatedGameCards = mock(classOf[IGameCards])

      // Chain card additions
      when(hand.addCard(c1)).thenReturn(h1)
      when(h1.addCard(c2)).thenReturn(h2)

      // Assign new player hand
      when(gameCards.newPlayerHand(attacker, h2)).thenReturn(updatedGameCards)

      val dummyActionManager = mock(classOf[IPlayerActionManager])
      val dummyBoostManager = mock(classOf[IBoostManager])
      val strategy = new TestableDoubleAttackStrategy3(0, dummyActionManager, dummyBoostManager)

      val (resultGameCards, event) = strategy.testAttackerWins(hand, gameCards, attacker, defender, Some(c1), Some(c2), None)

      resultGameCards shouldBe updatedGameCards
      event shouldBe StateEvent.AttackResultEvent(attacker, defender, attackSuccess = true)
    }
  }
  "defenderWins" should {
    "add all non-empty cards to hand and return updated game state with fail event" in {
      val attacker = mock(classOf[IPlayer])
      val defender = mock(classOf[IPlayer])
      val gameCards = mock(classOf[IGameCards])

      val hand = mock(classOf[IHandCardsQueue])
      val c1 = mock(classOf[ICard])
      val c2 = mock(classOf[ICard])

      val h1 = mock(classOf[IHandCardsQueue])
      val h2 = mock(classOf[IHandCardsQueue])
      val updatedGameCards = mock(classOf[IGameCards])

      // Chain card additions
      when(hand.addCard(c1)).thenReturn(h1)
      when(h1.addCard(c2)).thenReturn(h2)

      // Assign new player hand
      when(gameCards.newPlayerHand(defender, h2)).thenReturn(updatedGameCards)

      val dummyActionManager = mock(classOf[IPlayerActionManager])
      val dummyBoostManager = mock(classOf[IBoostManager])
      val strategy = new TestableDoubleAttackStrategy3(0, dummyActionManager, dummyBoostManager)

      val (resultGameCards, event) = strategy.testDefenderWins(hand, gameCards, attacker, defender, Some(c1), Some(c2), None)

      resultGameCards shouldBe updatedGameCards
      event shouldBe StateEvent.AttackResultEvent(attacker, defender, attackSuccess = false)
    }
  }

  class TestableDoubleAttackStrategy4(
                                       index: Int,
                                       playerActionService: IPlayerActionManager,
                                       boostManager: IBoostManager
                                     ) extends DoubleAttackStrategy(index, playerActionService, boostManager) {

    def testHandleTie(
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
      handleTie(attacker, defender, attackerHand, defenderHand, attackingCard1, attackingCard2, gameCards, revertStrategy, roles)
    }

  }

  "handleTie" should {
    "resolve tie by comparing extra cards, attacker wins" in {
      val attacker = mock(classOf[IPlayer])
      val defender = mock(classOf[IPlayer])
      val attackerHand = mock(classOf[IHandCardsQueue])
      val defenderHand = mock(classOf[IHandCardsQueue])
      val updatedAttackerHand = mock(classOf[IHandCardsQueue])
      val updatedDefenderHand = mock(classOf[IHandCardsQueue])

      val attackingCard1 = mock(classOf[ICard])
      val attackingCard2 = mock(classOf[ICard])
      val extraAttackerCard = mock(classOf[ICard])
      val extraDefenderCard = mock(classOf[ICard])
      val revertedExtraAttackerCard = mock(classOf[ICard])
      val revertedExtraDefenderCard = mock(classOf[ICard])
      val defenderCard = mock(classOf[ICard])
      val revertedDefenderCard = mock(classOf[ICard])

      val gameCards = mock(classOf[IGameCards])
      val updatedGameCards = mock(classOf[IGameCards])
      val newGameCards = mock(classOf[IGameCards])
      val revertStrategy = mock(classOf[IRevertStrategy])
      val roles = mock(classOf[IRoles])
      val resultEvent = mock(classOf[ObservableEvent])

      val dummyBoostManager = mock(classOf[IBoostManager])
      val dummyActionManager = mock(classOf[IPlayerActionManager])
      val strategy = new TestableDoubleAttackStrategy4(0, dummyActionManager, dummyBoostManager)

      // Enough cards
      when(attackerHand.getHandSize).thenReturn(1)
      when(defenderHand.getHandSize).thenReturn(1)

      // removeLastCard
      when(attackerHand.removeLastCard()).thenReturn(Success((extraAttackerCard, updatedAttackerHand)))
      when(defenderHand.removeLastCard()).thenReturn(Success((extraDefenderCard, updatedDefenderHand)))

      // Revert cards
      when(revertStrategy.revertCard(Some(extraAttackerCard))).thenReturn(Some(revertedExtraAttackerCard))
      when(revertStrategy.revertCard(Some(extraDefenderCard))).thenReturn(Some(revertedExtraDefenderCard))
      when(gameCards.getDefenderCard(defender, 0)).thenReturn(Some(defenderCard))
      when(revertStrategy.revertCard(Some(defenderCard))).thenReturn(Some(revertedDefenderCard))

      // Attacker wins tiebreaker
      when(extraAttackerCard.compare(extraDefenderCard)).thenReturn(1)

      // attackerWins logic
      val h1 = mock(classOf[IHandCardsQueue])
      val h2 = mock(classOf[IHandCardsQueue])
      val h3 = mock(classOf[IHandCardsQueue])

      when(updatedAttackerHand.addCard(Some(attackingCard1).get)).thenReturn(h1)
      when(h1.addCard(Some(attackingCard2).get)).thenReturn(h2)
      when(h2.addCard(revertedExtraAttackerCard)).thenReturn(h3)
      when(h3.addCard(revertedExtraDefenderCard)).thenReturn(h3)

      when(gameCards.newPlayerHand(attacker, h3)).thenReturn(updatedGameCards)
      when(updatedGameCards.removeDefenderCard(defender, Some(defenderCard))).thenReturn(updatedGameCards)
      when(updatedGameCards.removeDefenderCard(defender, Some(revertedDefenderCard))).thenReturn(newGameCards)

      val (finalCards, finalRoles, events) = strategy.testHandleTie(
        attacker,
        defender,
        attackerHand,
        defenderHand,
        Some(attackingCard1),
        Some(attackingCard2),
        gameCards,
        revertStrategy,
        roles
      )

      finalCards shouldBe newGameCards
      finalRoles shouldBe roles
      events.exists(_ == resultEvent) shouldBe false // This can vary based on how you mock resultEvent
      events.exists(_.isInstanceOf[StateEvent.AttackResultEvent]) shouldBe true
      events.exists(_.isInstanceOf[StateEvent.DoubleTieComparisonEvent]) shouldBe true

    }
  }

}
