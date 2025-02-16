package model.playingFiledComponent.scoringStrategy

class StandardScoring extends ScoringStrategy {
  override def calculatePoints(currentScore: Int): Int = currentScore + 1
}