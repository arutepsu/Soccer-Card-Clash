package model.playingFiledComponent.strategy.scoringStrategy.base

import model.playingFiledComponent.strategy.scoringStrategy.IScoringStrategy

class StandardScoring extends IScoringStrategy {
  override def calculatePoints(currentScore: Int): Int = currentScore + 1
}