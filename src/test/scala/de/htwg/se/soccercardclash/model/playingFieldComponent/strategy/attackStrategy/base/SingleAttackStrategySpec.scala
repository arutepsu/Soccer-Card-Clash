package de.htwg.se.soccercardclash.model.playingFieldComponent.strategy.attackStrategy.base

import de.htwg.se.soccercardclash.model.cardComponent.ICard
import de.htwg.se.soccercardclash.model.playerComponent.IPlayer
import de.htwg.se.soccercardclash.model.playerComponent.playerAction.{CanPerformAction, OutOfActions, PlayerActionPolicies}
import de.htwg.se.soccercardclash.model.gameComponent.playingFiledComponent.IPlayingField
import de.htwg.se.soccercardclash.model.cardComponent.dataStructure.*
import de.htwg.se.soccercardclash.model.gameComponent.playingFiledComponent.manager.{IActionManager, IDataManager, IRolesManager}
import de.htwg.se.soccercardclash.model.gameComponent.playingFiledComponent.strategy.boostStrategy.{BoostManager, IRevertStrategy}
import de.htwg.se.soccercardclash.model.gameComponent.playingFiledComponent.strategy.scoringStrategy.IPlayerScores
import org.mockito.Mockito.*
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.mockito.MockitoSugar
import de.htwg.se.soccercardclash.util.{AttackResultEvent, ComparedCardsEvent, Events, Observable, ObservableEvent, TieComparisonEvent}
import de.htwg.se.soccercardclash.model.gameComponent.playingFiledComponent.strategy.attackStrategy.base.SingleAttackStrategy
import org.mockito.ArgumentMatchers.any

class SingleAttackStrategySpec extends AnyWordSpec with Matchers with MockitoSugar {

  class ObservableMockPlayingField extends Observable with IPlayingField with MockitoSugar {
    override def getRoles: IRolesManager = mock[IRolesManager]
    override def getDataManager: IDataManager = mock[IDataManager]
    override def getScores: IPlayerScores = mock[IPlayerScores]
    override def getActionManager: IActionManager = mock[IActionManager]
    override def reset(): Unit = {}
    override def setPlayingField(): Unit = {}
    override def notifyObservers(e: ObservableEvent): Unit = super.notifyObservers(e)
  }

  "SingleAttackStrategy" should {

    "execute a successful single attack on a defender" in {
      val playingField = spy(new ObservableMockPlayingField)
      val dataManager = mock[IDataManager]
      val rolesManager = mock[IRolesManager]
      val attacker = mock[IPlayer]
      val defender = mock[IPlayer]
      val attackerHand = mock[IHandCardsQueue]
      val defenderHand = mock[IHandCardsQueue]
      val revertStrategy = mock[IRevertStrategy]
      val scores = mock[IPlayerScores]
      val card = mock[ICard]
      val defenderCard = mock[ICard]

      val boostManager = new BoostManager(playingField) {
        override def getRevertStrategy: IRevertStrategy = revertStrategy
      }
      val actionManager = mock[IActionManager]

      when(actionManager.getBoostManager).thenReturn(boostManager)
      when(playingField.getActionManager).thenReturn(actionManager)

      when(playingField.getRoles).thenReturn(rolesManager)
      when(playingField.getDataManager).thenReturn(dataManager)
      when(playingField.getScores).thenReturn(scores)

      when(rolesManager.attacker).thenReturn(attacker)
      when(rolesManager.defender).thenReturn(defender)

      when(dataManager.getPlayerHand(attacker)).thenReturn(attackerHand)
      when(dataManager.getPlayerHand(defender)).thenReturn(defenderHand)
      when(attackerHand.removeLastCard()).thenReturn(card)
      when(dataManager.allDefendersBeaten(defender)).thenReturn(false)
      when(dataManager.getDefenderCard(defender, 0)).thenReturn(defenderCard)

      when(card.compare(defenderCard)).thenReturn(1) // attacker wins
      when(defenderCard.valueToInt).thenReturn(5)
      when(revertStrategy.revertCard(defenderCard)).thenReturn(defenderCard)
      doNothing().when(dataManager).removeDefenderCard(defender, defenderCard)
      doNothing().when(dataManager).removeDefenderCard(defender, defenderCard)
      doNothing().when(attackerHand).addCard(card)

      val strategy = new SingleAttackStrategy(0)
      val result = strategy.execute(playingField)

      result shouldBe true

      verify(playingField).notifyObservers(ComparedCardsEvent(card, defenderCard))
      verify(attackerHand).addCard(card)
      verify(dataManager, times(2)).removeDefenderCard(defender, defenderCard)
    }

//    "handle a tie and resolve with tie-breaker" in {
//      val playingField = spy(new ObservableMockPlayingField)
//      val dataManager = mock[IDataManager]
//      val rolesManager = mock[IRolesManager]
//      val attacker = mock[IPlayer]
//      val defender = mock[IPlayer]
//      val attackerHand = mock[IHandCardsQueue]
//      val defenderHand = mock[IHandCardsQueue]
//      val revertStrategy = mock[IRevertStrategy]
//      val scores = mock[IPlayerScores]
//      val attackingCard = mock[ICard]
//      val defenderCard = mock[ICard]
//      val extraAttackerCard = mock[ICard]
//      val extraDefenderCard = mock[ICard]
//
//      // BoostManager with mocked revertStrategy
//      val boostManager = new BoostManager(playingField) {
//        override def getRevertStrategy: IRevertStrategy = revertStrategy
//      }
//      val actionManager = mock[IActionManager]
//
//      when(actionManager.getBoostManager).thenReturn(boostManager)
//      when(playingField.getActionManager).thenReturn(actionManager)
//
//      // Setup roles and data manager
//      when(playingField.getRoles).thenReturn(rolesManager)
//      when(playingField.getDataManager).thenReturn(dataManager)
//      when(playingField.getScores).thenReturn(scores)
//
//      when(rolesManager.attacker).thenReturn(attacker)
//      when(rolesManager.defender).thenReturn(defender)
//
//      // Stubbing hand cards
//      when(dataManager.getPlayerHand(attacker)).thenReturn(attackerHand)
//      when(dataManager.getPlayerHand(defender)).thenReturn(defenderHand)
//
//      // Attacker and defender hand stubbing
//      when(attackerHand.removeLastCard()).thenReturn(attackingCard, extraAttackerCard)
//      when(defenderHand.removeLastCard()).thenReturn(extraDefenderCard)
//
//      // Stubbing defender status and cards
//      when(dataManager.allDefendersBeaten(defender)).thenReturn(false)
//      when(dataManager.getDefenderCard(defender, 0)).thenReturn(defenderCard)
//      when(attackingCard.compare(defenderCard)).thenReturn(0) // tie
//      when(extraAttackerCard.compare(extraDefenderCard)).thenReturn(1) // attacker wins tiebreaker
//
//      // Stubbing revert and remove calls
//      when(revertStrategy.revertCard(defenderCard)).thenReturn(defenderCard)
//      doNothing().when(dataManager).removeDefenderCard(defender, defenderCard)
//      doNothing().when(dataManager).removeDefenderCard(defender, defenderCard)
//      doNothing().when(attackerHand).addCard(any())
//
//      // Now execute the strategy
//      val strategy = new SingleAttackStrategy(0)
//      val result = strategy.execute(playingField)
//
//      // Log values for debugging purposes
//      println(s"Attacking Card: $attackingCard")
//      println(s"Defender Card: $defenderCard")
//      println(s"Extra Attacker Card: $extraAttackerCard")
//      println(s"Extra Defender Card: $extraDefenderCard")
//      println(s"Comparison of extra cards: ${extraAttackerCard.compare(extraDefenderCard)}")
//
//      // The result should be true, as the attacker wins after the tie-break
//      result shouldBe true
//
//      // Verifying the correct event notifications
//      verify(playingField).notifyObservers(TieComparisonEvent(attackingCard, defenderCard, extraAttackerCard, extraDefenderCard))
//      verify(playingField).notifyObservers(Events.TieComparison)
//      verify(dataManager, times(2)).removeDefenderCard(defender, defenderCard)
//    }
  }
}
