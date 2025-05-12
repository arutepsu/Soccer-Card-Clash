package de.htwg.se.soccercardclash.model.gameComponent.state.strategy.scoringStrategy.base

import de.htwg.se.soccercardclash.model.gameComponent.state.strategy.scoringStrategy.IScoringStrategy

class StandardScoring extends IScoringStrategy {
  override def calculatePoints(currentScore: Int): Int = currentScore + 1
}