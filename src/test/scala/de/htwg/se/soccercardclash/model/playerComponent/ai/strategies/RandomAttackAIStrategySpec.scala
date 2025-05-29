package de.htwg.se.soccercardclash.model.playerComponent.ai.strategies

import de.htwg.se.soccercardclash.model.cardComponent.ICard
import de.htwg.se.soccercardclash.model.cardComponent.dataStructure.IHandCardsQueue
import de.htwg.se.soccercardclash.model.gameComponent.context.GameContext
import de.htwg.se.soccercardclash.model.gameComponent.state.*
import de.htwg.se.soccercardclash.model.gameComponent.state.components.{IGameCards, IRoles}
import de.htwg.se.soccercardclash.model.playerComponent.IPlayer
import de.htwg.se.soccercardclash.model.playerComponent.util.IRandomProvider
import de.htwg.se.soccercardclash.util.{NoOpAIAction, SingleAttackAIAction, UndoManager}
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.mockito.MockitoSugar
import org.mockito.Mockito.*

class RandomAttackAIStrategySpec extends AnyWordSpec with Matchers with MockitoSugar {

  "RandomAttackAIStrategy" should {

    "return NoOpAIAction if attacker has no cards" in {
      val player = mock[IPlayer]
      val defender = mock[IPlayer]

      val roles = mock[IRoles]
      when(roles.attacker).thenReturn(player)
      when(roles.defender).thenReturn(defender)

      val emptyHand = mock[IHandCardsQueue]
      when(emptyHand.toList).thenReturn(Nil)

      val dataManager = mock[IGameCards]
      when(dataManager.getPlayerHand(player)).thenReturn(emptyHand)

      val state = mock[IGameState]
      when(state.getRoles).thenReturn(roles)
      when(state.getGameCards).thenReturn(dataManager)

      val ctx = GameContext(state, undoManager = mock[UndoManager])
      val strategy = new RandomAttackAIStrategy(mock[IRandomProvider])

      strategy.decideAction(ctx, player) shouldBe NoOpAIAction
    }

    "return SingleAttackAIAction with a valid defender index if defenders are present" in {
      val player = mock[IPlayer]
      val defender = mock[IPlayer]

      val roles = mock[IRoles]
      when(roles.attacker).thenReturn(player)
      when(roles.defender).thenReturn(defender)

      val hand = mock[IHandCardsQueue]
      when(hand.toList).thenReturn(List(mock[ICard]))

      val dataManager = mock[IGameCards]
      when(dataManager.getPlayerHand(player)).thenReturn(hand)
      when(dataManager.getPlayerDefenders(defender)).thenReturn(List(Some(mock[ICard]), None, Some(mock[ICard])))

      val state = mock[IGameState]
      when(state.getRoles).thenReturn(roles)
      when(state.getGameCards).thenReturn(dataManager)

      val ctx = GameContext(state, undoManager = mock[UndoManager])

      val random = mock[IRandomProvider]
      when(random.nextInt(2)).thenReturn(1) // chooses index 2 (second in filtered: 0 and 2)

      val strategy = new RandomAttackAIStrategy(random)
      strategy.decideAction(ctx, player) shouldBe SingleAttackAIAction(defenderIndex = 2)
    }

    "return SingleAttackAIAction(-1) if no defenders but goalkeeper exists" in {
      val player = mock[IPlayer]
      val defender = mock[IPlayer]

      val roles = mock[IRoles]
      when(roles.attacker).thenReturn(player)
      when(roles.defender).thenReturn(defender)

      val hand = mock[IHandCardsQueue]
      when(hand.toList).thenReturn(List(mock[ICard]))

      val dataManager = mock[IGameCards]
      when(dataManager.getPlayerHand(player)).thenReturn(hand)
      when(dataManager.getPlayerDefenders(defender)).thenReturn(List(None, None, None))
      when(dataManager.getPlayerGoalkeeper(defender)).thenReturn(Some(mock[ICard]))

      val state = mock[IGameState]
      when(state.getRoles).thenReturn(roles)
      when(state.getGameCards).thenReturn(dataManager)

      val ctx = GameContext(state, undoManager = mock[UndoManager])
      val strategy = new RandomAttackAIStrategy(mock[IRandomProvider])

      strategy.decideAction(ctx, player) shouldBe SingleAttackAIAction(defenderIndex = -1)
    }

    "return NoOpAIAction if no defenders and no goalkeeper" in {
      val player = mock[IPlayer]
      val defender = mock[IPlayer]

      val roles = mock[IRoles]
      when(roles.attacker).thenReturn(player)
      when(roles.defender).thenReturn(defender)

      val hand = mock[IHandCardsQueue]
      when(hand.toList).thenReturn(List(mock[ICard]))

      val dataManager = mock[IGameCards]
      when(dataManager.getPlayerHand(player)).thenReturn(hand)
      when(dataManager.getPlayerDefenders(defender)).thenReturn(List(None, None))
      when(dataManager.getPlayerGoalkeeper(defender)).thenReturn(None)

      val state = mock[IGameState]
      when(state.getRoles).thenReturn(roles)
      when(state.getGameCards).thenReturn(dataManager)

      val ctx = GameContext(state, undoManager = mock[UndoManager])
      val strategy = new RandomAttackAIStrategy(mock[IRandomProvider])

      strategy.decideAction(ctx, player) shouldBe NoOpAIAction
    }
  }
}