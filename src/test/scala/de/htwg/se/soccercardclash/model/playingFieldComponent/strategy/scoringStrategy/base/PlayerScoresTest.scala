package de.htwg.se.soccercardclash.model.playingFieldComponent.strategy.scoringStrategy.base

import de.htwg.se.soccercardclash.model.playerComponent.IPlayer
import de.htwg.se.soccercardclash.model.gameComponent.state.IGameState
import de.htwg.se.soccercardclash.model.gameComponent.state.components.{IGameCards, IRoles, Scores}
import de.htwg.se.soccercardclash.model.gameComponent.state.manager.IActionManager
import de.htwg.se.soccercardclash.model.gameComponent.state.strategy.scoringStrategy.{IPlayerScores, IScoringStrategy}
import org.mockito.Mockito.*
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import org.scalatestplus.mockito.MockitoSugar
import de.htwg.se.soccercardclash.util.{GameOver, ScoreEvent, Observable, ObservableEvent}

class PlayerScoresTest extends AnyFlatSpec with Matchers with MockitoSugar {

  class ObservableMockGameState extends Observable with IGameState with MockitoSugar {
    override def getGameCards: IGameCards = mock[IGameCards]
    override def getRoles: IRoles = mock[IRoles]
    override def getScores: IPlayerScores = mock[IPlayerScores]
    override def getActionManager: IActionManager = mock[IActionManager]
    override def reset(): Unit = {}
    override def setPlayingField(): Unit = {}
    override def notifyObservers(e: ObservableEvent): Unit = super.notifyObservers(e)
  }

  "PlayerScores" should "initialize both scores to 0" in {
    val field = new ObservableMockGameState
    val player1 = mock[IPlayer]
    val player2 = mock[IPlayer]

    val scores = new Scores(field, player1, player2)

    scores.getScorePlayer1 shouldBe 0
    scores.getScorePlayer2 shouldBe 0
  }

  it should "score a goal using the scoring strategy" in {
    val field = new ObservableMockGameState
    val player1 = mock[IPlayer]
    val player2 = mock[IPlayer]
    val strategy = mock[IScoringStrategy]

    when(strategy.calculatePoints(0)).thenReturn(5)

    val scores = new Scores(field, player1, player2)
    scores.updateScoringStrategy(strategy)

    scores.scoreGoal(player1)
    scores.getScorePlayer1 shouldBe 5
    scores.getScorePlayer2 shouldBe 0
  }

  it should "notify GoalScoredEvent when a goal is scored" in {
    val player1 = mock[IPlayer]
    val player2 = mock[IPlayer]

    var captured: Option[ScoreEvent] = None

    val testField = new ObservableMockGameState {
      override def notifyObservers(e: ObservableEvent): Unit = {
        e match {
          case g: ScoreEvent => captured = Some(g)
          case _ => // ignore
        }
      }
    }

    val scores = new Scores(testField, player1, player2)
    scores.scoreGoal(player1)

    captured match {
      case Some(ScoreEvent(p)) => p shouldBe player1
      case _ => fail("Expected GoalScoredEvent for player1")
    }
  }

  it should "notify GameOver if player1 score reaches threshold via scoreGoal" in {
    val player1 = mock[IPlayer]
    val player2 = mock[IPlayer]
    val strategy = mock[IScoringStrategy]

    when(strategy.calculatePoints(0)).thenReturn(3)

    var captured: Option[GameOver] = None

    val field = new ObservableMockGameState {
      override def notifyObservers(e: ObservableEvent): Unit = {
        e match {
          case g: GameOver => captured = Some(g)
          case _ => // ignore
        }
      }
    }

    val scores = new Scores(field, player1, player2)
    scores.updateScoringStrategy(strategy)
    scores.scoreGoal(player1) // triggers GameOver

    captured match {
      case Some(GameOver(winner)) => winner shouldBe theSameInstanceAs(player1) // ✅ Fixed
      case _ => fail("Expected GameOver event for player1")
    }
  }

  it should "notify GameOver if player2 score reaches threshold via scoreGoal" in {
    val player1 = mock[IPlayer]
    val player2 = mock[IPlayer]
    val strategy = mock[IScoringStrategy]

    when(strategy.calculatePoints(0)).thenReturn(3)

    var captured: Option[GameOver] = None

    val field = new ObservableMockGameState {
      override def notifyObservers(e: ObservableEvent): Unit = {
        e match {
          case g: GameOver => captured = Some(g)
          case _ => // ignore
        }
      }
    }

    val scores = new Scores(field, player1, player2)
    scores.updateScoringStrategy(strategy)
    scores.scoreGoal(player2) // triggers GameOver

    captured match {
      case Some(GameOver(winner)) => winner shouldBe theSameInstanceAs(player2) // ✅ Fixed
      case _ => fail("Expected GameOver event for player2")
    }
  }

  it should "notify both GoalScoredEvent and GameOver when score reaches threshold" in {
    val player1 = mock[IPlayer]
    val player2 = mock[IPlayer]
    val strategy = mock[IScoringStrategy]

    when(strategy.calculatePoints(0)).thenReturn(3)

    var goalEventTriggered = false
    var gameOverTriggered = false

    val field = new ObservableMockGameState {
      override def notifyObservers(e: ObservableEvent): Unit = {
        e match {
          case ScoreEvent(p) if p == player1 => goalEventTriggered = true
          case GameOver(p) if p == player1        => gameOverTriggered = true
          case _ => // ignore
        }
      }
    }

    val scores = new Scores(field, player1, player2)
    scores.updateScoringStrategy(strategy)
    scores.scoreGoal(player1)

    goalEventTriggered shouldBe true
    gameOverTriggered shouldBe true
  }

  it should "reset both scores to 0 and notify observers" in {
    val player1 = mock[IPlayer]
    val player2 = mock[IPlayer]

    var wasNotified = false
    val field = new ObservableMockGameState {
      override def notifyObservers(e: ObservableEvent): Unit = wasNotified = true
    }

    val scores = new Scores(field, player1, player2)
    scores.setScorePlayer1(3)
    scores.setScorePlayer2(4)

    scores.reset()

    scores.getScorePlayer1 shouldBe 0
    scores.getScorePlayer2 shouldBe 0
    wasNotified shouldBe true
  }
}
