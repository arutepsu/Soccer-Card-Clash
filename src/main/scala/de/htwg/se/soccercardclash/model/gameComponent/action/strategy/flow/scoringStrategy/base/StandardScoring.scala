package de.htwg.se.soccercardclash.model.gameComponent.action.strategy.flow.scoringStrategy.base

import de.htwg.se.soccercardclash.model.gameComponent.action.strategy.flow.scoringStrategy.IScoringStrategy

class StandardScoring extends IScoringStrategy {
  override def calculatePoints(currentScore: Int): Int = currentScore + 1
}