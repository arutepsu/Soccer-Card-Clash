package de.htwg.se.soccercardclash.model.gameComponent.service

import de.htwg.se.soccercardclash.model.cardComponent.ICard
import de.htwg.se.soccercardclash.model.cardComponent.dataStructure.IHandCardsQueue
import de.htwg.se.soccercardclash.model.cardComponent.factory.{DeckFactory, IDeckFactory}
import de.htwg.se.soccercardclash.model.gameComponent.state.IGameState
import de.htwg.se.soccercardclash.model.gameComponent.state.components.{IGameCards, IGameCardsFactory, IRoles, IRolesFactory, IScores, IScoresFactory, Roles}
import de.htwg.se.soccercardclash.model.playerComponent.IPlayer
import de.htwg.se.soccercardclash.model.playerComponent.factory.IPlayerFactory
import org.mockito.Mockito.*
import org.mockito.ArgumentMatchers.*
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.mockito.MockitoSugar
import scala.collection.mutable

class GameInitializerSpec extends AnyWordSpec with Matchers with MockitoSugar {

  "GameInitializer" should {

    "create a new game state with two players" in {
      val mockPlayer1 = mock[IPlayer]
      val mockPlayer2 = mock[IPlayer]
      val mockCard = mock[ICard]
      val mockDeck = mock[mutable.Queue[ICard]]
      val mockGameCards = mock[IGameCards]
      val mockRoles = mock[IRoles]
      val mockScores = mock[IScores]

      val mockPlayerFactory = mock[IPlayerFactory]
      val mockDeckFactory = mock[IDeckFactory]
      val mockGameCardsFactory = mock[IGameCardsFactory]
      val mockRolesFactory = mock[IRolesFactory]
      val mockScoresFactory = mock[IScoresFactory]

      when(mockPlayerFactory.createPlayer("Alice")).thenReturn(mockPlayer1)
      when(mockPlayerFactory.createPlayer("Bob")).thenReturn(mockPlayer2)

      when(mockDeckFactory.createDeck()).thenReturn(mockDeck)
      when(mockDeck.dequeue()).thenReturn(mockCard)

      when(mockGameCardsFactory.createFromData(
        any(), any(), any(), any(), any(), any(), any(), any())
      ).thenReturn(mockGameCards)

      when(mockRolesFactory.create(mockPlayer1, mockPlayer2)).thenReturn(mockRoles)
      when(mockScoresFactory.create(Seq(mockPlayer1, mockPlayer2))).thenReturn(mockScores)

      val initializer = new GameInitializer(
        mockPlayerFactory, mockDeckFactory,
        mockGameCardsFactory, mockRolesFactory, mockScoresFactory
      )

      val gameState = initializer.createGameState("Alice", "Bob")

      gameState.getGameCards shouldBe mockGameCards
      gameState.getRoles shouldBe mockRoles
      gameState.getScores shouldBe mockScores
    }

    "initialize from existing state" in {
      val mockRoles = mock[IRoles]
      val mockScores = mock[IScores]

      val gameCardsFactory = mock[IGameCardsFactory]
      val rolesFactory = mock[IRolesFactory]
      val scoresFactory = mock[IScoresFactory]

      val mockFactory = new GameInitializer(mock[IPlayerFactory], mock[IDeckFactory], gameCardsFactory, rolesFactory, scoresFactory)

      val attacker = mock[IPlayer]
      val defender = mock[IPlayer]
      val mockGameCards = mock[IGameCards]
      val mockState = mock[IGameState]
      val attackerHandQueue = mock[IHandCardsQueue]
      val defenderHandQueue = mock[IHandCardsQueue]

      when(attackerHandQueue.toList).thenReturn(List(mock[ICard]))
      when(defenderHandQueue.toList).thenReturn(List(mock[ICard]))
      // ✅ mock getRoles and getGameCards
      when(mockState.getRoles).thenReturn(Roles(attacker, defender))
      when(mockState.getGameCards).thenReturn(mockGameCards)

      when(mockGameCards.getPlayerHand(attacker)).thenReturn(attackerHandQueue)
      when(mockGameCards.getPlayerHand(defender)).thenReturn(defenderHandQueue)

      when(mockGameCards.getPlayerDefenders(attacker)).thenReturn(List(None, None, None))
      when(mockGameCards.getPlayerDefenders(defender)).thenReturn(List(None, None, None))

      when(mockGameCards.getPlayerGoalkeeper(attacker)).thenReturn(None)
      when(mockGameCards.getPlayerGoalkeeper(defender)).thenReturn(None)

      // ✅ mock getScores
      when(mockState.getScores).thenReturn(mockScores)
      when(mockScores.getScore(attacker)).thenReturn(1)
      when(mockScores.getScore(defender)).thenReturn(2)

      when(gameCardsFactory.createFromData(any(), any(), any(), any(), any(), any(), any(), any())).thenReturn(mockGameCards)
      when(rolesFactory.create(attacker, defender)).thenReturn(mockRoles)
      when(scoresFactory.createWithScores(any())).thenReturn(mockScores)

      val result = mockFactory.initializeFromState(mockState)

      result.getGameCards shouldBe mockGameCards
    }


    "create a game state with an AI player" in {
      val mockHuman = mock[IPlayer]
      val mockAI = mock[IPlayer]
      val mockDeck = mock[mutable.Queue[ICard]]
      val mockCard = mock[ICard]
      val mockGameCards = mock[IGameCards]
      val mockRoles = mock[IRoles]
      val mockScores = mock[IScores]

      val playerFactory = mock[IPlayerFactory]
      val deckFactory = mock[IDeckFactory]
      val cardsFactory = mock[IGameCardsFactory]
      val rolesFactory = mock[IRolesFactory]
      val scoresFactory = mock[IScoresFactory]

      when(playerFactory.createPlayer("You")).thenReturn(mockHuman)
      when(deckFactory.createDeck()).thenReturn(mockDeck)
      when(deckFactory.shuffleDeck(mockDeck)).thenAnswer(_ => ())
      when(mockDeck.dequeue()).thenReturn(mockCard)

      when(cardsFactory.createFromData(
        any(), any(), any(), any(), any(), any(), any(), any())
      ).thenReturn(mockGameCards)

      when(rolesFactory.create(mockHuman, mockAI)).thenReturn(mockRoles)
      when(scoresFactory.create(Seq(mockHuman, mockAI))).thenReturn(mockScores)

      val initializer = new GameInitializer(playerFactory, deckFactory, cardsFactory, rolesFactory, scoresFactory)

      val result = initializer.createGameStateWithAI("You", mockAI)

      result.getRoles shouldBe mockRoles
      result.getScores shouldBe mockScores
    }
  }
}
