package de.htwg.se.soccercardclash.model.playingFieldComponent.strategy.swapStrategy.base

import de.htwg.se.soccercardclash.model.cardComponent.ICard
import de.htwg.se.soccercardclash.model.cardComponent.dataStructure.*
import de.htwg.se.soccercardclash.model.playerComponent.IPlayer
import de.htwg.se.soccercardclash.model.playerComponent.playerAction.*
import de.htwg.se.soccercardclash.model.gameComponent.playingFiledComponent.IPlayingField
import de.htwg.se.soccercardclash.model.gameComponent.playingFiledComponent.manager.{IActionManager, IDataManager, IPlayerActionManager, IRolesManager}
import de.htwg.se.soccercardclash.model.gameComponent.playingFiledComponent.strategy.scoringStrategy.IPlayerScores
import de.htwg.se.soccercardclash.model.gameComponent.playingFiledComponent.strategy.swapStrategy.base.RegularSwapStrategy
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import org.scalatestplus.mockito.MockitoSugar
import org.mockito.Mockito.*
import de.htwg.se.soccercardclash.util.{NoSwapsEvent, Observable, ObservableEvent}
import org.mockito.ArgumentCaptor
import org.mockito.ArgumentMatchers

class RegularSwapStrategyTest extends AnyFlatSpec with Matchers with MockitoSugar {

  class ObservableMockPlayingField extends Observable with IPlayingField with MockitoSugar {
    override def getRoles: IRolesManager = mock[IRolesManager]
    override def getDataManager: IDataManager = mock[IDataManager]
    override def getScores: IPlayerScores = mock[IPlayerScores]
    override def getActionManager: IActionManager = mock[IActionManager]
    override def reset(): Unit = {}
    override def setPlayingField(): Unit = {}
    override def notifyObservers(e: ObservableEvent): Unit = super.notifyObservers(e)
  }

  "HandSwapStrategy" should "swap selected card with last card in hand" in {
    val mockData = mock[IDataManager]
    val mockRoles = mock[IRolesManager]
    val mockAttacker = mock[IPlayer]
    val mockDefender = mock[IPlayer]
    val mockPlayerActionManager = mock[IPlayerActionManager]

    val card1 = mock[ICard]("card1")
    val card2 = mock[ICard]("card2")
    val hand = new HandCardsQueue(List(card1, card2))

    when(mockRoles.attacker).thenReturn(mockAttacker)
    when(mockRoles.defender).thenReturn(mockDefender)
    when(mockData.getPlayerHand(mockAttacker)).thenReturn(hand)
    when(mockPlayerActionManager.canPerform(mockAttacker, PlayerActionPolicies.Swap)).thenReturn(true)

    val updatedPlayer = mock[IPlayer]
    when(mockPlayerActionManager.performAction(mockAttacker, PlayerActionPolicies.Swap)).thenReturn(updatedPlayer)
    when(updatedPlayer.actionStates).thenReturn(Map(PlayerActionPolicies.Swap -> CanPerformAction(0)))

    val field = new ObservableMockPlayingField {
      override def getDataManager: IDataManager = mockData

      override def getRoles: IRolesManager = mockRoles
    }

    val strategy = new RegularSwapStrategy(0, mockPlayerActionManager)
    val result = strategy.swap(field)

    result shouldBe true

    val handCaptor = ArgumentCaptor.forClass(classOf[IHandCardsQueue])

    verify(mockData).setPlayerHand(ArgumentMatchers.eq(mockAttacker), handCaptor.capture())

    val swappedList = handCaptor.getValue.toList
    swappedList should contain inOrder (card2, card1)

    verify(mockRoles).setRoles(updatedPlayer, mockDefender)
  }


  it should "fail and notify if swap is out of actions" in {
    val mockData = mock[IDataManager]
    val mockRoles = mock[IRolesManager]
    val mockAttacker = mock[IPlayer]
    val mockPlayerActionManager = mock[IPlayerActionManager]
    val hand = new HandCardsQueue(List(mock[ICard], mock[ICard]))

    when(mockRoles.attacker).thenReturn(mockAttacker)
    when(mockAttacker.actionStates).thenReturn(Map(PlayerActionPolicies.Swap -> OutOfActions))
    when(mockData.getPlayerHand(mockAttacker)).thenReturn(hand)

    var notified: Option[ObservableEvent] = None

    val field = new ObservableMockPlayingField {
      override def getDataManager: IDataManager = mockData
      override def getRoles: IRolesManager = mockRoles
      override def notifyObservers(e: ObservableEvent): Unit = {
        notified = Some(e)
      }
    }

    val strategy = new RegularSwapStrategy(0, mockPlayerActionManager)
    val result = strategy.swap(field)

    result shouldBe false
    notified.get shouldBe NoSwapsEvent(mockAttacker)
  }

  it should "fail if hand has less than two cards" in {
    val mockData = mock[IDataManager]
    val mockRoles = mock[IRolesManager]
    val mockAttacker = mock[IPlayer]
    val mockPlayerActionManager = mock[IPlayerActionManager]

    val hand = new HandCardsQueue(Nil)
    hand.addCard(mock[ICard]) // only one card

    when(mockRoles.attacker).thenReturn(mockAttacker)
    when(mockAttacker.actionStates).thenReturn(Map(PlayerActionPolicies.Swap -> CanPerformAction(1)))
    when(mockData.getPlayerHand(mockAttacker)).thenReturn(hand)

    val field = new ObservableMockPlayingField {
      override def getDataManager: IDataManager = mockData
      override def getRoles: IRolesManager = mockRoles
    }

    val strategy = new RegularSwapStrategy(0, mockPlayerActionManager)
    val result = strategy.swap(field)

    result shouldBe false
  }

  it should "fail if index is out of bounds" in {
    val mockData = mock[IDataManager]
    val mockRoles = mock[IRolesManager]
    val mockAttacker = mock[IPlayer]
    val mockPlayerActionManager = mock[IPlayerActionManager]

    val hand = new HandCardsQueue(Nil)
    hand.addCard(mock[ICard])
    hand.addCard(mock[ICard])

    when(mockRoles.attacker).thenReturn(mockAttacker)
    when(mockAttacker.actionStates).thenReturn(Map(PlayerActionPolicies.Swap -> CanPerformAction(1)))
    when(mockData.getPlayerHand(mockAttacker)).thenReturn(hand)

    val field = new ObservableMockPlayingField {
      override def getDataManager: IDataManager = mockData
      override def getRoles: IRolesManager = mockRoles
    }

    val strategy = new RegularSwapStrategy(2, mockPlayerActionManager) // invalid index
    val result = strategy.swap(field)

    result shouldBe false
  }
}
