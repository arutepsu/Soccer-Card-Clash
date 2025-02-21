package model.playingFiledComponent.strategy.scoringStrategy

import model.playerComponent.IPlayer
import model.playingFiledComponent.IPlayingField
import model.playingFiledComponent.strategy.scoringStrategy.ScoringStrategy

trait IPlayerScores {
  def getScorePlayer1: Int
  def getScorePlayer2: Int
  def setScoringStrategy(strategy: ScoringStrategy): Unit
  def scoreGoal(player: IPlayer): Unit
  def setScorePlayer1(score: Int): Unit
  def setScorePlayer2(score: Int): Unit
}
