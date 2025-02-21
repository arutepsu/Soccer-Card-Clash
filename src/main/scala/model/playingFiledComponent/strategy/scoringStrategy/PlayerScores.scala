package model.playingFiledComponent.strategy.scoringStrategy

import model.playerComponent.IPlayer
import model.playingFiledComponent.IPlayingField
import com.google.inject.{Inject, Singleton}
import model.playerComponent.base.factories.IPlayerFactory

@Singleton
class PlayerScores @Inject() (
                               playingField: IPlayingField,
                               player1: IPlayer,
                               player2: IPlayer,
                               private var scoringStrategy: ScoringStrategy = new StandardScoring()
                             ) extends IPlayerScores {

  private var player1Score: Int = 0
  private var player2Score: Int = 0

  override def getScorePlayer1: Int = player1Score
  override def getScorePlayer2: Int = player2Score

  override def setScoringStrategy(strategy: ScoringStrategy): Unit = {
    scoringStrategy = strategy
    println(s"🔄 Scoring strategy updated to: ${strategy.getClass.getSimpleName}")
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
}
