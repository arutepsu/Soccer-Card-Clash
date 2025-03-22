package model.playingFieldComponent.factory

import model.playingFiledComponent.factory.PlayingFieldManagerFactory
import model.playingFiledComponent.manager._
import model.playingFiledComponent.strategy.scoringStrategy.base.PlayerScores
import model.playingFiledComponent.strategy.scoringStrategy.IPlayerScores
import model.playingFiledComponent.IPlayingField
import model.playerComponent.IPlayer

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.mockito.MockitoSugar

class PlayingFieldManagerFactorySpec extends AnyWordSpec with Matchers with MockitoSugar {
  "PlayingFieldManagerFactory" should {
    "create all required managers" in {
      val handManager = mock[IPlayerHandManager]
      val fieldManager = mock[IPlayerFieldManager]
      val playingField = mock[IPlayingField]
      val player1 = mock[IPlayer]
      val player2 = mock[IPlayer]

      val factory = new PlayingFieldManagerFactory(handManager, fieldManager)

      factory.createDataManager(playingField, player1, player2) shouldBe a[DataManager]
      factory.createActionManager(playingField) shouldBe a[ActionManager]
      factory.createRolesManager(playingField, player1, player2) shouldBe a[RolesManager]
      factory.createScoresManager(playingField, player1, player2) shouldBe a[PlayerScores]
    }
  }
}
