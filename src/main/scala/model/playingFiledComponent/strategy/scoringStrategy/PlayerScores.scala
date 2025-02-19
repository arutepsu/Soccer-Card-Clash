package model.playingFiledComponent.strategy.scoringStrategy

import model.playerComponent.IPlayer
import model.playingFiledComponent.PlayingField

class PlayerScores(
                    player1: IPlayer,
                    player2: IPlayer,
                    playingField: PlayingField,
                    private var scoringStrategy: ScoringStrategy = new StandardScoring() // ✅ Default Strategy
                  ) {
  private var player1Score: Int = 0
  private var player2Score: Int = 0

  def getScorePlayer1: Int = player1Score
  def getScorePlayer2: Int = player2Score

  def setScoringStrategy(strategy: ScoringStrategy): Unit = {
    scoringStrategy = strategy
    println(s"🔄 Scoring strategy updated to: ${strategy.getClass.getSimpleName}")
  }

  def scoreGoal(player: IPlayer): Unit = {
    if (player == player1) {
      player1Score = scoringStrategy.calculatePoints(player1Score)
    } else {
      player2Score = scoringStrategy.calculatePoints(player2Score)
    }
    playingField.notifyObservers()
  }

  def setScorePlayer1(score: Int): Unit = {
    player1Score = score
    playingField.notifyObservers()
  }

  def setScorePlayer2(score: Int): Unit = {
    player2Score = score
    playingField.notifyObservers()
  }
}
