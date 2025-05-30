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
import de.htwg.se.soccercardclash.util.{BoostAIAction, DefenderZone, DoubleAttackAIAction, GoalkeeperZone, NoOpAIAction, SingleAttackAIAction, UndoManager}
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.mockito.MockitoSugar
import org.mockito.Mockito.*
import de.htwg.se.soccercardclash.model.cardComponent.base.components.Value
import de.htwg.se.soccercardclash.model.gameComponent.IGameState

class SmartAttackAIStrategySpec extends AnyWordSpec with Matchers with MockitoSugar {

  def mockCard(intValue: Int): ICard = {
    val card = mock[ICard]
    val value = Value.fromInt(intValue)
    require(value.isDefined, s"Invalid value: $intValue")
    when(card.value).thenReturn(value.get)
    card
  }

  def setup(
             attacker: IPlayer,
             defender: IPlayer,
             hand: List[ICard],
             defenders: List[Option[ICard]],
             goalkeeper: Option[ICard] = None,
             canDouble: Boolean = false
           ): (GameContext, SmartAttackAIStrategy) = {

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

    val strategy = new SmartAttackAIStrategy {
      override val playerActionManager = {
        val pam = mock[PlayerActionManager]
        when(pam.canPerform(attacker, PlayerActionPolicies.DoubleAttack)).thenReturn(canDouble)
        pam
      }
    }

    (ctx, strategy)
  }

  "SmartAttackAIStrategy" should {

    "return NoOpAIAction if not attacker" in {
      val p1 = mock[IPlayer]
      val p2 = mock[IPlayer]

      val ctx = mock[GameContext]
      val state = mock[IGameState]
      val roles = mock[IRoles]
      when(ctx.state).thenReturn(state)
      when(state.getRoles).thenReturn(roles)
      when(roles.attacker).thenReturn(p2)

      val strategy = new SmartAttackAIStrategy
      strategy.decideAction(ctx, p1) shouldBe NoOpAIAction
    }

    "attack the strongest beatable defender with a single attack" in {
      val p = mock[IPlayer]
      val d = mock[IPlayer]
      val hand = List(mockCard(5), mockCard(9))
      val defenders = List(Some(mockCard(6)), Some(mockCard(8)))

      val (ctx, strategy) = setup(p, d, hand, defenders)
      strategy.decideAction(ctx, p) shouldBe SingleAttackAIAction(1)
    }

    "fallback to double attack on strongest defender if no beatable defenders" in {
      val p = mock[IPlayer]
      val d = mock[IPlayer]
      val hand = List(mockCard(4), mockCard(6))
      val defenders = List(Some(mockCard(7)), Some(mockCard(9)))

      val (ctx, strategy) = setup(p, d, hand, defenders, canDouble = true)
      strategy.decideAction(ctx, p) shouldBe DoubleAttackAIAction(1)
    }

    "attack goalkeeper with single if beatable" in {
      val p = mock[IPlayer]
      val d = mock[IPlayer]
      val hand = List(mockCard(5), mockCard(12))
      val (ctx, strategy) = setup(p, d, hand, List(), Some(mockCard(10)))
      strategy.decideAction(ctx, p) shouldBe SingleAttackAIAction(-1)
    }

    "double attack goalkeeper if single not enough and allowed" in {
      val p = mock[IPlayer]
      val d = mock[IPlayer]
      val hand = List(mockCard(2), mockCard(3))
      val (ctx, strategy) = setup(p, d, hand, List(), Some(mockCard(10)), canDouble = true)
      strategy.decideAction(ctx, p) shouldBe DoubleAttackAIAction(-1)
    }

    "return NoOpAIAction if nothing can be done" in {
      val p = mock[IPlayer]
      val d = mock[IPlayer]
      val hand = List(mockCard(3))
      val (ctx, strategy) = setup(p, d, hand, List(), Some(mockCard(10)))
      strategy.decideAction(ctx, p) shouldBe NoOpAIAction
    }
  }
}