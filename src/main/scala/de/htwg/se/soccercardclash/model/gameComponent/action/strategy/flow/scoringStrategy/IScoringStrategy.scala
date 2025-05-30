package de.htwg.se.soccercardclash.model.gameComponent.action.strategy.flow.scoringStrategy

trait IScoringStrategy {
  def calculatePoints(currentScore: Int): Int
}
