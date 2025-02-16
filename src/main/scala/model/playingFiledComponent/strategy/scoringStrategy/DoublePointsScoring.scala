package model.playingFiledComponent.strategy.scoringStrategy

class DoublePointsScoring extends ScoringStrategy {
  override def calculatePoints(currentScore: Int): Int = currentScore + 2
}