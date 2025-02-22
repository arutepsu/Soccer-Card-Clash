package model.playingFiledComponent.strategy.scoringStrategy

import model.playerComponent.IPlayer
import model.playingFiledComponent.IPlayingField
import model.playingFiledComponent.strategy.scoringStrategy.IScoringStrategy

trait IPlayerScores {
  def getScorePlayer1: Int
  def getScorePlayer2: Int
  def setScoringStrategy(strategy: IScoringStrategy): Unit
  def scoreGoal(player: IPlayer): Unit
  def setScorePlayer1(score: Int): Unit
  def setScorePlayer2(score: Int): Unit
}
