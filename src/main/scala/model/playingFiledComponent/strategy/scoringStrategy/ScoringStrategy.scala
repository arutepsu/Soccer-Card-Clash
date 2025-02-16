package model.playingFiledComponent.strategy.scoringStrategy

trait ScoringStrategy {
  def calculatePoints(currentScore: Int): Int
}
