package model.playingFieldComponent.strategy.scoringStrategy.base

import model.playingFiledComponent.strategy.scoringStrategy.IScoringStrategy
import model.playingFiledComponent.strategy.scoringStrategy.base.StandardScoring
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class StandardScoringTest extends AnyFlatSpec with Matchers {

  "StandardScoring" should "add 1 point to the current score" in {
    val strategy: IScoringStrategy = new StandardScoring

    strategy.calculatePoints(0) shouldBe 1
    strategy.calculatePoints(5) shouldBe 6
    strategy.calculatePoints(-1) shouldBe 0
    strategy.calculatePoints(100) shouldBe 101
  }
}
