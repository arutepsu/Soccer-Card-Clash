package de.htwg.se.soccercardclash.model.gameComponent.service

import de.htwg.se.soccercardclash.model.cardComponent.ICard
import de.htwg.se.soccercardclash.model.cardComponent.dataStructure.IHandCardsQueue
import de.htwg.se.soccercardclash.model.cardComponent.factory.{DeckFactory, IDeckFactory}
import de.htwg.se.soccercardclash.model.gameComponent.IGameState
import de.htwg.se.soccercardclash.model.gameComponent.components.{IGameCards, IGameCardsFactory, IRoles, IRolesFactory, IScores, IScoresFactory, Roles}
import de.htwg.se.soccercardclash.model.playerComponent.IPlayer
import de.htwg.se.soccercardclash.model.playerComponent.ai.types.BitstormStrategy
import de.htwg.se.soccercardclash.model.playerComponent.factory.PlayerBuilder
import de.htwg.se.soccercardclash.model.playerComponent.util.IRandomProvider
import org.mockito.Mockito.*
import org.mockito.ArgumentMatchers.*
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.mockito.MockitoSugar

import scala.collection.mutable

class GameInitializerSpec extends AnyWordSpec with Matchers with MockitoSugar {

  "GameInitializer" should {

    "create a new game state with two players" in {
      val player1 = PlayerBuilder().withName("Alice").asHuman().withDefaultLimits().build()
      val player2 = PlayerBuilder().withName("Bob").asHuman().withDefaultLimits().build()

      val mockCard = mock[ICard]
      val mockDeck = mock[mutable.Queue[ICard]]
      val mockGameCards = mock[IGameCards]
      val mockRoles = mock[IRoles]
      val mockScores = mock[IScores]

      val mockDeckFactory = mock[IDeckFactory]
      val mockGameCardsFactory = mock[IGameCardsFactory]
      val mockRolesFactory = mock[IRolesFactory]
      val mockScoresFactory = mock[IScoresFactory]

      when(mockDeckFactory.createDeck()).thenReturn(mockDeck)
      when(mockDeck.dequeue()).thenReturn(mockCard)
      when(mockGameCardsFactory.createFromData(any(), any(), any(), any(), any(), any(), any(), any()))
        .thenReturn(mockGameCards)
      when(mockRolesFactory.create(player1, player2)).thenReturn(mockRoles)
      when(mockScoresFactory.create(Seq(player1, player2))).thenReturn(mockScores)

      val initializer = new GameInitializer(
        mockDeckFactory, mockGameCardsFactory, mockRolesFactory, mockScoresFactory
      )

      val result = initializer.createGameState(player1.name, player2.name)

      result.getGameCards shouldBe mockGameCards
      result.getRoles shouldBe mockRoles
      result.getScores shouldBe mockScores
    }

    "initialize from existing state" in {
      val mockState = mock[IGameState]
      val mockGameCards = mock[IGameCards]
      val mockScores = mock[IScores]
      val mockRoles = mock[IRoles]

      val attacker = PlayerBuilder().withName("Attacker").asHuman().build()
      val defender = PlayerBuilder().withName("Defender").asHuman().build()

      val attackerHand = mock[IHandCardsQueue]
      val defenderHand = mock[IHandCardsQueue]
      when(attackerHand.toList).thenReturn(List(mock[ICard]))
      when(defenderHand.toList).thenReturn(List(mock[ICard]))

      when(mockState.getRoles).thenReturn(Roles(attacker, defender))
      when(mockState.getGameCards).thenReturn(mockGameCards)
      when(mockGameCards.getPlayerHand(attacker)).thenReturn(attackerHand)
      when(mockGameCards.getPlayerHand(defender)).thenReturn(defenderHand)
      when(mockGameCards.getPlayerDefenders(attacker)).thenReturn(List(None, None, None))
      when(mockGameCards.getPlayerDefenders(defender)).thenReturn(List(None, None, None))
      when(mockGameCards.getPlayerGoalkeeper(attacker)).thenReturn(None)
      when(mockGameCards.getPlayerGoalkeeper(defender)).thenReturn(None)
      when(mockState.getScores).thenReturn(mockScores)
      when(mockScores.getScore(attacker)).thenReturn(1)
      when(mockScores.getScore(defender)).thenReturn(2)

      val cardsFactory = mock[IGameCardsFactory]
      val rolesFactory = mock[IRolesFactory]
      val scoresFactory = mock[IScoresFactory]

      when(cardsFactory.createFromData(any(), any(), any(), any(), any(), any(), any(), any()))
        .thenReturn(mockGameCards)
      when(rolesFactory.create(attacker, defender)).thenReturn(mockRoles)
      when(scoresFactory.createWithScores(any())).thenReturn(mockScores)

      val initializer = new GameInitializer(
        mock[IDeckFactory],
        cardsFactory, rolesFactory, scoresFactory
      )

      val result = initializer.initializeFromState(mockState)

      result.getGameCards shouldBe mockGameCards
    }


    "create a game state with an AI player" in {
      val human = PlayerBuilder().withName("You").asHuman().withDefaultLimits().build()
      val ai = PlayerBuilder()
        .withName("AI")
        .asAI(new BitstormStrategy(mock[IRandomProvider]))
        .withDefaultLimits()
        .build()

      val mockCard = mock[ICard]
      val mockDeck = mock[mutable.Queue[ICard]]
      val mockGameCards = mock[IGameCards]
      val mockRoles = mock[IRoles]
      val mockScores = mock[IScores]

      val mockDeckFactory = mock[IDeckFactory]
      val mockGameCardsFactory = mock[IGameCardsFactory]
      val mockRolesFactory = mock[IRolesFactory]
      val mockScoresFactory = mock[IScoresFactory]

      when(mockDeckFactory.createDeck()).thenReturn(mockDeck)
      when(mockDeckFactory.shuffleDeck(mockDeck)).thenAnswer(_ => ())
      when(mockDeck.dequeue()).thenReturn(mockCard)
      when(mockGameCardsFactory.createFromData(any(), any(), any(), any(), any(), any(), any(), any()))
        .thenReturn(mockGameCards)
      when(mockRolesFactory.create(human, ai)).thenReturn(mockRoles)
      when(mockScoresFactory.create(Seq(human, ai))).thenReturn(mockScores)

      val initializer = new GameInitializer(
        mockDeckFactory, mockGameCardsFactory, mockRolesFactory, mockScoresFactory
      )

      val result = initializer.createGameStateWithAI(human.name, ai)

      result.getRoles shouldBe mockRoles
      result.getScores shouldBe mockScores
    }

  }
}
