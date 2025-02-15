package model.playingFiledComponent
import model.playerComponent.Player

class PlayerScores(player1: Player, player2: Player, playingField: PlayingField) {
  private var player1Score: Int = 0
  private var player2Score: Int = 0

  def getScorePlayer1: Int = player1Score
  def getScorePlayer2: Int = player2Score

  def scoreGoal(player: Player): Unit = {
    if (player == player1) {
      player1Score += 1
    } else {
      player2Score += 1
    }
    playingField.notifyObservers()
  }

  def setScorePlayer1(score: Int): Unit = {
    player1Score = score;
    playingField.notifyObservers()
  }

  def setScorePlayer2(score: Int): Unit = {
    player2Score = score;
    playingField.notifyObservers()
  }
}