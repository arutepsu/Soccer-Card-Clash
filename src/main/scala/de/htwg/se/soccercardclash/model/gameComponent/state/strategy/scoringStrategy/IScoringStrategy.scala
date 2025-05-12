package de.htwg.se.soccercardclash.model.gameComponent.state.strategy.scoringStrategy

trait IScoringStrategy {
  def calculatePoints(currentScore: Int): Int
}
