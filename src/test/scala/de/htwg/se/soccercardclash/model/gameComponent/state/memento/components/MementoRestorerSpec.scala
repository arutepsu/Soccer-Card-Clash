package de.htwg.se.soccercardclash.model.gameComponent.state.memento.components

import de.htwg.se.soccercardclash.model.gameComponent.state.memento.factory.IMementoManagerFactory
import de.htwg.se.soccercardclash.model.gameComponent.state.memento.IMementoManager
import de.htwg.se.soccercardclash.model.gameComponent.state.memento.base.Memento
import de.htwg.se.soccercardclash.model.cardComponent.base.types.{BoostedCard, RegularCard}
import de.htwg.se.soccercardclash.model.gameComponent.IGame
import org.mockito.Mockito.*
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.mockito.MockitoSugar
import de.htwg.se.soccercardclash.model.gameComponent.state.IGameState
import de.htwg.se.soccercardclash.model.gameComponent.state.manager.*
import de.htwg.se.soccercardclash.model.gameComponent.state.strategy.boostStrategy.*
import de.htwg.se.soccercardclash.model.playerComponent.IPlayer
import de.htwg.se.soccercardclash.model.playerComponent.playerAction.*
import de.htwg.se.soccercardclash.model.cardComponent.ICard
import de.htwg.se.soccercardclash.model.cardComponent.dataStructure.HandCardsQueue
import de.htwg.se.soccercardclash.model.gameComponent.state.memento.components.*
import de.htwg.se.soccercardclash.util.{Events, Observable, ObservableEvent}
import org.mockito.ArgumentMatchers.any
import de.htwg.se.soccercardclash.model.cardComponent.dataStructure.IHandCardsQueueFactory
import de.htwg.se.soccercardclash.model.gameComponent.state.components.{IGameCards, IRoles, IScores}
import de.htwg.se.soccercardclash.model.gameComponent.state.strategy.scoringStrategy.IPlayerScores
class ObservableMockGameState extends Observable with IGameState {

  var lastObservedEvent: Option[ObservableEvent] = None

  override def notifyObservers(e: ObservableEvent): Unit = {
    lastObservedEvent = Some(e)
    super.notifyObservers(e)
  }

  // Provide mocks instead of throwing
  private val dummyRoles = mock(classOf[IRoles])
  private val dummyDataManager = mock(classOf[IGameCards])
  private val dummyScores = mock(classOf[IScores])
  private val dummyActionManager = mock(classOf[IActionManager])
  private val dummyPlayer = mock(classOf[IPlayer])

  override def getRoles: IRoles = dummyRoles
  override def getGameCards: IGameCards = dummyDataManager
  override def getScores: IScores = dummyScores
  override def getActionManager: IActionManager = dummyActionManager

  override def reset(): Unit = {}
  override def setPlayingField(): Unit = {}
}


class MementoRestorerSpec extends AnyWordSpec with Matchers with MockitoSugar {

  "MementoRestorer" should {

    "restore boosted card using revert strategy and update attacker actions and roles" in {
      // Create mocks
      val dataManager = mock[IGameCards]
      val rolesManager = mock[IRoles]
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
      val handCardsQueueFactory = mock[IHandCardsQueueFactory]


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
      val playingField = new ObservableMockGameState {
        override def getGameCards: IGameCards = dataManager

        override def getRoles: IRoles = rolesManager

        override def getActionManager: IActionManager = actionManager
      }

      val game = mock[IGame]
      when(game.getPlayingField).thenReturn(playingField)
      when(game.getActionManager).thenReturn(actionManager)

      val restorer = new MementoRestorer(game, handCardsQueueFactory)

      // Act
      restorer.restoreBoosts(memento, 1)

      // Assert
      verify(dataManager).newPlayerDefenders(attacker, updatedDefenders)
      verify(rolesManager).newRoles(attacker, defender)
      playingField.lastObservedEvent shouldBe Some(Events.Reverted)
    }
    "restore goalkeeper boost from memento and update roles and observers" in {
      val dataManager = mock[IGameCards]
      val rolesManager = mock[IRoles]
      val actionManager = mock[IActionManager]
      val boostManager = mock[IBoostManager]
      val revertStrategy = mock[IRevertStrategy]

      val attacker = mock[IPlayer]
      val defender = mock[IPlayer]
      val goalkeeper = mock[RegularCard]
      val revertedGK = mock[ICard]
      val handCardsQueueFactory = mock[IHandCardsQueueFactory]

      val memento = mock[Memento]
      when(memento.attacker).thenReturn(attacker)
      when(memento.player1Goalkeeper).thenReturn(Some(goalkeeper))
      when(memento.player1Actions).thenReturn(Map(PlayerActionPolicies.Boost -> 1))

      val actionStates = Map(PlayerActionPolicies.Boost -> OutOfActions)
      when(attacker.actionStates).thenReturn(actionStates)
      when(attacker.setActionStates(any())).thenReturn(attacker)
      when(rolesManager.defender).thenReturn(defender)

      when(revertStrategy.revertCard(goalkeeper)).thenReturn(revertedGK)

      val playingField = new ObservableMockGameState {
        override def getGameCards: IGameCards = dataManager

        override def getRoles: IRoles = rolesManager

        override def getActionManager: IActionManager = actionManager
      }

      when(actionManager.getBoostManager).thenReturn(boostManager)
      when(boostManager.getRevertStrategy).thenReturn(revertStrategy)

      val game = mock[IGame]
      when(game.getPlayingField).thenReturn(playingField)
      when(game.getActionManager).thenReturn(actionManager)

      val restorer = new MementoRestorer(game, handCardsQueueFactory)

      // Act
      restorer.restoreGoalkeeperBoost(memento)

      // Assert
      verify(dataManager).newPlayerGoalkeeper(attacker, Some(revertedGK))
      verify(rolesManager).newRoles(attacker, defender)
      playingField.lastObservedEvent shouldBe Some(Events.Reverted)
    }
    "restore full game state from memento" in {
      val dataManager = mock[IGameCards]
      val rolesManager = mock[IRoles]
      val scores = mock[IScores]
      val handCardsQueueFactory = mock[IHandCardsQueueFactory] // ✅ mocked factory

      val attacker = mock[IPlayer]
      val defender = mock[IPlayer]
      val restoredP1 = mock[IPlayer]
      val restoredP2 = mock[IPlayer]

      val defCard1 = mock[ICard];
      when(defCard1.copy()).thenReturn(defCard1)
      val defCard2 = mock[ICard];
      when(defCard2.copy()).thenReturn(defCard2)
      val gk1 = mock[ICard];
      when(gk1.copy()).thenReturn(gk1)
      val gk2 = mock[ICard];
      when(gk2.copy()).thenReturn(gk2)
      val h1 = mock[ICard];
      when(h1.copy()).thenReturn(h1)
      val h2 = mock[ICard];
      when(h2.copy()).thenReturn(h2)

      val hand1 = new HandCardsQueue(List(h1)) // ✅ now with correct content
      val hand2 = new HandCardsQueue(List(h2))

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

      // ✅ Tell the factory what to return
      when(handCardsQueueFactory.create(List(h1))).thenReturn(hand1)
      when(handCardsQueueFactory.create(List(h2))).thenReturn(hand2)

      val playingField = new ObservableMockGameState {
        override def getGameCards: IGameCards = dataManager

        override def getRoles: IRoles = rolesManager

        override def getScores: IScores = scores
      }

      val game = mock[IGame]
      when(game.getPlayingField).thenReturn(playingField)

      val restorer = new MementoRestorer(game, handCardsQueueFactory) // ✅ fixed

      // Act
      restorer.restoreGameState(memento)

      // Assert
      verify(dataManager).newPlayerDefenders(attacker, List(defCard1))
      verify(dataManager).newPlayerDefenders(defender, List(defCard2))
      verify(dataManager).newPlayerGoalkeeper(attacker, Some(gk1))
      verify(dataManager).newPlayerGoalkeeper(defender, Some(gk2))
      hand1.toList should contain theSameElementsAs List(h1) // ✅ updated check
      hand2.toList should contain theSameElementsAs List(h2)
      verify(scores).setScorePlayer1(1)
      verify(scores).setScorePlayer2(2)
      verify(rolesManager).newRoles(restoredP1, restoredP2)
      playingField.lastObservedEvent.isDefined shouldBe true
    }
  }
}
