package de.htwg.se.soccercardclash.model.playingFieldComponent.factory

import de.htwg.se.soccercardclash.model.gameComponent.state.manager.*
import de.htwg.se.soccercardclash.model.gameComponent.state.strategy.scoringStrategy.IPlayerScores
import de.htwg.se.soccercardclash.model.playerComponent.IPlayer
import de.htwg.se.soccercardclash.model.gameComponent.state.IGameState
import de.htwg.se.soccercardclash.model.gameComponent.state.base.GameState
import de.htwg.se.soccercardclash.model.gameComponent.state.components.{IGameCards, IRoles, IScores}
import de.htwg.se.soccercardclash.model.gameComponent.state.factory.{IPlayingFieldFactory, IPlayingFieldManagerFactory, PlayingFieldFactory}
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.mockito.MockitoSugar
import org.mockito.Mockito.*
import org.mockito.ArgumentMatchers.any

class GameStateFactorySpec extends AnyWordSpec with Matchers with MockitoSugar {

  "PlayingFieldFactory" should {

    "create a PlayingField instance with given players and injected manager" in {
      val player1 = mock[IPlayer]
      val player2 = mock[IPlayer]

      val mockManager = mock[IPlayingFieldManagerFactory]
      val mockDataManager = mock[IGameCards]
      val mockActionManager = mock[IActionManager]
      val mockRolesManager = mock[IRoles]
      val mockScores = mock[IScores]

      // Return mocked managers when PlayingField calls factory methods
      when(mockManager.createDataManager(any(), any(), any())).thenReturn(mockDataManager)
      when(mockManager.createActionManager(any())).thenReturn(mockActionManager)
      when(mockManager.createRolesManager(any(), any(), any())).thenReturn(mockRolesManager)
      when(mockManager.createScoresManager(any(), any(), any())).thenReturn(mockScores)

      // Required for getAttacker/getDefender
      when(mockRolesManager.attacker).thenReturn(player1)
      when(mockRolesManager.defender).thenReturn(player2)

      // Provide the given instance to satisfy `using` context
      given IPlayingFieldManagerFactory = mockManager

      val factory = new PlayingFieldFactory(mockManager)
      val playingField = factory.createPlayingField(player1, player2)

      playingField should not be null
      playingField shouldBe a[GameState]
      playingField.getRoles.attacker shouldBe player1
      playingField.getRoles.defender shouldBe player2
      playingField.getDataManager shouldBe mockDataManager
      playingField.getActionManager shouldBe mockActionManager
      playingField.getRoles shouldBe mockRolesManager
      playingField.getScores shouldBe mockScores
    }
  }
}