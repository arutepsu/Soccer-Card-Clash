package de.htwg.se.soccercardclash.model.playingFieldComponent.strategy.swapStrategy.base

import de.htwg.se.soccercardclash.model.cardComponent.ICard
import de.htwg.se.soccercardclash.model.playerComponent.IPlayer
import de.htwg.se.soccercardclash.model.playerComponent.playerAction.*
import de.htwg.se.soccercardclash.model.gameComponent.state.IGameState
import de.htwg.se.soccercardclash.model.gameComponent.state.manager.{IActionManager, IPlayerActionManager}
import de.htwg.se.soccercardclash.model.cardComponent.dataStructure.*
import de.htwg.se.soccercardclash.model.gameComponent.state.components.{IDataManager, IRoles}
import de.htwg.se.soccercardclash.model.gameComponent.state.strategy.scoringStrategy.IPlayerScores
import de.htwg.se.soccercardclash.model.gameComponent.state.strategy.swapStrategy.base.{RegularSwapStrategy, ReverseSwapStrategy}
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import org.scalatestplus.mockito.MockitoSugar
import org.mockito.Mockito.*
import de.htwg.se.soccercardclash.util.{NoSwapsEvent, Observable, ObservableEvent}
import org.mockito.ArgumentCaptor
import org.mockito.ArgumentMatchers.{eq => eqTo}

class ReverseSwapStrategyTest extends AnyFlatSpec with Matchers with MockitoSugar {

  class ObservableMockGameState extends Observable with IGameState with MockitoSugar {
    override def getRoles: IRoles = mock[IRoles]
    override def getDataManager: IDataManager = mock[IDataManager]
    override def getScores: IPlayerScores = mock[IPlayerScores]
    override def getActionManager: IActionManager = mock[IActionManager]
    override def reset(): Unit = {}
    override def setPlayingField(): Unit = {}
    override def notifyObservers(e: ObservableEvent): Unit = super.notifyObservers(e)
  }

  "ReverseSwapStrategy" should "reverse the attacker's hand if swap is allowed" in {
    val mockData = mock[IDataManager]
    val mockRoles = mock[IRoles]
    val mockAttacker = mock[IPlayer]
    val mockDefender = mock[IPlayer]
    val mockPlayerActionManager = mock[IPlayerActionManager]
    val card1 = mock[ICard]
    val card2 = mock[ICard]
    val card3 = mock[ICard]

    val originalHand = new HandCardsQueue(List(card1, card2, card3))

    when(mockRoles.attacker).thenReturn(mockAttacker)
    when(mockRoles.defender).thenReturn(mockDefender)
    when(mockAttacker.actionStates).thenReturn(Map(PlayerActionPolicies.Swap -> CanPerformAction(1)))
    when(mockData.getPlayerHand(mockAttacker)).thenReturn(originalHand)
    when(mockPlayerActionManager.canPerform(mockAttacker, PlayerActionPolicies.Swap)).thenReturn(true)

    val updatedPlayer = mock[IPlayer]
    when(mockPlayerActionManager.performAction(mockAttacker, PlayerActionPolicies.Swap)).thenReturn(updatedPlayer)
    when(updatedPlayer.actionStates).thenReturn(Map(PlayerActionPolicies.Swap -> CanPerformAction(0)))

    val playingField = new ObservableMockGameState {
      override def getDataManager: IDataManager = mockData
      override def getRoles: IRoles = mockRoles
    }

    val strategy = new ReverseSwapStrategy(mockPlayerActionManager)
    val result = strategy.swap(playingField)

    result shouldBe true

    // Capture the reversed hand set by the strategy
    val handCaptor = ArgumentCaptor.forClass(classOf[HandCardsQueue])
    verify(mockData).setPlayerHand(eqTo(mockAttacker), handCaptor.capture())



    handCaptor.getValue.toList shouldBe List(card3, card2, card1)

    verify(mockRoles).setRoles(updatedPlayer, mockDefender)
  }


  it should "fail and notify if out of swaps" in {
    val mockData = mock[IDataManager]
    val mockRoles = mock[IRoles]
    val mockAttacker = mock[IPlayer]
    val hand = new HandCardsQueue(List(mock[ICard], mock[ICard]))
    val mockPlayerActionManager = mock[IPlayerActionManager]

    when(mockRoles.attacker).thenReturn(mockAttacker)
    when(mockAttacker.actionStates).thenReturn(Map(PlayerActionPolicies.Swap -> OutOfActions))
    when(mockData.getPlayerHand(mockAttacker)).thenReturn(hand)

    var notified: Option[ObservableEvent] = None

    val field = new ObservableMockGameState {
      override def getDataManager: IDataManager = mockData
      override def getRoles: IRoles = mockRoles
      override def notifyObservers(e: ObservableEvent): Unit = notified = Some(e)
    }

    val strategy = new ReverseSwapStrategy(mockPlayerActionManager)
    val result = strategy.swap(field)

    result shouldBe false
    notified.get shouldBe NoSwapsEvent(mockAttacker)
  }

  it should "fail if remaining uses == 0 without notify" in {
    val mockData = mock[IDataManager]
    val mockRoles = mock[IRoles]
    val mockAttacker = mock[IPlayer]
    val mockPlayerActionManager = mock[IPlayerActionManager]
    val hand = new HandCardsQueue(List(mock[ICard], mock[ICard]))

    when(mockRoles.attacker).thenReturn(mockAttacker)
    when(mockData.getPlayerHand(mockAttacker)).thenReturn(hand)

    // 👇 simulate remainingUses == 0 without being OutOfActions
    when(mockAttacker.actionStates).thenReturn(
      Map(PlayerActionPolicies.Swap -> CanPerformAction(0))
    )
    when(mockPlayerActionManager.canPerform(mockAttacker, PlayerActionPolicies.Swap)).thenReturn(false)

    var notified = false
    val field = new ObservableMockGameState {
      override def getDataManager: IDataManager = mockData

      override def getRoles: IRoles = mockRoles

      override def notifyObservers(e: ObservableEvent): Unit = notified = true
    }

    val strategy = new ReverseSwapStrategy(mockPlayerActionManager)
    val result = strategy.swap(field)

    result shouldBe false
    notified shouldBe false
  }


  it should "fail if hand has fewer than 2 cards" in {
    val mockData = mock[IDataManager]
    val mockRoles = mock[IRoles]
    val mockAttacker = mock[IPlayer]
    val mockPlayerActionManager = mock[IPlayerActionManager]
    val hand = new HandCardsQueue(List(mock[ICard]))

    when(mockRoles.attacker).thenReturn(mockAttacker)
    when(mockAttacker.actionStates).thenReturn(Map(PlayerActionPolicies.Swap -> CanPerformAction(1)))
    when(mockData.getPlayerHand(mockAttacker)).thenReturn(hand)

    val field = new ObservableMockGameState {
      override def getDataManager: IDataManager = mockData
      override def getRoles: IRoles = mockRoles
    }

    val strategy = new ReverseSwapStrategy(mockPlayerActionManager)
    val result = strategy.swap(field)

    result shouldBe false
  }
}