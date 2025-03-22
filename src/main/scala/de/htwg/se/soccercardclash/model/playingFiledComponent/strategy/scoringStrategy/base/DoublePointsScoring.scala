package de.htwg.se.soccercardclash.model.playingFiledComponent.strategy.scoringStrategy.base

import de.htwg.se.soccercardclash.model.playingFiledComponent.strategy.scoringStrategy.IScoringStrategy

class DoublePointsScoring extends IScoringStrategy {
  override def calculatePoints(currentScore: Int): Int = currentScore + 2
}