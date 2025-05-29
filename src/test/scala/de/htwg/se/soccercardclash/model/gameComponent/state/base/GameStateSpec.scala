package de.htwg.se.soccercardclash.model.gameComponent.state.base

import de.htwg.se.soccercardclash.model.gameComponent.IGameState
import de.htwg.se.soccercardclash.model.gameComponent.state.components.{IGameCards, IRoles, IScores}
import de.htwg.se.soccercardclash.model.playerComponent.IPlayer
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.mockito.MockitoSugar
import org.mockito.Mockito._

class GameStateSpec extends AnyWordSpec with Matchers with MockitoSugar {

  "A GameState" should {

    "return its initial components" in {
      val cards = mock[IGameCards]
      val roles = mock[IRoles]
      val scores = mock[IScores]

      val state = GameState(cards, roles, scores)

      state.getGameCards shouldBe cards
      state.getRoles shouldBe roles
      state.getScores shouldBe scores
    }

    "return a new GameState when updating game cards" in {
      val cards = mock[IGameCards]
      val newCards = mock[IGameCards]
      val roles = mock[IRoles]
      val scores = mock[IScores]

      val state = GameState(cards, roles, scores)
      val updated = state.newGameCards(newCards)

      updated.getGameCards shouldBe newCards
      updated.getRoles shouldBe roles
      updated.getScores shouldBe scores
    }

    "return a new GameState when updating roles" in {
      val cards = mock[IGameCards]
      val roles = mock[IRoles]
      val newRoles = mock[IRoles]
      val scores = mock[IScores]

      val state = GameState(cards, roles, scores)
      val updated = state.newRoles(newRoles)

      updated.getGameCards shouldBe cards
      updated.getRoles shouldBe newRoles
      updated.getScores shouldBe scores
    }

    "return a new GameState when updating scores" in {
      val cards = mock[IGameCards]
      val roles = mock[IRoles]
      val scores = mock[IScores]
      val newScores = mock[IScores]

      val state = GameState(cards, roles, scores)
      val updated = state.newScores(newScores)

      updated.getGameCards shouldBe cards
      updated.getRoles shouldBe roles
      updated.getScores shouldBe newScores
    }
  }
}
