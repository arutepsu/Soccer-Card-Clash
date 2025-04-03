package de.htwg.se.soccercardclash.model.playingFieldComponent.strategy.attackStrategy.base

import de.htwg.se.soccercardclash.model.cardComponent.ICard
import de.htwg.se.soccercardclash.model.playerComponent.IPlayer
import de.htwg.se.soccercardclash.model.playerComponent.playerAction.{CanPerformAction, OutOfActions, PlayerActionPolicies}
import de.htwg.se.soccercardclash.model.gameComponent.playingFiledComponent.IPlayingField
import de.htwg.se.soccercardclash.model.cardComponent.dataStructure.*
import de.htwg.se.soccercardclash.model.gameComponent.playingFiledComponent.manager.{IActionManager, IDataManager, IPlayerActionManager, IRolesManager}
import de.htwg.se.soccercardclash.model.gameComponent.playingFiledComponent.strategy.attackStrategy.base.DoubleAttackStrategy
import de.htwg.se.soccercardclash.model.gameComponent.playingFiledComponent.strategy.boostStrategy.{BoostManager, IRevertStrategy}
import de.htwg.se.soccercardclash.model.gameComponent.playingFiledComponent.strategy.scoringStrategy.IPlayerScores
import org.mockito.Mockito.*
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.mockito.MockitoSugar
import org.mockito.Mockito.mock
import org.mockito.ArgumentMatchers.any
import de.htwg.se.soccercardclash.util.{AttackResultEvent, DoubleComparedCardsEvent, Events, NoDoubleAttacksEvent, Observable, ObservableEvent}

import scala.util.Success

class DoubleAttackStrategySpec extends AnyWordSpec with Matchers with MockitoSugar {

  class ObservableMockPlayingField extends Observable with IPlayingField with MockitoSugar {
    override def getRoles: IRolesManager = mock[IRolesManager]
    override def getDataManager: IDataManager = mock[IDataManager]
    override def getScores: IPlayerScores = mock[IPlayerScores]
    override def getActionManager: IActionManager = mock[IActionManager]
    override def reset(): Unit = {}
    override def setPlayingField(): Unit = {}
    override def notifyObservers(e: ObservableEvent): Unit = super.notifyObservers(e)
  }

  "DoubleAttackStrategy" should {

    "execute a successful double attack on a defender" in {
      val playingField = spy(new ObservableMockPlayingField)
      val dataManager = mock[IDataManager]
      val rolesManager = mock[IRolesManager]
      val attacker = mock[IPlayer]
      val updatedAttacker = mock[IPlayer]
      val defender = mock[IPlayer]
      val attackerHand = mock[IHandCardsQueue]
      val defenderHand = mock[IHandCardsQueue]
      val revertStrategy = mock[IRevertStrategy]
      val scores = mock[IPlayerScores]
      val card1 = mock[ICard]
      val card2 = mock[ICard]
      val defenderCard = mock[ICard]
      val mockPlayerActionManager = mock[IPlayerActionManager]

      val boostManager = new BoostManager(playingField) {
        override def getRevertStrategy: IRevertStrategy = revertStrategy
      }

      val actionManager = mock[IActionManager]
      when(actionManager.getBoostManager).thenReturn(boostManager)
      when(actionManager.getPlayerActionService).thenReturn(mockPlayerActionManager)
      when(playingField.getActionManager).thenReturn(actionManager)

      when(playingField.getRoles).thenReturn(rolesManager)
      when(playingField.getDataManager).thenReturn(dataManager)
      when(playingField.getScores).thenReturn(scores)

      when(rolesManager.attacker).thenReturn(attacker)
      when(rolesManager.defender).thenReturn(defender)

      // ✅ This one is critical — because strategy updates the roles!
      doNothing().when(rolesManager).setRoles(updatedAttacker, defender)

      when(attacker.actionStates).thenReturn(Map(PlayerActionPolicies.DoubleAttack -> CanPerformAction(1)))
      when(mockPlayerActionManager.canPerform(attacker, PlayerActionPolicies.DoubleAttack))
        .thenReturn(true)
      when(mockPlayerActionManager.performAction(attacker, PlayerActionPolicies.DoubleAttack))
        .thenReturn(updatedAttacker)
      when(updatedAttacker.actionStates).thenReturn(Map(PlayerActionPolicies.DoubleAttack -> CanPerformAction(0)))

      when(dataManager.getPlayerHand(updatedAttacker)).thenReturn(attackerHand)
      when(dataManager.getPlayerHand(defender)).thenReturn(defenderHand)

      when(attackerHand.getHandSize).thenReturn(2)
      when(attackerHand.removeLastCard())
      .thenReturn(Success((card2, attackerHand)))
      .thenReturn(Success((card1, attackerHand)))
      when(card1.valueToInt).thenReturn(5)
      when(card2.valueToInt).thenReturn(7)
      when(dataManager.allDefendersBeaten(defender)).thenReturn(false)
      when(dataManager.getDefenderCard(defender, 0)).thenReturn(defenderCard)
      when(defenderCard.valueToInt).thenReturn(10)
      when(revertStrategy.revertCard(defenderCard)).thenReturn(defenderCard)

      doNothing().when(dataManager).removeDefenderCard(defender, defenderCard)
      when(attackerHand.addCard(card1)).thenReturn(attackerHand)
      when(attackerHand.addCard(card2)).thenReturn(attackerHand)

      // ✅ Mock notifyObservers to avoid side effects
      doNothing().when(playingField).notifyObservers(any[ObservableEvent])

      val strategy = new DoubleAttackStrategy(0, mockPlayerActionManager)
      val result = strategy.execute(playingField)

      result shouldBe true

      verify(attackerHand).addCard(card1)
      verify(attackerHand).addCard(card2)
      verify(dataManager, times(2)).removeDefenderCard(defender, defenderCard)

    }


    "fail when not enough cards in attacker’s hand" in {
      val playingField = spy(new ObservableMockPlayingField)
      val dataManager = mock[IDataManager]
      val rolesManager = mock[IRolesManager]
      val attacker = mock[IPlayer]
      val updatedAttacker = mock[IPlayer]
      val defender = mock[IPlayer]
      val attackerHand = mock[IHandCardsQueue]
      val revertStrategy = mock[IRevertStrategy]
      val mockPlayerActionManager = mock[IPlayerActionManager]

      val boostManager = new BoostManager(playingField) {
        override def getRevertStrategy: IRevertStrategy = revertStrategy
      }

      val actionManager = mock[IActionManager]
      when(actionManager.getBoostManager).thenReturn(boostManager)
      when(actionManager.getPlayerActionService).thenReturn(mockPlayerActionManager)
      when(playingField.getActionManager).thenReturn(actionManager)

      when(playingField.getRoles).thenReturn(rolesManager)
      when(playingField.getDataManager).thenReturn(dataManager)

      when(rolesManager.attacker).thenReturn(attacker)
      when(rolesManager.defender).thenReturn(defender)

      when(attacker.actionStates).thenReturn(Map(PlayerActionPolicies.DoubleAttack -> CanPerformAction(1)))
      when(mockPlayerActionManager.performAction(attacker, PlayerActionPolicies.DoubleAttack)).thenReturn(updatedAttacker)
      when(dataManager.getPlayerHand(updatedAttacker)).thenReturn(attackerHand)
      when(attackerHand.getHandSize).thenReturn(1)

      val strategy = new DoubleAttackStrategy(0, mockPlayerActionManager)

      val result = strategy.execute(playingField)

      result shouldBe false
    }

    "notify observers if attacker is out of double attacks" in {
      val playingField = spy(new ObservableMockPlayingField)
      val rolesManager = mock[IRolesManager]
      val attacker = mock[IPlayer]
      val defender = mock[IPlayer]
      val revertStrategy = mock[IRevertStrategy]
      val mockPlayerActionManager = mock[IPlayerActionManager]

      val boostManager = new BoostManager(playingField) {
        override def getRevertStrategy: IRevertStrategy = revertStrategy
      }

      val actionManager = mock[IActionManager]
      when(actionManager.getBoostManager).thenReturn(boostManager)
      when(actionManager.getPlayerActionService).thenReturn(mockPlayerActionManager)
      when(playingField.getActionManager).thenReturn(actionManager)

      when(playingField.getRoles).thenReturn(rolesManager)
      when(rolesManager.attacker).thenReturn(attacker)
      when(rolesManager.defender).thenReturn(defender)

      when(attacker.actionStates).thenReturn(
        Map(PlayerActionPolicies.DoubleAttack -> OutOfActions)
      )

      val strategy = new DoubleAttackStrategy(0, mockPlayerActionManager)
      val result = strategy.execute(playingField)

      result shouldBe false
      verify(playingField).notifyObservers(NoDoubleAttacksEvent(attacker))
    }
  }
}
