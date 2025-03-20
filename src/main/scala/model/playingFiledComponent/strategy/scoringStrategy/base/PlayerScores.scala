package model.playingFiledComponent.strategy.scoringStrategy.base

import com.google.inject.{Inject, Singleton}
import controller.GameOver
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
    checkForWinner()
    playingField.notifyObservers()
  }

  override def setScorePlayer1(score: Int): Unit = {
    player1Score = score
    checkForWinner()
    playingField.notifyObservers()
  }

  override def setScorePlayer2(score: Int): Unit = {
    player2Score = score
    checkForWinner()
    playingField.notifyObservers()
  }

  override def checkForWinner(): Unit = {
    if (player1Score >= 1) {
      playingField.notifyObservers(GameOver(player1))
    } else if (player2Score >= 1) {
      playingField.notifyObservers(GameOver(player2))
    }
  }


  override def reset(): Unit = {

    player1Score = 0
    player2Score = 0

    playingField.notifyObservers()
  }
}
