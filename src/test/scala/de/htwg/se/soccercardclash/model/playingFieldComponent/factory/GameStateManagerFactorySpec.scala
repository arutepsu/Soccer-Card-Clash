package de.htwg.se.soccercardclash.model.playingFieldComponent.factory

import de.htwg.se.soccercardclash.model.gameComponent.state.factory.PlayingFieldManagerFactory
import de.htwg.se.soccercardclash.model.gameComponent.state.manager.*
import de.htwg.se.soccercardclash.model.gameComponent.state.strategy.scoringStrategy.IPlayerScores
import de.htwg.se.soccercardclash.model.gameComponent.state.IGameState
import de.htwg.se.soccercardclash.model.gameComponent.state.components.{DataManager, IFieldCards, IHandCards, Roles, Scores}
import de.htwg.se.soccercardclash.model.playerComponent.IPlayer
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.mockito.MockitoSugar

class GameStateManagerFactorySpec extends AnyWordSpec with Matchers with MockitoSugar {
  "PlayingFieldManagerFactory" should {
    "create all required managers" in {
      val handManager = mock[IHandCards]
      val fieldManager = mock[IFieldCards]
      val playingField = mock[IGameState]
      val player1 = mock[IPlayer]
      val player2 = mock[IPlayer]
      val playerActionManager = mock[IPlayerActionManager]
      val factory = new PlayingFieldManagerFactory(handManager, fieldManager, playerActionManager)

      factory.createDataManager(playingField, player1, player2) shouldBe a[DataManager]
      factory.createActionManager(playingField) shouldBe a[ActionManager]
      factory.createRolesManager(playingField, player1, player2) shouldBe a[Roles]
      factory.createScoresManager(playingField, player1, player2) shouldBe a[Scores]
    }
  }
}
