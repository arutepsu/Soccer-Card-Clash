package de.htwg.se.soccercardclash.model.playingFieldComponent.strategy.swapStrategy.base

import de.htwg.se.soccercardclash.controller.NoSwapsEvent
import de.htwg.se.soccercardclash.model.cardComponent.ICard
import de.htwg.se.soccercardclash.model.playerComponent.IPlayer
import de.htwg.se.soccercardclash.model.playerComponent.playerAction.*
import de.htwg.se.soccercardclash.model.playingFiledComponent.IPlayingField
import de.htwg.se.soccercardclash.model.playingFiledComponent.manager.{IActionManager, IDataManager, IRolesManager}
import de.htwg.se.soccercardclash.model.cardComponent.dataStructure.*
import de.htwg.se.soccercardclash.model.playingFiledComponent.strategy.scoringStrategy.IPlayerScores
import de.htwg.se.soccercardclash.model.playingFiledComponent.strategy.swapStrategy.base.{RegularSwapStrategy, ReverseSwapStrategy}
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import org.scalatestplus.mockito.MockitoSugar
import org.mockito.Mockito.*
import de.htwg.se.soccercardclash.util.{Observable, ObservableEvent}

class ReverseSwapStrategyTest extends AnyFlatSpec with Matchers with MockitoSugar {

  class ObservableMockPlayingField extends Observable with IPlayingField with MockitoSugar {
    override def getRoles: IRolesManager = mock[IRolesManager]
    override def getDataManager: IDataManager = mock[IDataManager]
    override def getScores: IPlayerScores = mock[IPlayerScores]
    override def getActionManager: IActionManager = mock[IActionManager]
    override def getAttacker: IPlayer = mock[IPlayer]
    override def getDefender: IPlayer = mock[IPlayer]
    override def reset(): Unit = {}
    override def setPlayingField(): Unit = {}
    override def notifyObservers(e: ObservableEvent): Unit = super.notifyObservers(e)
  }

  "ReverseSwapStrategy" should "reverse the attacker's hand if swap is allowed" in {
    val mockData = mock[IDataManager]
    val mockRoles = mock[IRolesManager]
    val mockAttacker = mock[IPlayer]
    val mockDefender = mock[IPlayer]

    val card1 = mock[ICard]
    val card2 = mock[ICard]
    val card3 = mock[ICard]

    val hand = new HandCardsQueue(List(card1, card2, card3))

    when(mockRoles.attacker).thenReturn(mockAttacker)
    when(mockRoles.defender).thenReturn(mockDefender)
    when(mockAttacker.actionStates).thenReturn(Map(PlayerActionPolicies.Swap -> CanPerformAction(1)))
    when(mockData.getPlayerHand(mockAttacker)).thenReturn(hand)

    val updatedPlayer = mock[IPlayer]
    when(mockAttacker.performAction(PlayerActionPolicies.Swap)).thenReturn(updatedPlayer)
    when(updatedPlayer.actionStates).thenReturn(Map(PlayerActionPolicies.Swap -> CanPerformAction(0)))

    val field = new ObservableMockPlayingField {
      override def getDataManager: IDataManager = mockData
      override def getRoles: IRolesManager = mockRoles
    }

    val strategy = new ReverseSwapStrategy
    val result = strategy.swap(field)

    result shouldBe true
    hand.getCards shouldBe List(card3, card2, card1)
    verify(mockRoles).setRoles(updatedPlayer, mockDefender)
  }

  it should "fail and notify if out of swaps" in {
    val mockData = mock[IDataManager]
    val mockRoles = mock[IRolesManager]
    val mockAttacker = mock[IPlayer]
    val hand = new HandCardsQueue(List(mock[ICard], mock[ICard]))

    when(mockRoles.attacker).thenReturn(mockAttacker)
    when(mockAttacker.actionStates).thenReturn(Map(PlayerActionPolicies.Swap -> OutOfActions))
    when(mockData.getPlayerHand(mockAttacker)).thenReturn(hand)

    var notified: Option[ObservableEvent] = None

    val field = new ObservableMockPlayingField {
      override def getDataManager: IDataManager = mockData
      override def getRoles: IRolesManager = mockRoles
      override def notifyObservers(e: ObservableEvent): Unit = notified = Some(e)
    }

    val strategy = new ReverseSwapStrategy
    val result = strategy.swap(field)

    result shouldBe false
    notified.get shouldBe NoSwapsEvent(mockAttacker)
  }

  it should "fail if remaining uses == 0 without notify" in {
    val mockData = mock[IDataManager]
    val mockRoles = mock[IRolesManager]
    val mockAttacker = mock[IPlayer]
    val hand = new HandCardsQueue(List(mock[ICard], mock[ICard]))

    when(mockRoles.attacker).thenReturn(mockAttacker)
    when(mockAttacker.actionStates).thenReturn(Map(PlayerActionPolicies.Swap -> CanPerformAction(0)))
    when(mockData.getPlayerHand(mockAttacker)).thenReturn(hand)

    var notified = false
    val field = new ObservableMockPlayingField {
      override def getDataManager: IDataManager = mockData
      override def getRoles: IRolesManager = mockRoles
      override def notifyObservers(e: ObservableEvent): Unit = notified = true
    }

    val strategy = new ReverseSwapStrategy
    val result = strategy.swap(field)

    result shouldBe false
    notified shouldBe false
  }

  it should "fail if hand has fewer than 2 cards" in {
    val mockData = mock[IDataManager]
    val mockRoles = mock[IRolesManager]
    val mockAttacker = mock[IPlayer]
    val hand = new HandCardsQueue(List(mock[ICard]))

    when(mockRoles.attacker).thenReturn(mockAttacker)
    when(mockAttacker.actionStates).thenReturn(Map(PlayerActionPolicies.Swap -> CanPerformAction(1)))
    when(mockData.getPlayerHand(mockAttacker)).thenReturn(hand)

    val field = new ObservableMockPlayingField {
      override def getDataManager: IDataManager = mockData
      override def getRoles: IRolesManager = mockRoles
    }

    val strategy = new ReverseSwapStrategy
    val result = strategy.swap(field)

    result shouldBe false
  }
}