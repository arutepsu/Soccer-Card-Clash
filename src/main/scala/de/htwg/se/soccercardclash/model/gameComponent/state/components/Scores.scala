package de.htwg.se.soccercardclash.model.gameComponent.state.components

import com.google.inject.{Inject, Singleton}
import de.htwg.se.soccercardclash.model.gameComponent.state.IGameState
import de.htwg.se.soccercardclash.model.gameComponent.state.strategy.scoringStrategy.IScoringStrategy
import de.htwg.se.soccercardclash.model.gameComponent.state.strategy.scoringStrategy.base.StandardScoring
import de.htwg.se.soccercardclash.model.playerComponent.IPlayer
import de.htwg.se.soccercardclash.model.playerComponent.factory.IPlayerFactory
import de.htwg.se.soccercardclash.util.{Events, ObservableEvent}
import play.api.libs.json.{JsObject, Json}

import scala.xml.Elem

trait IScoresFactory {
  def create(player1: IPlayer, player2: IPlayer): IScores
}
class ScoresFactory extends IScoresFactory {
  override def create(player1: IPlayer, player2: IPlayer): IScores =
    Scores(player1, player2)
}

case class Scores(
                         player1: IPlayer,
                         player2: IPlayer,
                         player1Score: Int = 0,
                         player2Score: Int = 0,
                         scoringStrategy: IScoringStrategy = new StandardScoring()
                       ) extends IScores {

  override def getScorePlayer1: Int = player1Score
  override def getScorePlayer2: Int = player2Score

  override def scoreGoal(player: IPlayer): (IScores, List[Events]) = {
    val (newScore1, newScore2) =
      if (player == player1)
        (scoringStrategy.calculatePoints(player1Score), player2Score)
      else
        (player1Score, scoringStrategy.calculatePoints(player2Score))

    val updated = this.copy(player1Score = newScore1, player2Score = newScore2)
    val events = List(Events.ScoreEvent(player)) ++ updated.checkForWinner()
    (updated, events)
  }

  override def setScoringStrategy(strategy: IScoringStrategy): IScores =
    this.copy(scoringStrategy = strategy)

  override def setScorePlayer1(score: Int): IScores =
    this.copy(player1Score = score)

  override def setScorePlayer2(score: Int): IScores =
    this.copy(player2Score = score)

  override def reset(): IScores =
    this.copy(player1Score = 0, player2Score = 0)

  private def checkForWinner(): List[Events] = {
    if (player1Score >= 3) List(Events.GameOver(player1))
    else if (player2Score >= 3) List(Events.GameOver(player2))
    else Nil
  }
}
trait IScores {
  def getScorePlayer1: Int

  def getScorePlayer2: Int

  def setScoringStrategy(strategy: IScoringStrategy): IScores

  def scoreGoal(player: IPlayer): (IScores, List[Events])

  def setScorePlayer1(score: Int): IScores

  def setScorePlayer2(score: Int): IScores

  def reset(): IScores

  def toXml: Elem = {
    <PlayerScores>
      <ScorePlayer1>
        {getScorePlayer1}
      </ScorePlayer1>
      <ScorePlayer2>
        {getScorePlayer2}
      </ScorePlayer2>
    </PlayerScores>
  }

  def toJson: JsObject = Json.obj(
    "scorePlayer1" -> getScorePlayer1,
    "scorePlayer2" -> getScorePlayer2
  )
}
