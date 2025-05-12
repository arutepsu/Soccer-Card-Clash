package de.htwg.se.soccercardclash.model.playingFieldComponent.strategy.attackStrategy.base

import de.htwg.se.soccercardclash.model.cardComponent.ICard
import de.htwg.se.soccercardclash.model.playerComponent.IPlayer
import de.htwg.se.soccercardclash.model.playerComponent.playerAction.{CanPerformAction, OutOfActions, PlayerActionPolicies}
import de.htwg.se.soccercardclash.model.gameComponent.state.IGameState
import de.htwg.se.soccercardclash.model.cardComponent.dataStructure.*
import de.htwg.se.soccercardclash.model.gameComponent.state.components.{IDataManager, IRoles}
import de.htwg.se.soccercardclash.model.gameComponent.state.manager.{IActionManager}
import de.htwg.se.soccercardclash.model.gameComponent.state.strategy.boostStrategy.{BoostManager, IRevertStrategy}
import de.htwg.se.soccercardclash.model.gameComponent.state.strategy.scoringStrategy.IPlayerScores
import org.mockito.Mockito.*
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.mockito.MockitoSugar
import de.htwg.se.soccercardclash.util.{AttackResultEvent, ComparedCardsEvent, Events, Observable, ObservableEvent, TieComparisonEvent}
import de.htwg.se.soccercardclash.model.gameComponent.state.strategy.attackStrategy.base.SingleAttackStrategy
import org.mockito.ArgumentMatchers.any

import scala.util.{Failure, Success}

class SingleAttackStrategySpec extends AnyWordSpec with Matchers with MockitoSugar {

  class ObservableMockGameState extends Observable with IGameState {
    override def getRoles: IRoles = mock[IRoles]
    override def getDataManager: IDataManager = mock[IDataManager]
    override def getScores: IPlayerScores = mock[IPlayerScores]
    override def getActionManager: IActionManager = mock[IActionManager]
    override def reset(): Unit = {}
    override def setPlayingField(): Unit = {}
    override def notifyObservers(e: ObservableEvent): Unit = super.notifyObservers(e)
  }

  "SingleAttackStrategy" should {

    "execute a successful single attack on a defender" in {
      // Capture the observer event
      var notifiedEvent: Option[ObservableEvent] = None

      // Mocks
      val dataManager = mock[IDataManager]
      val rolesManager = mock[IRoles]
      val attacker = mock[IPlayer]
      val defender = mock[IPlayer]
      val attackerHand = mock[IHandCardsQueue]
      val updatedAttackerHand = mock[IHandCardsQueue]
      val defenderHand = mock[IHandCardsQueue]
      val revertStrategy = mock[IRevertStrategy]
      val scores = mock[IPlayerScores]
      val card = mock[ICard]
      val defenderCard = mock[ICard]
      val actionManager = mock[IActionManager]

      // Boost manager
      val playingField = new ObservableMockGameState {
        override def notifyObservers(e: ObservableEvent): Unit = {
          // Capture only the ComparedCardsEvent (first one)
          if (notifiedEvent.isEmpty && e.isInstanceOf[ComparedCardsEvent]) {
            notifiedEvent = Some(e)
          }
        }

        override def getActionManager: IActionManager = actionManager
        override def getRoles: IRoles = rolesManager
        override def getDataManager: IDataManager = dataManager
        override def getScores: IPlayerScores = scores
      }

      val boostManager = new BoostManager(playingField) {
        override def getRevertStrategy: IRevertStrategy = revertStrategy
      }

      // Wiring mocks
      when(actionManager.getBoostManager).thenReturn(boostManager)
      when(rolesManager.attacker).thenReturn(attacker)
      when(rolesManager.defender).thenReturn(defender)
      when(dataManager.getPlayerHand(attacker)).thenReturn(attackerHand)
      when(dataManager.getPlayerHand(defender)).thenReturn(defenderHand)
      when(attackerHand.removeLastCard()).thenReturn(Success((card, updatedAttackerHand)))
      when(dataManager.allDefendersBeaten(defender)).thenReturn(false)
      when(dataManager.getDefenderCard(defender, 0)).thenReturn(defenderCard)
      when(card.compare(defenderCard)).thenReturn(1)
      when(defenderCard.valueToInt).thenReturn(5)
      when(revertStrategy.revertCard(defenderCard)).thenReturn(defenderCard)

      doNothing().when(dataManager).removeDefenderCard(defender, defenderCard)
      doReturn(updatedAttackerHand).when(updatedAttackerHand).addCard(card)

      // Execute strategy
      val strategy = new SingleAttackStrategy(0)
      val result = strategy.execute(playingField)

      // ✅ Assertions
      result shouldBe true

      notifiedEvent match {
        case Some(ComparedCardsEvent(actualAttackerCard, actualDefenderCard)) =>
          actualAttackerCard shouldBe card
          actualDefenderCard shouldBe defenderCard
        case Some(other) =>
          fail(s"Expected ComparedCardsEvent but got $other")
        case None =>
          fail("No event was notified")
      }

      verify(updatedAttackerHand).addCard(card)
      verify(dataManager, times(2)).removeDefenderCard(defender, defenderCard)
    }
  }
}
