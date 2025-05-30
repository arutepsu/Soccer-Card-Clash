package de.htwg.se.soccercardclash.model.gameComponent.action.strategy.attack.base

import de.htwg.se.soccercardclash.model.cardComponent.ICard
import de.htwg.se.soccercardclash.model.cardComponent.dataStructure.IHandCardsQueue
import de.htwg.se.soccercardclash.model.gameComponent.IGameState
import de.htwg.se.soccercardclash.model.gameComponent.action.strategy.attack.SingleAttackStrategy
import de.htwg.se.soccercardclash.model.gameComponent.state.components.{IGameCards, IRoles, IScores}
import de.htwg.se.soccercardclash.model.playerComponent.IPlayer
import de.htwg.se.soccercardclash.model.gameComponent.action.strategy.boost.IBoostManager
import de.htwg.se.soccercardclash.model.gameComponent.action.strategy.boost.revert.IRevertStrategy
import de.htwg.se.soccercardclash.util.StateEvent
import org.mockito.Mockito.*
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.mockito.MockitoSugar
import org.mockito.ArgumentMatchers.any
import org.mockito.ArgumentMatchers.{any, eq as eqTo}

import scala.util.Success
import de.htwg.se.soccercardclash.util.*

class SingleAttackStrategySpec extends AnyWordSpec with Matchers {

  class TestableSingleAttackStrategy(index: Int, boostManager: IBoostManager)
    extends SingleAttackStrategy(index, boostManager) {
    def testProcessDefenderAttack(
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
      processDefenderAttack(attacker, defender, attackerHand, defenderHand, attackingCard, defenderCard, gameCards, revertStrategy, roles)
    }
  }

  "processDefenderAttack" should {
    "return updated state when attacker wins" in {
      val attacker = mock(classOf[IPlayer])
      val defender = mock(classOf[IPlayer])
      val attackingCard = mock(classOf[ICard])
      val defenderCard = mock(classOf[ICard])
      val revertedCard = mock(classOf[ICard])
      val attackerHand = mock(classOf[IHandCardsQueue])
      val h1 = mock(classOf[IHandCardsQueue])
      val h2 = mock(classOf[IHandCardsQueue])
      val defenderHand = mock(classOf[IHandCardsQueue])
      val gameCards = mock(classOf[IGameCards])
      val finalGameCards = mock(classOf[IGameCards])
      val roles = mock(classOf[IRoles])
      val revertStrategy = mock(classOf[IRevertStrategy])
      val resultEvent = mock(classOf[ObservableEvent])

      // revert defender card
      when(revertStrategy.revertCard(Some(defenderCard))).thenReturn(Some(revertedCard))

      // compare returns attacker wins (> 0)
      when(attackingCard.compare(defenderCard)).thenReturn(1)

      // attacker hand after adding attacking card and reverted card
      when(attackerHand.addCard(attackingCard)).thenReturn(h1)
      when(h1.addCard(revertedCard)).thenReturn(h2)

      // new player hand set in game cards
      when(gameCards.newPlayerHand(attacker, h2)).thenReturn(gameCards)

      // remove both defender and reverted card
      when(gameCards.removeDefenderCard(defender, Some(defenderCard))).thenReturn(gameCards)
      when(gameCards.removeDefenderCard(defender, Some(revertedCard))).thenReturn(finalGameCards)

      val dummyBoostManager = mock(classOf[IBoostManager])
      val strategy: TestableSingleAttackStrategy = new TestableSingleAttackStrategy(0, dummyBoostManager)
      val (newCards, newRoles, events) = strategy.testProcessDefenderAttack(
        attacker,
        defender,
        attackerHand,
        defenderHand,
        attackingCard,
        Some(defenderCard),
        gameCards,
        revertStrategy,
        roles
      )


      newCards shouldBe finalGameCards
      newRoles shouldBe roles // unchanged
      events should contain(GameActionEvent.RegularAttack)
    }
  }

  class TestableSingleAttackStrategy2(
                                      index: Int,
                                      boostManager: IBoostManager
                                    ) extends SingleAttackStrategy(index, boostManager) {

    def testProcessGoalkeeperAttack(
                                     attacker: IPlayer,
                                     defender: IPlayer,
                                     attackerHand: IHandCardsQueue,
                                     gameCards: IGameCards,
                                     attackingCard: ICard,
                                     revertStrategy: IRevertStrategy,
                                     scores: IScores,
                                     roles: IRoles
                                   ): (IGameCards, IRoles, IScores, List[ObservableEvent]) = {
      processGoalkeeperAttack(
        attacker,
        defender,
        attackerHand,
        gameCards,
        attackingCard,
        revertStrategy,
        scores,
        roles
      )
    }

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

    def testHandleTie(
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
      handleTie(attacker, defender, attackerHand, defenderHand, attackingCard, defenderCard, gameCards, revertStrategy, roles)
    }


  }
  "handleTie" should {
    "handle tie and attacker wins the tiebreaker" in {
      val attacker = mock(classOf[IPlayer])
      val defender = mock(classOf[IPlayer])
      val attackingCard = mock(classOf[ICard])
      val defenderCard = mock(classOf[ICard])
      val revertedDefenderCard = mock(classOf[ICard])
      val extraAttackerCard = mock(classOf[ICard])
      val extraDefenderCard = mock(classOf[ICard])

      val attackerHand = mock(classOf[IHandCardsQueue])
      val defenderHand = mock(classOf[IHandCardsQueue])
      val updatedAttackerHand = mock(classOf[IHandCardsQueue])
      val updatedDefenderHand = mock(classOf[IHandCardsQueue])
      val gameCards = mock(classOf[IGameCards])
      val updatedGameCards = mock(classOf[IGameCards])
      val newGameCards = mock(classOf[IGameCards])
      val revertStrategy = mock(classOf[IRevertStrategy])
      val roles = mock(classOf[IRoles])

      // Setup revert logic
      when(revertStrategy.revertCard(Some(defenderCard))).thenReturn(Some(revertedDefenderCard))

      // Mock hand removals
      when(attackerHand.removeLastCard()).thenReturn(Success((extraAttackerCard, updatedAttackerHand)))
      when(defenderHand.removeLastCard()).thenReturn(Success((extraDefenderCard, updatedDefenderHand)))
      
      when(extraAttackerCard.compare(extraDefenderCard)).thenReturn(1)

      val h1 = mock(classOf[IHandCardsQueue])
      val h2 = mock(classOf[IHandCardsQueue])
      val h3 = mock(classOf[IHandCardsQueue])
      val h4 = mock(classOf[IHandCardsQueue])

      when(updatedAttackerHand.addCard(attackingCard)).thenReturn(h1)
      when(h1.addCard(revertedDefenderCard)).thenReturn(h2)
      when(h2.addCard(extraAttackerCard)).thenReturn(h3)
      when(h3.addCard(extraDefenderCard)).thenReturn(h4)

      when(gameCards.newPlayerHand(attacker, h4)).thenReturn(updatedGameCards)
      when(updatedGameCards.removeDefenderCard(defender, Some(defenderCard))).thenReturn(updatedGameCards)
      when(updatedGameCards.removeDefenderCard(defender, Some(revertedDefenderCard))).thenReturn(newGameCards)

      val dummyBoostManager = mock(classOf[IBoostManager])
      val strategy = new TestableSingleAttackStrategy2(0, dummyBoostManager)

      val (resultCards, resultRoles, events) = strategy.testHandleTie(
        attacker,
        defender,
        attackerHand,
        defenderHand,
        attackingCard,
        Some(defenderCard),
        gameCards,
        revertStrategy,
        roles
      )

      resultCards shouldBe newGameCards
      resultRoles shouldBe roles // no switch
      events.exists(_.isInstanceOf[StateEvent.TieComparisonEvent]) shouldBe true
    }
  }

  "attackerWins" should {
    "add all non-empty cards to the attacker hand and return updated game state" in {
      val attacker = mock(classOf[IPlayer])
      val defender = mock(classOf[IPlayer])
      val gameCards = mock(classOf[IGameCards])

      val hand = mock(classOf[IHandCardsQueue])
      val c1 = mock(classOf[ICard])
      val c2 = mock(classOf[ICard])
      val h1 = mock(classOf[IHandCardsQueue])
      val h2 = mock(classOf[IHandCardsQueue])
      val updatedGameCards = mock(classOf[IGameCards])

      // Add cards step-by-step
      when(hand.addCard(c1)).thenReturn(h1)
      when(h1.addCard(c2)).thenReturn(h2)

      // Updated hand set into game cards
      when(gameCards.newPlayerHand(attacker, h2)).thenReturn(updatedGameCards)

      val dummyBoostManager = mock(classOf[IBoostManager])
      val strategy = new TestableSingleAttackStrategy2(0, dummyBoostManager)

      val (resultCards, event) = strategy.testAttackerWins(hand, gameCards, attacker, defender, Some(c1), Some(c2), None)

      resultCards shouldBe updatedGameCards
      event shouldBe StateEvent.AttackResultEvent(attacker, defender, attackSuccess = true)
    }
  }

  "processGoalkeeperAttack" should {
    "handle a successful attack against the goalkeeper" in {
      val attacker = mock(classOf[IPlayer])
      val defender = mock(classOf[IPlayer])
      val attackingCard = mock(classOf[ICard])
      val goalkeeper = mock(classOf[ICard])
      val revertedGoalkeeper = mock(classOf[ICard])
      val attackerHand = mock(classOf[IHandCardsQueue])
      val gameCards = mock(classOf[IGameCards])
      val updatedGameCards = mock(classOf[IGameCards])
      val finalGameCards = mock(classOf[IGameCards])
      val scores = mock(classOf[IScores])
      val updatedScores = mock(classOf[IScores])
      val roles = mock(classOf[IRoles])
      val updatedRoles = mock(classOf[IRoles])
      val resultEvent = mock(classOf[ObservableEvent])
      val scoreEvent = mock(classOf[ObservableEvent])
      val revertStrategy = mock(classOf[IRevertStrategy])

      // Return goalkeeper
      when(gameCards.getPlayerGoalkeeper(defender)).thenReturn(Some(goalkeeper))

      // Compare: attacker wins
      when(attackingCard.compare(goalkeeper)).thenReturn(1)

      // Revert strategy returns a card
      when(revertStrategy.revertCard(Some(goalkeeper))).thenReturn(Some(revertedGoalkeeper))

      // attackerWins chain
      val h1 = mock(classOf[IHandCardsQueue])
      val h2 = mock(classOf[IHandCardsQueue])
      when(attackerHand.addCard(attackingCard)).thenReturn(h1)
      when(h1.addCard(revertedGoalkeeper)).thenReturn(h2)
      when(gameCards.newPlayerHand(attacker, h2)).thenReturn(updatedGameCards)

      // Update game cards after winning
      when(updatedGameCards.removeDefenderGoalkeeper(defender)).thenReturn(updatedGameCards)
      when(updatedGameCards.newPlayerGoalkeeper(defender, None)).thenReturn(updatedGameCards)
      when(updatedGameCards.newPlayerDefenders(defender, List.fill(3)(None))).thenReturn(updatedGameCards)
      when(updatedGameCards.refillDefenderField(defender)).thenReturn(finalGameCards)

      // Scoring
      when(scores.scoreGoal(attacker)).thenReturn((updatedScores, List(scoreEvent)))
      when(roles.switchRoles()).thenReturn(updatedRoles)

      val dummyBoostManager = mock(classOf[IBoostManager])
      val strategy = new TestableSingleAttackStrategy2(0, dummyBoostManager)

      val (resultCards, resultRoles, resultScores, events) = strategy.testProcessGoalkeeperAttack(
        attacker,
        defender,
        attackerHand,
        gameCards,
        attackingCard,
        revertStrategy,
        scores,
        roles
      )

      resultCards shouldBe finalGameCards
      resultRoles shouldBe updatedRoles
      resultScores shouldBe updatedScores
      events should contain(GameActionEvent.RegularAttack)
      events.exists(_.isInstanceOf[StateEvent.AttackResultEvent]) shouldBe true
    }
  }

}