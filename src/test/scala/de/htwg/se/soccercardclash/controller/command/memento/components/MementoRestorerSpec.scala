package de.htwg.se.soccercardclash.controller.command.memento.components

import de.htwg.se.soccercardclash.controller.Events
import de.htwg.se.soccercardclash.controller.command.memento.base.Memento
import de.htwg.se.soccercardclash.controller.command.memento.componenets.{IMementoCreator, IMementoRestorer, MementoRestorer}
import de.htwg.se.soccercardclash.model.cardComponent.base.types.{BoostedCard, RegularCard}
import de.htwg.se.soccercardclash.model.gameComponent.IGame
import org.mockito.Mockito.*
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.mockito.MockitoSugar
import de.htwg.se.soccercardclash.model.playingFiledComponent.IPlayingField
import de.htwg.se.soccercardclash.model.playingFiledComponent.manager.*
import de.htwg.se.soccercardclash.model.playingFiledComponent.strategy.boostStrategy.*
import de.htwg.se.soccercardclash.model.playerComponent.IPlayer
import de.htwg.se.soccercardclash.model.playerComponent.playerAction.*
import de.htwg.se.soccercardclash.model.cardComponent.ICard
import de.htwg.se.soccercardclash.model.cardComponent.dataStructure.HandCardsQueue
import de.htwg.se.soccercardclash.util.{Observable, ObservableEvent}
import org.mockito.ArgumentMatchers.any
import de.htwg.se.soccercardclash.model.playingFiledComponent.strategy.scoringStrategy.IPlayerScores
class ObservableMockPlayingField extends Observable with IPlayingField {
  var lastObservedEvent: Option[ObservableEvent] = None

  override def notifyObservers(e: ObservableEvent): Unit = {
    lastObservedEvent = Some(e)
    super.notifyObservers(e)
  }

  // All other methods throw by default unless overridden
  override def getRoles: IRolesManager = ???
  override def getDataManager: IDataManager = ???
  override def getScores: IPlayerScores = ???
  override def getActionManager: IActionManager = ???
  override def getAttacker: IPlayer = ???
  override def getDefender: IPlayer = ???
  override def reset(): Unit = {}
  override def setPlayingField(): Unit = {}
}


class MementoRestorerSpec extends AnyWordSpec with Matchers with MockitoSugar {

  "MementoRestorer" should {

    "restore boosted card using revert strategy and update attacker actions and roles" in {
      // Create mocks
      val dataManager = mock[IDataManager]
      val rolesManager = mock[IRolesManager]
      val actionManager = mock[IActionManager]
      val boostManager = mock[IBoostManager]
      val revertStrategy = mock[IRevertStrategy]
      val attacker = mock[IPlayer]
      val defender = mock[IPlayer]
      val boostedCard = mock[BoostedCard]
      val revertedCard = mock[ICard]
      val defenders = List(mock[ICard], boostedCard, mock[ICard])
      val updatedDefenders = defenders.updated(1, revertedCard)
      val memento = mock[Memento]

      // Setup mock behavior
      when(memento.attacker).thenReturn(attacker)
      when(memento.player1Actions).thenReturn(Map(PlayerActionPolicies.Boost -> 1))
      val actionStates = Map(PlayerActionPolicies.Boost -> OutOfActions)
      when(attacker.actionStates).thenReturn(actionStates)
      when(attacker.setActionStates(any())).thenReturn(attacker)
      when(dataManager.getPlayerDefenders(attacker)).thenReturn(defenders)
      when(revertStrategy.revertCard(boostedCard)).thenReturn(revertedCard)
      when(actionManager.getBoostManager).thenReturn(boostManager)
      when(boostManager.getRevertStrategy).thenReturn(revertStrategy)
      when(rolesManager.defender).thenReturn(defender)

      // Override methods to inject mocks
      val playingField = new ObservableMockPlayingField {
        override def getDataManager: IDataManager = dataManager

        override def getRoles: IRolesManager = rolesManager

        override def getActionManager: IActionManager = actionManager
      }

      val game = mock[IGame]
      when(game.getPlayingField).thenReturn(playingField)
      when(game.getActionManager).thenReturn(actionManager)

      val restorer = new MementoRestorer(game)

      // Act
      restorer.restoreBoosts(memento, 1)

      // Assert
      verify(dataManager).setPlayerDefenders(attacker, updatedDefenders)
      verify(rolesManager).setRoles(attacker, defender)
      playingField.lastObservedEvent shouldBe Some(Events.Reverted)
    }
    "restore goalkeeper boost from memento and update roles and observers" in {
      val dataManager = mock[IDataManager]
      val rolesManager = mock[IRolesManager]
      val actionManager = mock[IActionManager]
      val boostManager = mock[IBoostManager]
      val revertStrategy = mock[IRevertStrategy]

      val attacker = mock[IPlayer]
      val defender = mock[IPlayer]
      val goalkeeper = mock[RegularCard]
      val revertedGK = mock[ICard]

      val memento = mock[Memento]
      when(memento.attacker).thenReturn(attacker)
      when(memento.player1Goalkeeper).thenReturn(Some(goalkeeper))
      when(memento.player1Actions).thenReturn(Map(PlayerActionPolicies.Boost -> 1))

      val actionStates = Map(PlayerActionPolicies.Boost -> OutOfActions)
      when(attacker.actionStates).thenReturn(actionStates)
      when(attacker.setActionStates(any())).thenReturn(attacker)
      when(rolesManager.defender).thenReturn(defender)

      when(revertStrategy.revertCard(goalkeeper)).thenReturn(revertedGK)

      val playingField = new ObservableMockPlayingField {
        override def getDataManager: IDataManager = dataManager
        override def getRoles: IRolesManager = rolesManager
        override def getActionManager: IActionManager = actionManager
      }

      when(actionManager.getBoostManager).thenReturn(boostManager)
      when(boostManager.getRevertStrategy).thenReturn(revertStrategy)

      val game = mock[IGame]
      when(game.getPlayingField).thenReturn(playingField)
      when(game.getActionManager).thenReturn(actionManager)

      val restorer = new MementoRestorer(game)

      // Act
      restorer.restoreGoalkeeperBoost(memento)

      // Assert
      verify(dataManager).setPlayerGoalkeeper(attacker, Some(revertedGK))
      verify(rolesManager).setRoles(attacker, defender)
      playingField.lastObservedEvent shouldBe Some(Events.Reverted)
    }
    "restore full game state from memento" in {
      val dataManager = mock[IDataManager]
      val rolesManager = mock[IRolesManager]
      val scores = mock[IPlayerScores]

      val attacker = mock[IPlayer]
      val defender = mock[IPlayer]
      val restoredP1 = mock[IPlayer]
      val restoredP2 = mock[IPlayer]

      val defCard1 = mock[ICard]; when(defCard1.copy()).thenReturn(defCard1)
      val defCard2 = mock[ICard]; when(defCard2.copy()).thenReturn(defCard2)
      val gk1 = mock[ICard]; when(gk1.copy()).thenReturn(gk1)
      val gk2 = mock[ICard]; when(gk2.copy()).thenReturn(gk2)
      val h1 = mock[ICard]; when(h1.copy()).thenReturn(h1)
      val h2 = mock[ICard]; when(h2.copy()).thenReturn(h2)

      val hand1 = new HandCardsQueue(List.empty)
      val hand2 = new HandCardsQueue(List.empty)

      val memento = mock[Memento]
      when(memento.attacker).thenReturn(attacker)
      when(memento.defender).thenReturn(defender)
      when(memento.player1Defenders).thenReturn(List(defCard1))
      when(memento.player2Defenders).thenReturn(List(defCard2))
      when(memento.player1Goalkeeper).thenReturn(Some(gk1))
      when(memento.player2Goalkeeper).thenReturn(Some(gk2))
      when(memento.player1Hand).thenReturn(List(h1))
      when(memento.player2Hand).thenReturn(List(h2))
      when(memento.player1Score).thenReturn(1)
      when(memento.player2Score).thenReturn(2)
      when(memento.player1Actions).thenReturn(Map(PlayerActionPolicies.Boost -> 1))
      when(memento.player2Actions).thenReturn(Map(PlayerActionPolicies.Swap -> 2))

      when(attacker.setActionStates(any())).thenReturn(restoredP1)
      when(defender.setActionStates(any())).thenReturn(restoredP2)

      val playingField = new ObservableMockPlayingField {
        override def getDataManager: IDataManager = dataManager
        override def getRoles: IRolesManager = rolesManager
        override def getScores: IPlayerScores = scores
      }

      when(dataManager.getPlayerHand(attacker)).thenReturn(hand1)
      when(dataManager.getPlayerHand(defender)).thenReturn(hand2)

      val game = mock[IGame]
      when(game.getPlayingField).thenReturn(playingField)

      val restorer = new MementoRestorer(game)

      // Act
      restorer.restoreGameState(memento)

      // Assert
      verify(dataManager).setPlayerDefenders(attacker, List(defCard1))
      verify(dataManager).setPlayerDefenders(defender, List(defCard2))
      verify(dataManager).setPlayerGoalkeeper(attacker, Some(gk1))
      verify(dataManager).setPlayerGoalkeeper(defender, Some(gk2))
      hand1 should contain theSameElementsAs List(h1)
      hand2 should contain theSameElementsAs List(h2)
      verify(scores).setScorePlayer1(1)
      verify(scores).setScorePlayer2(2)
      verify(rolesManager).setRoles(restoredP1, restoredP2)
      playingField.lastObservedEvent.isDefined shouldBe true
    }

  }
}
