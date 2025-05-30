package de.htwg.se.soccercardclash.model.gameComponent.components

import de.htwg.se.soccercardclash.model.gameComponent.action.strategy.flow.scoringStrategy.base.*
import de.htwg.se.soccercardclash.model.gameComponent.action.strategy.flow.scoringStrategy.IScoringStrategy
import de.htwg.se.soccercardclash.model.gameComponent.components.*
import de.htwg.se.soccercardclash.model.playerComponent.IPlayer
import de.htwg.se.soccercardclash.util.StateEvent
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.mockito.MockitoSugar
import org.mockito.Mockito.*
import org.mockito.Mockito.when

class ScoresFactorySpec extends AnyWordSpec with Matchers with MockitoSugar {

  "A ScoresFactory" should {
    "create empty scores for players" in {
      val player1 = mock[IPlayer]
      val player2 = mock[IPlayer]
      val scoringStrategy = mock[IScoringStrategy]
      val factory = new ScoresFactory(scoringStrategy)

      val scores = factory.create(Seq(player1, player2))
      scores.getScore(player1) shouldBe 0
      scores.getScore(player2) shouldBe 0
    }

    "create scores with initial values" in {
      val player = mock[IPlayer]
      val scoringStrategy = mock[IScoringStrategy]
      val factory = new ScoresFactory(scoringStrategy)

      val scores = factory.createWithScores(Map(player -> 2))
      scores.getScore(player) shouldBe 2
    }
  }

  "Scores" should {
    "update score for a player" in {
      val player = mock[IPlayer]
      val scoringStrategy = mock[IScoringStrategy]
      when(scoringStrategy.calculatePoints(1)).thenReturn(2)

      val scores = Scores(Map(player -> 1), scoringStrategy)
      val (updatedScores, events) = scores.scoreGoal(player)

      updatedScores.getScore(player) shouldBe 2
      events should contain(StateEvent.ScoreEvent(player))
    }

    "trigger GameOver when score reaches 3" in {
      val player = mock[IPlayer]
      val scoringStrategy = mock[IScoringStrategy]
      when(scoringStrategy.calculatePoints(2)).thenReturn(3)

      val scores = Scores(Map(player -> 2), scoringStrategy)
      val (updated, events) = scores.scoreGoal(player)

      events should contain(StateEvent.GameOver(player))
    }

    "allow replacing scoring strategy" in {
      val player = mock[IPlayer]
      val initialStrategy = mock[IScoringStrategy]
      val newStrategy = mock[IScoringStrategy]

      when(newStrategy.calculatePoints(1)).thenReturn(10)

      val scores = Scores(Map(player -> 1), initialStrategy)
      val updated = scores.updateScoringStrategy(newStrategy)
      val (finalScores, _) = updated.scoreGoal(player)

      finalScores.getScore(player) shouldBe 10
    }

    "set a new score directly" in {
      val player = mock[IPlayer]
      val scoringStrategy = mock[IScoringStrategy]
      val scores = Scores(Map(player -> 0), scoringStrategy)

      val updated = scores.newScore(player, 7)
      updated.getScore(player) shouldBe 7
    }
  }
}