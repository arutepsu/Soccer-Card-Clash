package model.playingFiledComponent.scoringStrategy

class DoublePointsScoring extends ScoringStrategy {
  override def calculatePoints(currentScore: Int): Int = currentScore + 2
}