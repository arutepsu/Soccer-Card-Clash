package de.htwg.se.soccercardclash.model.gameComponent.action.strategy.flow.scoringStrategy

import de.htwg.se.soccercardclash.model.gameComponent.action.strategy.flow.scoringStrategy.base.StandardScoring
import de.htwg.se.soccercardclash.model.gameComponent.action.strategy.flow.scoringStrategy.IScoringStrategy
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