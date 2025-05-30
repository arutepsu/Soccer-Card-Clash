package de.htwg.se.soccercardclash.model.playerComponent.ai.strategies


import de.htwg.se.soccercardclash.model.cardComponent.ICard
import de.htwg.se.soccercardclash.model.cardComponent.base.components.Value
import de.htwg.se.soccercardclash.model.cardComponent.dataStructure.IHandCardsQueue
import de.htwg.se.soccercardclash.model.gameComponent.IGameState
import de.htwg.se.soccercardclash.model.gameComponent.action.manager.PlayerActionManager
import de.htwg.se.soccercardclash.model.gameComponent.context.GameContext
import de.htwg.se.soccercardclash.model.gameComponent.components.{GameCards, IGameCards}
import de.htwg.se.soccercardclash.model.playerComponent.IPlayer
import de.htwg.se.soccercardclash.model.playerComponent.playerAction.PlayerActionPolicies
import de.htwg.se.soccercardclash.model.playerComponent.util.IRandomProvider
import de.htwg.se.soccercardclash.util.*
import org.mockito.Mockito.*
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.mockito.MockitoSugar

class SimpleSwapAIStrategySpec extends AnyWordSpec with Matchers with MockitoSugar {

  "SimpleSwapAIStrategy" should {

    "return NoOpAIAction if swap is not allowed" in {
      val player = mock[IPlayer]
      val dataManager = mock[IGameCards]
      val state = mock[IGameState]

      when(state.getGameCards).thenReturn(dataManager)

      val context = GameContext(state, new UndoManager)

      val strategy = new SimpleSwapAIStrategy(mock[IRandomProvider]) {
        override val playerActionManager: PlayerActionManager = {
          val pam = mock[PlayerActionManager]
          when(pam.canPerform(player, PlayerActionPolicies.Swap)).thenReturn(false)
          pam
        }
      }

      strategy.decideAction(context, player) shouldBe NoOpAIAction
    }

    "return NoOpAIAction if hand has less than 2 cards" in {
      val player = mock[IPlayer]
      val handQueue = mock[IHandCardsQueue]
      when(handQueue.toList).thenReturn(List(mock[ICard]))

      val dataManager = mock[IGameCards]
      when(dataManager.getPlayerHand(player)).thenReturn(handQueue)

      val state = mock[IGameState]
      when(state.getGameCards).thenReturn(dataManager)

      val context = GameContext(state, new UndoManager)

      val strategy = new SimpleSwapAIStrategy(mock[IRandomProvider]) {
        override val playerActionManager: PlayerActionManager = {
          val pam = mock[PlayerActionManager]
          when(pam.canPerform(player, PlayerActionPolicies.Swap)).thenReturn(true)
          pam
        }
      }

      strategy.decideAction(context, player) shouldBe NoOpAIAction
    }

    "return NoOpAIAction if first card value >= 6" in {
      val player = mock[IPlayer]
      val card = mock[ICard]
      when(card.value).thenReturn(Value.Six)

      val handQueue = mock[IHandCardsQueue]
      when(handQueue.toList).thenReturn(List(card, mock[ICard]))

      val dataManager = mock[IGameCards]
      when(dataManager.getPlayerHand(player)).thenReturn(handQueue)

      val state = mock[IGameState]
      when(state.getGameCards).thenReturn(dataManager)

      val context = GameContext(state, new UndoManager)

      val strategy = new SimpleSwapAIStrategy(mock[IRandomProvider]) {
        override val playerActionManager: PlayerActionManager = {
          val pam = mock[PlayerActionManager]
          when(pam.canPerform(player, PlayerActionPolicies.Swap)).thenReturn(true)
          pam
        }
      }

      strategy.decideAction(context, player) shouldBe NoOpAIAction
    }

    "return RegularSwapAIAction if valid and random chooses index" in {
      val player = mock[IPlayer]
      val card = mock[ICard]
      when(card.value).thenReturn(Value.Four)

      val handQueue = mock[IHandCardsQueue]
      when(handQueue.toList).thenReturn(List(card, mock[ICard], mock[ICard]))

      val dataManager = mock[IGameCards]
      when(dataManager.getPlayerHand(player)).thenReturn(handQueue)

      val state = mock[IGameState]
      when(state.getGameCards).thenReturn(dataManager)

      val context = GameContext(state, new UndoManager)

      val random = mock[IRandomProvider]
      when(random.between(0, 2)).thenReturn(1)

      val strategy = new SimpleSwapAIStrategy(random) {
        override val playerActionManager: PlayerActionManager = {
          val pam = mock[PlayerActionManager]
          when(pam.canPerform(player, PlayerActionPolicies.Swap)).thenReturn(true)
          pam
        }
      }

      strategy.decideAction(context, player) shouldBe RegularSwapAIAction(index = 1)
    }
  }
}
