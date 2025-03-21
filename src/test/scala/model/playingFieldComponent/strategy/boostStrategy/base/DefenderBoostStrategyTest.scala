package model.playingFieldComponent.strategy.boostStrategy.base

import controller.{Events, NoBoostsEvent}
import model.cardComponent.ICard
import model.playerComponent.IPlayer
import model.playerComponent.playerAction.*
import model.playingFiledComponent.IPlayingField
import model.playingFiledComponent.manager.*
import model.playingFiledComponent.strategy.boostStrategy.base.DefenderBoostStrategy
import org.mockito.Mockito.*
import org.mockito.ArgumentMatchers.*
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import org.scalatestplus.mockito.MockitoSugar
import model.playingFiledComponent.strategy.scoringStrategy.IPlayerScores
import util.ObservableEvent
import util.Observable

class DefenderBoostStrategyTest extends AnyFlatSpec with Matchers with MockitoSugar {

  // Custom observable mock that avoids ClassCastException
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

  "DefenderBoostStrategy" should "successfully boost a card if allowed" in {
    val mockData = mock[IDataManager]
    val mockRoles = mock[IRolesManager]
    val mockAttacker = mock[IPlayer]
    val mockDefender = mock[IPlayer]

    val card = mock[ICard]
    val boostedCard = mock[ICard]

    when(mockRoles.attacker).thenReturn(mockAttacker)
    when(mockRoles.defender).thenReturn(mockDefender)
    when(mockAttacker.actionStates).thenReturn(Map(PlayerActionPolicies.Boost -> CanPerformAction(1)))
    when(card.boost()).thenReturn(boostedCard)
    when(mockData.getPlayerDefenders(mockAttacker)).thenReturn(List(card))

    val updatedPlayer = mock[IPlayer]
    when(mockAttacker.performAction(PlayerActionPolicies.Boost)).thenReturn(updatedPlayer)
    when(updatedPlayer.actionStates).thenReturn(Map(PlayerActionPolicies.Boost -> CanPerformAction(0)))

    val field = new ObservableMockPlayingField {
      override def getDataManager: IDataManager = mockData
      override def getRoles: IRolesManager = mockRoles
    }

    val strategy = new DefenderBoostStrategy(0)
    val result = strategy.boost(field)

    result shouldBe true
    verify(mockData).setPlayerDefenders(mockAttacker, List(boostedCard))
    verify(mockRoles).setRoles(updatedPlayer, mockDefender)
  }

  it should "fail if index is out of bounds" in {
    val mockData = mock[IDataManager]
    val mockRoles = mock[IRolesManager]
    val mockAttacker = mock[IPlayer]

    when(mockRoles.attacker).thenReturn(mockAttacker)
    when(mockData.getPlayerDefenders(mockAttacker)).thenReturn(List.empty)

    val field = new ObservableMockPlayingField {
      override def getDataManager: IDataManager = mockData
      override def getRoles: IRolesManager = mockRoles
    }

    val strategy = new DefenderBoostStrategy(1)
    val result = strategy.boost(field)

    result shouldBe false
  }

  it should "fail and notify if attacker is out of boosts (OutOfActions)" in {
    val mockData = mock[IDataManager]
    val mockRoles = mock[IRolesManager]
    val mockAttacker = mock[IPlayer]
    val card = mock[ICard]

    when(mockRoles.attacker).thenReturn(mockAttacker)
    when(mockAttacker.actionStates).thenReturn(Map(PlayerActionPolicies.Boost -> OutOfActions))
    when(mockData.getPlayerDefenders(mockAttacker)).thenReturn(List(card))

    var notified = false
    val field = new ObservableMockPlayingField {
      override def getDataManager: IDataManager = mockData
      override def getRoles: IRolesManager = mockRoles
      override def notifyObservers(e: ObservableEvent): Unit = {
        notified = true
        e shouldBe NoBoostsEvent(mockAttacker)
      }
    }

    val strategy = new DefenderBoostStrategy(0)
    val result = strategy.boost(field)

    result shouldBe false
    notified shouldBe true
  }
}