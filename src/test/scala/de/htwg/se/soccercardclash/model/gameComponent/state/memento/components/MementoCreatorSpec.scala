package de.htwg.se.soccercardclash.model.gameComponent.state.memento.components

import de.htwg.se.soccercardclash.model.gameComponent.state.memento.factory.IMementoManagerFactory
import de.htwg.se.soccercardclash.model.gameComponent.state.memento.IMementoManager
import de.htwg.se.soccercardclash.model.gameComponent.state.memento.base.Memento
import de.htwg.se.soccercardclash.model.gameComponent.state.memento.components.*
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
import de.htwg.se.soccercardclash.model.cardComponent.dataStructure.*
import de.htwg.se.soccercardclash.model.gameComponent.state.components.{IDataManager, IRoles, IScores}
import de.htwg.se.soccercardclash.util.{Events, Observable, ObservableEvent}
import org.mockito.ArgumentMatchers.any
import de.htwg.se.soccercardclash.model.gameComponent.state.strategy.scoringStrategy.IPlayerScores

import scala.collection.mutable
class MementoCreatorSpec extends AnyWordSpec with Matchers with MockitoSugar {

  "MementoCreator" should {

    "create a memento with correct field, hand, scores and action states" in {
      // Mocks
      val game = mock[IGame]
      val playingField = mock[IGameState]
      val dataManager = mock[IDataManager]
      val scores = mock[IScores]

      val attacker = mock[IPlayer]
      val defender = mock[IPlayer]

      val card1 = mock[ICard]
      val card2 = mock[ICard]
      val gk1 = mock[ICard]
      val gk2 = mock[ICard]

      val hand1 = mock[IHandCardsQueue]
      val hand2 = mock[IHandCardsQueue]

      // Dummy values for copied cards
      val copiedCard1 = mock[ICard]
      val copiedCard2 = mock[ICard]
      val copiedGK1 = mock[ICard]
      val copiedGK2 = mock[ICard]
      val copiedHandCard1 = mock[ICard]
      val copiedHandCard2 = mock[ICard]

      // Stub behavior
      when(game.getPlayingField).thenReturn(playingField)
      when(playingField.getDataManager).thenReturn(dataManager)
      when(playingField.getScores).thenReturn(scores)
      val mockRoles = mock[IRoles]
      when(mockRoles.attacker).thenReturn(attacker)
      when(mockRoles.defender).thenReturn(defender)
      when(playingField.getRoles).thenReturn(mockRoles)


      when(dataManager.getPlayerDefenders(attacker)).thenReturn(List(card1))
      when(dataManager.getPlayerDefenders(defender)).thenReturn(List(card2))
      when(card1.copy()).thenReturn(copiedCard1)
      when(card2.copy()).thenReturn(copiedCard2)

      when(dataManager.getPlayerGoalkeeper(attacker)).thenReturn(Some(gk1))
      when(dataManager.getPlayerGoalkeeper(defender)).thenReturn(Some(gk2))
      when(gk1.copy()).thenReturn(copiedGK1)
      when(gk2.copy()).thenReturn(copiedGK2)

      when(copiedHandCard1.copy()).thenReturn(copiedHandCard1)
      when(copiedHandCard2.copy()).thenReturn(copiedHandCard2)

      val handQueue1 = new HandCardsQueue(List(copiedHandCard1))
      val handQueue2 = new HandCardsQueue(List(copiedHandCard2))

      when(dataManager.getPlayerHand(attacker)).thenReturn(handQueue1)
      when(dataManager.getPlayerHand(defender)).thenReturn(handQueue2)


      when(scores.getScorePlayer1).thenReturn(5)
      when(scores.getScorePlayer2).thenReturn(3)

      val actionStates1 = Map(PlayerActionPolicies.Boost -> CanPerformAction(2), PlayerActionPolicies.Swap -> OutOfActions)
      val actionStates2 = Map(PlayerActionPolicies.Boost -> OutOfActions, PlayerActionPolicies.Swap -> CanPerformAction(1))

      when(attacker.actionStates).thenReturn(actionStates1)
      when(defender.actionStates).thenReturn(actionStates2)

      // Act
      val creator = new MementoCreator(game)
      val memento = creator.createMemento()

      // Assert
      memento.attacker shouldBe attacker
      memento.defender shouldBe defender
      memento.player1Defenders shouldBe List(copiedCard1)
      memento.player2Defenders shouldBe List(copiedCard2)
      memento.player1Goalkeeper shouldBe Some(copiedGK1)
      memento.player2Goalkeeper shouldBe Some(copiedGK2)
      memento.player1Hand shouldBe List(copiedHandCard1)
      memento.player2Hand shouldBe List(copiedHandCard2)
      memento.player1Score shouldBe 5
      memento.player2Score shouldBe 3
      memento.player1Actions shouldBe Map(PlayerActionPolicies.Boost -> 2, PlayerActionPolicies.Swap -> 0)
      memento.player2Actions shouldBe Map(PlayerActionPolicies.Boost -> 0, PlayerActionPolicies.Swap -> 1)
    }
  }
}
