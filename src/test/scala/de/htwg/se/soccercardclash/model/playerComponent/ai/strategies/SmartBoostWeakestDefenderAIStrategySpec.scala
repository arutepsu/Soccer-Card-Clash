package de.htwg.se.soccercardclash.model.playerComponent.ai.strategies

import de.htwg.se.soccercardclash.model.cardComponent.ICard
import de.htwg.se.soccercardclash.model.cardComponent.base.types.BoostedCard
import de.htwg.se.soccercardclash.model.cardComponent.dataStructure.IHandCardsQueue
import de.htwg.se.soccercardclash.model.gameComponent.action.manager.PlayerActionManager
import de.htwg.se.soccercardclash.model.gameComponent.context.GameContext
import de.htwg.se.soccercardclash.model.gameComponent.base.*
import de.htwg.se.soccercardclash.model.gameComponent.components.{IGameCards, IRoles}
import de.htwg.se.soccercardclash.model.playerComponent.IPlayer
import de.htwg.se.soccercardclash.model.playerComponent.playerAction.PlayerActionPolicies
import de.htwg.se.soccercardclash.model.playerComponent.util.IRandomProvider
import de.htwg.se.soccercardclash.util.{BoostAIAction, DefenderZone, GoalkeeperZone, NoOpAIAction, SingleAttackAIAction, UndoManager}
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.mockito.MockitoSugar
import org.mockito.Mockito.*
import de.htwg.se.soccercardclash.model.cardComponent.base.components.Value
import de.htwg.se.soccercardclash.model.gameComponent.IGameState

class SmartBoostWeakestDefenderAIStrategySpec extends AnyWordSpec with Matchers with MockitoSugar {

  def mockCard(intValue: Int, boosted: Boolean = false): ICard = {
    val card = if (boosted) mock[BoostedCard] else mock[ICard]
    val maybeValue = Value.fromInt(intValue)
    require(maybeValue.isDefined, s"Invalid Value int: $intValue")
    when(card.value).thenReturn(maybeValue.get)
    card
  }

  def setup(
             player: IPlayer,
             defenders: List[Option[ICard]],
             goalkeeper: Option[ICard],
             canBoost: Boolean
           ): (GameContext, SmartBoostWeakestDefenderAIStrategy) = {
    val data = mock[IGameCards]
    when(data.getPlayerDefenders(player)).thenReturn(defenders)
    when(data.getPlayerGoalkeeper(player)).thenReturn(goalkeeper)

    val state = mock[IGameState]
    when(state.getGameCards).thenReturn(data)

    val ctx = GameContext(state, new UndoManager)

    val strategy = new SmartBoostWeakestDefenderAIStrategy {
      override val playerActionManager = {
        val pam = mock[PlayerActionManager]
        when(pam.canPerform(player, PlayerActionPolicies.Boost)).thenReturn(canBoost)
        pam
      }
    }

    (ctx, strategy)
  }

  "SmartBoostWeakestDefenderAIStrategy" should {

    "return NoOpAIAction if boost is not allowed" in {
      val player = mock[IPlayer]
      val (ctx, strategy) = setup(player, Nil, None, canBoost = false)

      strategy.decideAction(ctx, player) shouldBe NoOpAIAction
    }

    "boost the only unboosted defender" in {
      val player = mock[IPlayer]
      val defenderCard = mockCard(5)
      val (ctx, strategy) = setup(player, List(Some(defenderCard)), None, canBoost = true)

      strategy.decideAction(ctx, player) shouldBe BoostAIAction(0, DefenderZone)
    }

    "boost the unboosted goalkeeper if no defenders" in {
      val player = mock[IPlayer]
      val gk = mockCard(6)
      val (ctx, strategy) = setup(player, Nil, Some(gk), canBoost = true)

      strategy.decideAction(ctx, player) shouldBe BoostAIAction(-1, GoalkeeperZone)
    }

    "boost the weakest unboosted card among defenders and goalkeeper" in {
      val player = mock[IPlayer]
      val defender1 = mockCard(9)
      val defender2 = mockCard(5)
      val gk = mockCard(6)
      val (ctx, strategy) = setup(player, List(Some(defender1), Some(defender2)), Some(gk), canBoost = true)

      strategy.decideAction(ctx, player) shouldBe BoostAIAction(1, DefenderZone) // index 1 has value 5
    }

    "return NoOpAIAction if all cards are boosted" in {
      val player = mock[IPlayer]
      val defender1 = mockCard(7, boosted = true)
      val defender2 = mockCard(8, boosted = true)
      val gk = mockCard(9, boosted = true)

      val (ctx, strategy) = setup(player, List(Some(defender1), Some(defender2)), Some(gk), canBoost = true)

      strategy.decideAction(ctx, player) shouldBe NoOpAIAction
    }
  }
}