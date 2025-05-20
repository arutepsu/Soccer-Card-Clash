package de.htwg.se.soccercardclash.model.playingFieldComponent.strategy.boostStrategy.base

import de.htwg.se.soccercardclash.model.cardComponent.ICard
import de.htwg.se.soccercardclash.model.playerComponent.IPlayer
import de.htwg.se.soccercardclash.model.playerComponent.playerAction.*
import de.htwg.se.soccercardclash.model.gameComponent.state.IGameState
import de.htwg.se.soccercardclash.model.gameComponent.state.components.{IDataManager, IRoles}
import de.htwg.se.soccercardclash.model.gameComponent.state.manager.{IActionManager, IPlayerActionManager}
import de.htwg.se.soccercardclash.model.gameComponent.state.strategy.scoringStrategy.IPlayerScores
import de.htwg.se.soccercardclash.model.gameComponent.state.strategy.boostStrategy.base.GoalkeeperBoostStrategy
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import org.scalatestplus.mockito.MockitoSugar
import org.mockito.Mockito.*
import org.mockito.ArgumentMatchers.any
import de.htwg.se.soccercardclash.util.{NoBoostsEvent, ObservableEvent}
import de.htwg.se.soccercardclash.util.Observable

class GoalkeeperBoostStrategyTest extends AnyFlatSpec with Matchers with MockitoSugar {

  class ObservableMockGameState extends Observable with IGameState with MockitoSugar {
    override def getRoles: IRoles = mock[IRoles]
    override def getDataManager: IDataManager = mock[IDataManager]
    override def getScores: IPlayerScores = mock[IPlayerScores]
    override def getActionManager: IActionManager = mock[IActionManager]
    override def reset(): Unit = {}
    override def setPlayingField(): Unit = {}
    override def notifyObservers(e: ObservableEvent): Unit = super.notifyObservers(e)
  }

  "GoalkeeperBoostStrategy" should "successfully boost the goalkeeper if allowed" in {
    val mockData = mock[IDataManager]
    val mockRoles = mock[IRoles]
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

    val field = new ObservableMockGameState {
      override def getDataManager: IDataManager = mockData
      override def getRoles: IRoles = mockRoles
    }

    val strategy = new GoalkeeperBoostStrategy(mockPlayerActionManager)
    val result = strategy.boost(field)

    result shouldBe true
    verify(mockData).updateGoalkeeperForAttacker(boostedGoalkeeper)
    verify(mockRoles).updateRoles(updatedPlayer, mockDefender)
  }

  it should "fail if no goalkeeper is available" in {
    val mockData = mock[IDataManager]
    val mockRoles = mock[IRoles]
    val mockAttacker = mock[IPlayer]
    val mockPlayerActionManager = mock[IPlayerActionManager]

    when(mockRoles.attacker).thenReturn(mockAttacker)
    when(mockData.getPlayerGoalkeeper(mockAttacker)).thenReturn(None)

    val field = new ObservableMockGameState {
      override def getDataManager: IDataManager = mockData
      override def getRoles: IRoles = mockRoles
    }

    val strategy = new GoalkeeperBoostStrategy(mockPlayerActionManager)
    val result = strategy.boost(field)

    result shouldBe false
  }

  it should "fail and notify if attacker is out of boosts (OutOfActions)" in {
    val mockData = mock[IDataManager]
    val mockRoles = mock[IRoles]
    val mockAttacker = mock[IPlayer]
    val goalkeeper = mock[ICard]
    val mockPlayerActionManager = mock[IPlayerActionManager]

    when(mockRoles.attacker).thenReturn(mockAttacker)
    when(mockData.getPlayerGoalkeeper(mockAttacker)).thenReturn(Some(goalkeeper))
    when(mockPlayerActionManager.canPerform(mockAttacker, PlayerActionPolicies.Boost)).thenReturn(false)

    var notified = false

    val field = new ObservableMockGameState {
      override def getDataManager: IDataManager = mockData
      override def getRoles: IRoles = mockRoles
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
