package de.htwg.se.soccercardclash.model.playingFiledComponent.strategy.scoringStrategy

trait IScoringStrategy {
  def calculatePoints(currentScore: Int): Int
}
