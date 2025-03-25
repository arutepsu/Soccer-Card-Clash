package de.htwg.se.soccercardclash.model.playingFieldComponent.strategy.boostStrategy.base

import de.htwg.se.soccercardclash.model.cardComponent.ICard
import de.htwg.se.soccercardclash.model.playerComponent.IPlayer
import de.htwg.se.soccercardclash.model.playerComponent.playerAction.*
import de.htwg.se.soccercardclash.model.playingFiledComponent.IPlayingField
import de.htwg.se.soccercardclash.model.playingFiledComponent.manager.*
import de.htwg.se.soccercardclash.model.playingFiledComponent.strategy.boostStrategy.base.DefenderBoostStrategy
import org.mockito.Mockito.*
import org.mockito.ArgumentMatchers.*
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import org.scalatestplus.mockito.MockitoSugar
import de.htwg.se.soccercardclash.model.playingFiledComponent.strategy.scoringStrategy.IPlayerScores
import de.htwg.se.soccercardclash.util.{Events, NoBoostsEvent, ObservableEvent}
import de.htwg.se.soccercardclash.util.Observable

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
    val mockPlayerActionManager = mock[IPlayerActionManager]

    val card = mock[ICard]
    val boostedCard = mock[ICard]

    when(mockRoles.attacker).thenReturn(mockAttacker)
    when(mockRoles.defender).thenReturn(mockDefender)
    when(mockPlayerActionManager.canPerform(mockAttacker, PlayerActionPolicies.Boost)).thenReturn(true)
    when(mockPlayerActionManager.performAction(mockAttacker, PlayerActionPolicies.Boost)).thenReturn(mockAttacker)

    when(card.boost()).thenReturn(boostedCard)
    when(mockData.getPlayerDefenders(mockAttacker)).thenReturn(List(card))

    val field = new ObservableMockPlayingField {
      override def getDataManager: IDataManager = mockData
      override def getRoles: IRolesManager = mockRoles
    }

    val strategy = new DefenderBoostStrategy(0, mockPlayerActionManager)
    val result = strategy.boost(field)

    result shouldBe true
    verify(mockData).setPlayerDefenders(mockAttacker, List(boostedCard))
    verify(mockRoles).setRoles(mockAttacker, mockDefender)
  }


  it should "fail if index is out of bounds" in {
    val mockData = mock[IDataManager]
    val mockRoles = mock[IRolesManager]
    val mockAttacker = mock[IPlayer]
    val mockPlayerActionManager = mock[IPlayerActionManager]

    when(mockRoles.attacker).thenReturn(mockAttacker)
    when(mockData.getPlayerDefenders(mockAttacker)).thenReturn(List.empty)

    val field = new ObservableMockPlayingField {
      override def getDataManager: IDataManager = mockData

      override def getRoles: IRolesManager = mockRoles
    }

    val strategy = new DefenderBoostStrategy(1, mockPlayerActionManager)

    val result = strategy.boost(field)

    result shouldBe false
  }

  it should "fail and notify if attacker is out of boosts (OutOfActions)" in {
    val mockData = mock[IDataManager]
    val mockRoles = mock[IRolesManager]
    val mockAttacker = mock[IPlayer]
    val card = mock[ICard]
    val mockPlayerActionManager = mock[IPlayerActionManager]

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

    val strategy = new DefenderBoostStrategy(0, mockPlayerActionManager)

    val result = strategy.boost(field)

    result shouldBe false
    notified shouldBe true
  }


  it should "fail and notify if attacker has 0 remaining boost uses" in {
    val mockData = mock[IDataManager]
    val mockRoles = mock[IRolesManager]
    val mockAttacker = mock[IPlayer]
    val mockDefender = mock[IPlayer]
    val card = mock[ICard]
    val mockPlayerActionManager = mock[IPlayerActionManager]

    when(mockRoles.attacker).thenReturn(mockAttacker)
    when(mockRoles.defender).thenReturn(mockDefender)
    when(mockAttacker.actionStates).thenReturn(Map(PlayerActionPolicies.Boost -> CanPerformAction(0)))
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

    val strategy = new DefenderBoostStrategy(0, mockPlayerActionManager)

    val result = strategy.boost(field)

    result shouldBe false
    notified shouldBe true
  } // ✅ This closing brace was missing!


}