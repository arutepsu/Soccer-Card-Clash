package de.htwg.se.soccercardclash.model.gameComponent.components

import com.google.inject.{Inject, Singleton}
import de.htwg.se.soccercardclash.model.gameComponent.IGameState
import de.htwg.se.soccercardclash.model.gameComponent.action.strategy.scoringStrategy.IScoringStrategy
import de.htwg.se.soccercardclash.model.gameComponent.action.strategy.scoringStrategy.base.StandardScoring
import de.htwg.se.soccercardclash.model.playerComponent.IPlayer
import de.htwg.se.soccercardclash.model.playerComponent.factory.IPlayerFactory
import de.htwg.se.soccercardclash.util.*
import play.api.libs.json.{JsObject, Json}

import scala.xml.Elem

trait IScoresFactory {
  def create(players: Seq[IPlayer]): IScores

  def createWithScores(playerScores: Map[IPlayer, Int]): IScores
}

class ScoresFactory extends IScoresFactory {
  override def create(players: Seq[IPlayer]): IScores = {
    val initialScores = players.map(p => p -> 0).toMap
    Scores(initialScores)
  }

  override def createWithScores(playerScores: Map[IPlayer, Int]): IScores = {
    Scores(playerScores)
  }
}


case class Scores(
                   playerToScore: Map[IPlayer, Int],
                   scoringStrategy: IScoringStrategy = new StandardScoring()
                 ) extends IScores {

  override def getScore(player: IPlayer): Int =
    playerToScore.getOrElse(player, 0)

  override def scoreGoal(player: IPlayer): (IScores, List[StateEvent]) = {
    val current = playerToScore.getOrElse(player, 0)
    val updatedScore = scoringStrategy.calculatePoints(current)
    val updatedMap = playerToScore.updated(player, updatedScore)
    val updated = this.copy(playerToScore = updatedMap)

    val events = List(StateEvent.ScoreEvent(player)) ++ updated.checkForWinner()
    (updated, events)
  }

  private def checkForWinner(): List[StateEvent] = {
    playerToScore.collectFirst {
      case (player, score) if score >= 3 => StateEvent.GameOver(player)
    }.toList
  }

  override def updateScoringStrategy(strategy: IScoringStrategy): IScores =
    this.copy(scoringStrategy = strategy)

  override def newScore(player: IPlayer, score: Int): IScores =
    this.copy(playerToScore = playerToScore.updated(player, score))
}

trait IScores {
  def getScore(player: IPlayer): Int

  def updateScoringStrategy(strategy: IScoringStrategy): IScores

  def scoreGoal(player: IPlayer): (IScores, List[StateEvent])

  def newScore(player: IPlayer, score: Int): IScores

}
