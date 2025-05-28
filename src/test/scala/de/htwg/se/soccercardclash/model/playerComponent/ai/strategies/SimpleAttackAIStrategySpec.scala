package de.htwg.se.soccercardclash.model.playerComponent.ai.strategies

import de.htwg.se.soccercardclash.model.cardComponent.ICard
import de.htwg.se.soccercardclash.model.cardComponent.dataStructure.IHandCardsQueue
import de.htwg.se.soccercardclash.model.gameComponent.context.GameContext
import de.htwg.se.soccercardclash.model.gameComponent.state.*
import de.htwg.se.soccercardclash.model.gameComponent.state.components.{IGameCards, IRoles}
import de.htwg.se.soccercardclash.model.playerComponent.IPlayer
import de.htwg.se.soccercardclash.util.{NoOpAIAction, SingleAttackAIAction, UndoManager}
import org.mockito.Mockito.*
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.mockito.MockitoSugar
import scalafx.scene.input.KeyCode.GameC

class SimpleAttackAIStrategySpec extends AnyWordSpec with Matchers with MockitoSugar {

  "SimpleAttackAIStrategy" should {

    "return NoOpAIAction if attacker has no cards" in {
      val player = mock[IPlayer]
      val defender = mock[IPlayer]

      val roles = mock[IRoles]
      when(roles.attacker).thenReturn(player)
      when(roles.defender).thenReturn(defender)

      val hand = mock[IHandCardsQueue]
      when(hand.toList).thenReturn(Nil)

      val dataManager = mock[IGameCards]
      when(dataManager.getPlayerHand(player)).thenReturn(hand)

      val state = mock[IGameState]
      when(state.getRoles).thenReturn(roles)
      when(state.getGameCards).thenReturn(dataManager)

      val ctx = GameContext(state, undoManager = new UndoManager)
      val strategy = new SimpleAttackAIStrategy

      strategy.decideAction(ctx, player) shouldBe NoOpAIAction
    }

    "return SingleAttackAIAction with index if defender has field cards" in {
      val player = mock[IPlayer]
      val defender = mock[IPlayer]

      val roles = mock[IRoles]
      when(roles.attacker).thenReturn(player)
      when(roles.defender).thenReturn(defender)

      val hand = mock[IHandCardsQueue]
      when(hand.toList).thenReturn(List(mock[ICard]))

      val defenders = List(None, Some(mock[ICard]), None) // should pick index 1

      val dataManager = mock[IGameCards]
      when(dataManager.getPlayerHand(player)).thenReturn(hand)
      when(dataManager.getPlayerDefenders(defender)).thenReturn(defenders)

      val state = mock[IGameState]
      when(state.getRoles).thenReturn(roles)
      when(state.getGameCards).thenReturn(dataManager)

      val ctx = GameContext(state, undoManager = new UndoManager)
      val strategy = new SimpleAttackAIStrategy

      strategy.decideAction(ctx, player) shouldBe SingleAttackAIAction(defenderIndex = 1)
    }

    "return SingleAttackAIAction(-1) if no defenders but goalkeeper is present" in {
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
      when(dataManager.getPlayerGoalkeeper(defender)).thenReturn(Some(mock[ICard]))

      val state = mock[IGameState]
      when(state.getRoles).thenReturn(roles)
      when(state.getGameCards).thenReturn(dataManager)

      val ctx = GameContext(state, undoManager = new UndoManager)
      val strategy = new SimpleAttackAIStrategy

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

      val ctx = GameContext(state, undoManager = new UndoManager)
      val strategy = new SimpleAttackAIStrategy

      strategy.decideAction(ctx, player) shouldBe NoOpAIAction
    }
  }
}
