package de.htwg.se.soccercardclash.model.playingFieldComponent.strategy.scoringStrategy.base

import de.htwg.se.soccercardclash.model.gameComponent.state.strategy.scoringStrategy.IScoringStrategy
import de.htwg.se.soccercardclash.model.gameComponent.state.strategy.scoringStrategy.base.DoublePointsScoring
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class DoublePointsScoringTest extends AnyFlatSpec with Matchers {

  "DoublePointsScoring" should "add 2 points to the current score" in {
    val strategy: IScoringStrategy = new DoublePointsScoring

    strategy.calculatePoints(0) shouldBe 2
    strategy.calculatePoints(5) shouldBe 7
    strategy.calculatePoints(-1) shouldBe 1
    strategy.calculatePoints(100) shouldBe 102
  }
}
