package de.htwg.se.soccercardclash.model.gameComponent.state.components

import de.htwg.se.soccercardclash.model.gameComponent.action.strategy.scoringStrategy.base.StandardScoring
import de.htwg.se.soccercardclash.model.gameComponent.action.strategy.scoringStrategy.IScoringStrategy
import de.htwg.se.soccercardclash.model.playerComponent.IPlayer
import de.htwg.se.soccercardclash.util.StateEvent
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.mockito.MockitoSugar
import org.mockito.Mockito._
import org.mockito.Mockito.when

class ScoresFactorySpec extends AnyWordSpec with Matchers with MockitoSugar {

  "A ScoresFactory" should {
    "create empty scores for players" in {
      val player1 = mock[IPlayer]
      val player2 = mock[IPlayer]
      val factory = new ScoresFactory

      val scores = factory.create(Seq(player1, player2))
      scores.getScore(player1) shouldBe 0
      scores.getScore(player2) shouldBe 0
    }

    "create scores with initial values" in {
      val player = mock[IPlayer]
      val factory = new ScoresFactory
      val scores = factory.createWithScores(Map(player -> 2))

      scores.getScore(player) shouldBe 2
    }
  }

  "Scores" should {
    "update score for a player" in {
      val player = mock[IPlayer]
      val scores = Scores(Map(player -> 1))

      val (updatedScores, events) = scores.scoreGoal(player)

      updatedScores.getScore(player) shouldBe 2 // StandardScoring adds 1
      events should contain(StateEvent.ScoreEvent(player))
    }

    "trigger GameOver when score reaches 3" in {
      val player = mock[IPlayer]
      val scores = Scores(Map(player -> 2))

      val (updated, events) = scores.scoreGoal(player)

      events should contain(StateEvent.GameOver(player))
    }

    "allow replacing scoring strategy" in {
      val player = mock[IPlayer]
      val scores = Scores(Map(player -> 1))
      val customStrategy = mock[IScoringStrategy]

      when(customStrategy.calculatePoints(1)).thenReturn(10)
      val updated = scores.updateScoringStrategy(customStrategy)
      val (finalScores, _) = updated.scoreGoal(player)

      finalScores.getScore(player) shouldBe 10
    }

    "set a new score directly" in {
      val player = mock[IPlayer]
      val scores = Scores(Map(player -> 0))

      val updated = scores.newScore(player, 7)
      updated.getScore(player) shouldBe 7
    }
  }
}
