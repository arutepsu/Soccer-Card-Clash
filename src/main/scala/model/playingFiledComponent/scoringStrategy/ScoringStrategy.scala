package model.playingFiledComponent.scoringStrategy

trait ScoringStrategy {
  def calculatePoints(currentScore: Int): Int
}
