package de.htwg.se.soccercardclash.model.playerComponent.ai.strategies

import de.htwg.se.soccercardclash.model.cardComponent.ICard
import de.htwg.se.soccercardclash.model.cardComponent.base.components.Value
import de.htwg.se.soccercardclash.model.cardComponent.base.types.BoostedCard
import de.htwg.se.soccercardclash.model.cardComponent.dataStructure.IHandCardsQueue
import de.htwg.se.soccercardclash.model.gameComponent.action.manager.PlayerActionManager
import de.htwg.se.soccercardclash.model.gameComponent.context.GameContext
import de.htwg.se.soccercardclash.model.gameComponent.state.components.*
import de.htwg.se.soccercardclash.model.gameComponent.state.IGameState
import de.htwg.se.soccercardclash.model.playerComponent.IPlayer
import de.htwg.se.soccercardclash.model.playerComponent.playerAction.PlayerActionPolicies
import de.htwg.se.soccercardclash.util.*
import org.mockito.Mockito.*
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.mockito.MockitoSugar

class SmartAggressiveAttackAIStrategySpec extends AnyWordSpec with Matchers with MockitoSugar {

  "SmartAggressiveAttackAIStrategy" should {

    def mockCard(intValue: Int, boosted: Boolean = false): ICard = {
      val card = if (boosted) mock[BoostedCard] else mock[ICard]
      val maybeValue = Value.fromInt(intValue)
      require(maybeValue.isDefined, s"Invalid Value int: $intValue")
      when(card.value).thenReturn(maybeValue.get)
      card
    }


    def setup(
               attacker: IPlayer,
               defender: IPlayer,
               hand: List[ICard],
               defenders: List[Option[ICard]],
               goalkeeper: Option[ICard] = None,
               canDouble: Boolean = false,
               canBoost: Boolean = false
             ): (GameContext, SmartAggressiveAttackAIStrategy) = { // ✅ fix here
      val roles = mock[IRoles]
      when(roles.attacker).thenReturn(attacker)
      when(roles.defender).thenReturn(defender)

      val handQueue = mock[IHandCardsQueue]
      when(handQueue.toList).thenReturn(hand)

      val data = mock[IGameCards]
      when(data.getPlayerHand(attacker)).thenReturn(handQueue)
      when(data.getPlayerDefenders(defender)).thenReturn(defenders)
      when(data.getPlayerGoalkeeper(defender)).thenReturn(goalkeeper)

      val state = mock[IGameState]
      when(state.getRoles).thenReturn(roles)
      when(state.getGameCards).thenReturn(data)

      val ctx = GameContext(state, new UndoManager)

      val strategy = new SmartAggressiveAttackAIStrategy {
        override val playerActionManager = {
          val pam = mock[PlayerActionManager]
          when(pam.canPerform(attacker, PlayerActionPolicies.DoubleAttack)).thenReturn(canDouble)
          when(pam.canPerform(attacker, PlayerActionPolicies.Boost)).thenReturn(canBoost)
          pam
        }
      }

      (ctx, strategy)
    }


    "return NoOpAIAction if player is not attacker" in {
      val attacker = mock[IPlayer]
      val notAttacker = mock[IPlayer]

      val roles = mock[IRoles]
      when(roles.attacker).thenReturn(attacker)

      val state = mock[IGameState]
      when(state.getRoles).thenReturn(roles)

      val ctx = GameContext(state, new UndoManager)
      val strategy = new SmartAggressiveAttackAIStrategy

      strategy.decideAction(ctx, notAttacker) shouldBe NoOpAIAction
    }

    "return NoOpAIAction if hand is empty" in {
      val player = mock[IPlayer]
      val defender = mock[IPlayer]

      val (ctx, strategy) = setup(player, defender, Nil, Nil)

      strategy.decideAction(ctx, player) shouldBe NoOpAIAction
    }
    

    "perform SingleAttackAIAction when last card can beat a defender and no double allowed" in {
      val player = mock[IPlayer]
      val defender = mock[IPlayer]

      val hand = List(mockCard(2), mockCard(10)) // last = 10
      val defenders = List(Some(mockCard(7)), Some(mockCard(5))) // beatable: index 0,1 → picks 0

      val (ctx, strategy) = setup(player, defender, hand, defenders)

      strategy.decideAction(ctx, player) shouldBe SingleAttackAIAction(0)
    }

    "perform BoostAIAction on unboosted weak goalkeeper" in {
      val player = mock[IPlayer]
      val defender = mock[IPlayer]

      val hand = List(mockCard(2), mockCard(3))
      val gk = mockCard(5)
      val (ctx, strategy) = setup(player, defender, hand, Nil, goalkeeper = Some(gk), canBoost = true)

      strategy.decideAction(ctx, player) shouldBe BoostAIAction(-1, GoalkeeperZone)
    }

    "perform BoostAIAction on weak unboosted defender if boostable" in {
      val player = mock[IPlayer]
      val defender = mock[IPlayer]

      val hand = List(mockCard(2), mockCard(3))
      val defenders = List(Some(mockCard(4)), Some(mockCard(8)))

      val (ctx, strategy) = setup(player, defender, hand, defenders, canBoost = true)

      strategy.decideAction(ctx, player) shouldBe BoostAIAction(0, DefenderZone)
    }
    

    "fallback with SingleAttackAIAction on strongest defender if no beats possible and no double" in {
      val player = mock[IPlayer]
      val defender = mock[IPlayer]

      val hand = List(mockCard(2))
      val defenders = List(Some(mockCard(10)), Some(mockCard(9))) // fallback pick: index 0

      val (ctx, strategy) = setup(player, defender, hand, defenders)

      strategy.decideAction(ctx, player) shouldBe SingleAttackAIAction(0)
    }
  }
}
