package model.playingFiledComponent.strategy.scoringStrategy

trait IScoringStrategy {
  def calculatePoints(currentScore: Int): Int
}
