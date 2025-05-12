package de.htwg.se.soccercardclash.model.playingFieldComponent.base

import de.htwg.se.soccercardclash.model.playerComponent.IPlayer
import de.htwg.se.soccercardclash.model.gameComponent.state.*
import de.htwg.se.soccercardclash.model.gameComponent.state.base.GameState
import de.htwg.se.soccercardclash.model.gameComponent.state.components.{IDataManager, IRoles, IScores}
import de.htwg.se.soccercardclash.model.gameComponent.state.factory.IPlayingFieldManagerFactory
import de.htwg.se.soccercardclash.model.gameComponent.state.manager.*
import de.htwg.se.soccercardclash.model.gameComponent.state.strategy.scoringStrategy.IPlayerScores
import org.mockito.Mockito.*
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.mockito.MockitoSugar
import org.mockito.ArgumentMatchers.{any, eq as eqTo}

class GameStateSpec extends AnyWordSpec with Matchers with MockitoSugar {

  // 🔁 Shared mocks and given instance available for all tests
  val mockFactory: IPlayingFieldManagerFactory = mock[IPlayingFieldManagerFactory]
  given IPlayingFieldManagerFactory = mockFactory // This makes `summonInline` work inside PlayingField

  "PlayingField" should {

    "initialize all managers using the factory" in {
      val player1 = mock[IPlayer]
      val player2 = mock[IPlayer]

      val dataManager = mock[IDataManager]
      val actionManager = mock[IActionManager]
      val rolesManager = mock[IRoles]
      val scores = mock[IScores]

      when(mockFactory.createDataManager(any(), eqTo(player1), eqTo(player2))).thenReturn(dataManager)
      when(mockFactory.createActionManager(any())).thenReturn(actionManager)
      when(mockFactory.createRolesManager(any(), eqTo(player1), eqTo(player2))).thenReturn(rolesManager)
      when(mockFactory.createScoresManager(any(), eqTo(player1), eqTo(player2))).thenReturn(scores)
      when(rolesManager.attacker).thenReturn(player1)
      when(rolesManager.defender).thenReturn(player2)

      val field = new GameState(player1, player2)

      field.getRoles.attacker shouldBe player1
      field.getRoles.defender shouldBe player2
      field.getDataManager shouldBe dataManager
      field.getActionManager shouldBe actionManager
      field.getRoles shouldBe rolesManager
      field.getScores shouldBe scores
    }

    "delegate setPlayingField to DataManager" in {
      val player1 = mock[IPlayer]
      val player2 = mock[IPlayer]
      val dataManager = mock[IDataManager]

      when(mockFactory.createDataManager(any(), any(), any())).thenReturn(dataManager)
      when(mockFactory.createActionManager(any())).thenReturn(mock[IActionManager])
      when(mockFactory.createRolesManager(any(), any(), any())).thenReturn(mock[IRoles])
      when(mockFactory.createScoresManager(any(), any(), any())).thenReturn(mock[IScores])

      val field = new GameState(player1, player2)
      field.setPlayingField()

      verify(dataManager).initializeFields()
    }

    "reset all internal state and notify observers" in {
      val player1 = mock[IPlayer]
      val player2 = mock[IPlayer]
      val dataManager = mock[IDataManager]
      val actionManager = mock[IActionManager]
      val roles = mock[IRoles]
      val scores = mock[IScores]

      when(mockFactory.createDataManager(any(), any(), any())).thenReturn(dataManager)
      when(mockFactory.createActionManager(any())).thenReturn(actionManager)
      when(mockFactory.createRolesManager(any(), any(), any())).thenReturn(roles)
      when(mockFactory.createScoresManager(any(), any(), any())).thenReturn(scores)

      val field = new GameState(player1, player2)
      field.reset()

      verify(dataManager).clearAll()
      verify(actionManager).reset()
      verify(roles).reset()
      verify(scores).reset()
    }
  }
}
