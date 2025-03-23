package de.htwg.se.soccercardclash.model.playingFieldComponent.strategy.boostStrategy.base

import de.htwg.se.soccercardclash.controller.NoBoostsEvent
import de.htwg.se.soccercardclash.model.cardComponent.ICard
import de.htwg.se.soccercardclash.model.playerComponent.IPlayer
import de.htwg.se.soccercardclash.model.playerComponent.playerAction.*
import de.htwg.se.soccercardclash.model.playingFiledComponent.IPlayingField
import de.htwg.se.soccercardclash.model.playingFiledComponent.manager.{IActionManager, IDataManager, IRolesManager, IPlayerActionManager}
import de.htwg.se.soccercardclash.model.playingFiledComponent.strategy.scoringStrategy.IPlayerScores
import de.htwg.se.soccercardclash.model.playingFiledComponent.strategy.boostStrategy.base.GoalkeeperBoostStrategy
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import org.scalatestplus.mockito.MockitoSugar
import org.mockito.Mockito.*
import org.mockito.ArgumentMatchers.any
import de.htwg.se.soccercardclash.util.ObservableEvent
import de.htwg.se.soccercardclash.util.Observable

class GoalkeeperBoostStrategyTest extends AnyFlatSpec with Matchers with MockitoSugar {

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

  "GoalkeeperBoostStrategy" should "successfully boost the goalkeeper if allowed" in {
    val mockData = mock[IDataManager]
    val mockRoles = mock[IRolesManager]
    val mockAttacker = mock[IPlayer]
    val mockDefender = mock[IPlayer]
    val mockPlayerActionManager = mock[IPlayerActionManager]

    val goalkeeper = mock[ICard]
    val boostedGoalkeeper = mock[ICard]

    when(mockRoles.attacker).thenReturn(mockAttacker)
    when(mockRoles.defender).thenReturn(mockDefender)
    when(mockData.getPlayerGoalkeeper(mockAttacker)).thenReturn(Some(goalkeeper))
    when(goalkeeper.boost()).thenReturn(boostedGoalkeeper)

    val updatedPlayer = mock[IPlayer]
    when(mockPlayerActionManager.canPerform(mockAttacker, PlayerActionPolicies.Boost)).thenReturn(true)
    when(mockPlayerActionManager.performAction(mockAttacker, PlayerActionPolicies.Boost)).thenReturn(updatedPlayer)

    val field = new ObservableMockPlayingField {
      override def getDataManager: IDataManager = mockData
      override def getRoles: IRolesManager = mockRoles
    }

    val strategy = new GoalkeeperBoostStrategy(mockPlayerActionManager)
    val result = strategy.boost(field)

    result shouldBe true
    verify(mockData).setGoalkeeperForAttacker(boostedGoalkeeper)
    verify(mockRoles).setRoles(updatedPlayer, mockDefender)
  }

  it should "fail if no goalkeeper is available" in {
    val mockData = mock[IDataManager]
    val mockRoles = mock[IRolesManager]
    val mockAttacker = mock[IPlayer]
    val mockPlayerActionManager = mock[IPlayerActionManager]

    when(mockRoles.attacker).thenReturn(mockAttacker)
    when(mockData.getPlayerGoalkeeper(mockAttacker)).thenReturn(None)

    val field = new ObservableMockPlayingField {
      override def getDataManager: IDataManager = mockData
      override def getRoles: IRolesManager = mockRoles
    }

    val strategy = new GoalkeeperBoostStrategy(mockPlayerActionManager)
    val result = strategy.boost(field)

    result shouldBe false
  }

  it should "fail and notify if attacker is out of boosts (OutOfActions)" in {
    val mockData = mock[IDataManager]
    val mockRoles = mock[IRolesManager]
    val mockAttacker = mock[IPlayer]
    val goalkeeper = mock[ICard]
    val mockPlayerActionManager = mock[IPlayerActionManager]

    when(mockRoles.attacker).thenReturn(mockAttacker)
    when(mockData.getPlayerGoalkeeper(mockAttacker)).thenReturn(Some(goalkeeper))
    when(mockPlayerActionManager.canPerform(mockAttacker, PlayerActionPolicies.Boost)).thenReturn(false)

    var notified = false

    val field = new ObservableMockPlayingField {
      override def getDataManager: IDataManager = mockData
      override def getRoles: IRolesManager = mockRoles
      override def notifyObservers(e: ObservableEvent): Unit = {
        notified = true
        e shouldBe NoBoostsEvent(mockAttacker)
      }
    }

    val strategy = new GoalkeeperBoostStrategy(mockPlayerActionManager)
    val result = strategy.boost(field)

    result shouldBe false
    notified shouldBe true
  }
}
