package de.htwg.se.soccercardclash.model.gameComponent.action.strategy.scoringStrategy

trait IScoringStrategy {
  def calculatePoints(currentScore: Int): Int
}
