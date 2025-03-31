package de.htwg.se.soccercardclash.model.gameComponent.playingFiledComponent.strategy.scoringStrategy

trait IScoringStrategy {
  def calculatePoints(currentScore: Int): Int
}
