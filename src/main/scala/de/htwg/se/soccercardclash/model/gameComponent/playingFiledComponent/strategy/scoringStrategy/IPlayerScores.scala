package de.htwg.se.soccercardclash.model.gameComponent.playingFiledComponent.strategy.scoringStrategy

import de.htwg.se.soccercardclash.model.playerComponent.IPlayer
import de.htwg.se.soccercardclash.model.gameComponent.playingFiledComponent.IPlayingField
import de.htwg.se.soccercardclash.model.gameComponent.playingFiledComponent.strategy.scoringStrategy.IScoringStrategy
import play.api.libs.json._
import scala.xml._
trait IPlayerScores {
  def getScorePlayer1: Int
  def getScorePlayer2: Int
  def setScoringStrategy(strategy: IScoringStrategy): Unit
  def scoreGoal(player: IPlayer): Unit
  def setScorePlayer1(score: Int): Unit
  def setScorePlayer2(score: Int): Unit

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
  def checkForWinner(): Unit
  def reset(): Unit
}
