package model.playingFieldComponent.strategy.scoringStrategy.base

import controller.GameOver
import model.playerComponent.IPlayer
import model.playingFiledComponent.IPlayingField
import model.playingFiledComponent.manager.{IActionManager, IDataManager, IRolesManager}
import model.playingFiledComponent.strategy.scoringStrategy.base.PlayerScores
import model.playingFiledComponent.strategy.scoringStrategy.{IPlayerScores, IScoringStrategy}
import org.mockito.Mockito.*
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import org.scalatestplus.mockito.MockitoSugar
import util.{Observable, ObservableEvent}

class PlayerScoresTest extends AnyFlatSpec with Matchers with MockitoSugar {

  class ObservableMockPlayingField extends Observable with IPlayingField with MockitoSugar {
    override def getDataManager: IDataManager = mock[IDataManager]
    override def getRoles: IRolesManager = mock[IRolesManager]
    override def getScores: IPlayerScores = mock[IPlayerScores]
    override def getActionManager: IActionManager = mock[IActionManager]
    override def getAttacker: IPlayer = mock[IPlayer]
    override def getDefender: IPlayer = mock[IPlayer]
    override def reset(): Unit = {}
    override def setPlayingField(): Unit = {}
    override def notifyObservers(e: ObservableEvent): Unit = super.notifyObservers(e)
  }

  "PlayerScores" should "initialize both scores to 0" in {
    val field = new ObservableMockPlayingField
    val player1 = mock[IPlayer]
    val player2 = mock[IPlayer]

    val scores = new PlayerScores(field, player1, player2)

    scores.getScorePlayer1 shouldBe 0
    scores.getScorePlayer2 shouldBe 0
  }

  it should "score a goal using the scoring strategy" in {
    val field = new ObservableMockPlayingField
    val player1 = mock[IPlayer]
    val player2 = mock[IPlayer]
    val strategy = mock[IScoringStrategy]

    when(strategy.calculatePoints(0)).thenReturn(5)

    val scores = new PlayerScores(field, player1, player2)
    scores.setScoringStrategy(strategy)

    scores.scoreGoal(player1)
    scores.getScorePlayer1 shouldBe 5
    scores.getScorePlayer2 shouldBe 0
  }

  it should "notify GameOver if player1 score reaches threshold" in {
    val player1 = mock[IPlayer]
    val player2 = mock[IPlayer]

    var captured: Option[GameOver] = None

    val testField = new ObservableMockPlayingField {
      override def notifyObservers(e: ObservableEvent): Unit = {
        e match {
          case g: GameOver => captured = Some(g)
          case _ => // ignore other events
        }
      }
    }

    val scores = new PlayerScores(testField, player1, player2)
    scores.setScorePlayer1(1) // triggers GameOver(player1)

    captured match {
      case Some(GameOver(winner)) => winner shouldBe player1
      case _ => fail("Expected GameOver event for player1")
    }
  }


  it should "notify GameOver if player2 score reaches threshold" in {
    val player1 = mock[IPlayer]
    val player2 = mock[IPlayer]

    var captured: Option[GameOver] = None

    val testField = new ObservableMockPlayingField {
      override def notifyObservers(e: ObservableEvent): Unit = {
        e match {
          case g: GameOver => captured = Some(g)
          case _ => // ignore other events
        }
      }
    }

    val scores = new PlayerScores(testField, player1, player2)
    scores.setScorePlayer2(1) // triggers GameOver(player2)

    captured match {
      case Some(GameOver(winner)) => winner shouldBe player2
      case _ => fail("Expected GameOver event for player2")
    }
  }

  it should "reset both scores to 0 and notify observers" in {
    val player1 = mock[IPlayer]
    val player2 = mock[IPlayer]

    var wasNotified = false
    val field = new ObservableMockPlayingField {
      override def notifyObservers(e: ObservableEvent): Unit = wasNotified = true
    }

    val scores = new PlayerScores(field, player1, player2)
    scores.setScorePlayer1(3)
    scores.setScorePlayer2(4)

    scores.reset()

    scores.getScorePlayer1 shouldBe 0
    scores.getScorePlayer2 shouldBe 0
    wasNotified shouldBe true
  }
}