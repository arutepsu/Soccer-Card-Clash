package model.playingFiledComponent.strategy.scoringStrategy.base

import com.google.inject.{Inject, Singleton}
import model.playerComponent.IPlayer
import model.playerComponent.factory.IPlayerFactory
import model.playingFiledComponent.IPlayingField
import model.playingFiledComponent.strategy.scoringStrategy.base.StandardScoring
import model.playingFiledComponent.strategy.scoringStrategy.{IPlayerScores, IScoringStrategy}

@Singleton
class PlayerScores @Inject() (
                               playingField: IPlayingField,
                               player1: IPlayer,
                               player2: IPlayer,
                               private var scoringStrategy: IScoringStrategy = new StandardScoring()
                             ) extends IPlayerScores {

  private var player1Score: Int = 0
  private var player2Score: Int = 0

  override def getScorePlayer1: Int = player1Score
  override def getScorePlayer2: Int = player2Score

  override def setScoringStrategy(strategy: IScoringStrategy): Unit = {
    scoringStrategy = strategy
  }

  override def scoreGoal(player: IPlayer): Unit = {
    if (player == player1) {
      player1Score = scoringStrategy.calculatePoints(player1Score)
    } else {
      player2Score = scoringStrategy.calculatePoints(player2Score)
    }
    playingField.notifyObservers()
  }

  override def setScorePlayer1(score: Int): Unit = {
    player1Score = score
    playingField.notifyObservers()
  }

  override def setScorePlayer2(score: Int): Unit = {
    player2Score = score
    playingField.notifyObservers()
  }

  override def reset(): Unit = {
    println("🔄 Resetting PlayerScores...")

    player1Score = 0
    player2Score = 0

    playingField.notifyObservers()
    println("✅ PlayerScores reset completed!")
  }
}
