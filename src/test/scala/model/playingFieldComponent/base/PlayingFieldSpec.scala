package model.playingFieldComponent.base

import model.playerComponent.IPlayer
import model.playingFiledComponent.*
import model.playingFiledComponent.base.PlayingField
import model.playingFiledComponent.factory.IPlayingFieldManagerFactory
import model.playingFiledComponent.manager.*
import model.playingFiledComponent.strategy.scoringStrategy.IPlayerScores
import org.mockito.Mockito.*
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.mockito.MockitoSugar
import org.mockito.ArgumentMatchers.{any, eq => eqTo}

class PlayingFieldSpec extends AnyWordSpec with Matchers with MockitoSugar {

  // 🔁 Shared mocks and given instance available for all tests
  val mockFactory: IPlayingFieldManagerFactory = mock[IPlayingFieldManagerFactory]
  given IPlayingFieldManagerFactory = mockFactory // This makes `summonInline` work inside PlayingField

  "PlayingField" should {

    "initialize all managers using the factory" in {
      val player1 = mock[IPlayer]
      val player2 = mock[IPlayer]

      val dataManager = mock[IDataManager]
      val actionManager = mock[IActionManager]
      val rolesManager = mock[IRolesManager]
      val scores = mock[IPlayerScores]

      when(mockFactory.createDataManager(any(), eqTo(player1), eqTo(player2))).thenReturn(dataManager)
      when(mockFactory.createActionManager(any())).thenReturn(actionManager)
      when(mockFactory.createRolesManager(any(), eqTo(player1), eqTo(player2))).thenReturn(rolesManager)
      when(mockFactory.createScoresManager(any(), eqTo(player1), eqTo(player2))).thenReturn(scores)
      when(rolesManager.attacker).thenReturn(player1)
      when(rolesManager.defender).thenReturn(player2)

      val field = new PlayingField(player1, player2)

      field.getAttacker shouldBe player1
      field.getDefender shouldBe player2
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
      when(mockFactory.createRolesManager(any(), any(), any())).thenReturn(mock[IRolesManager])
      when(mockFactory.createScoresManager(any(), any(), any())).thenReturn(mock[IPlayerScores])

      val field = new PlayingField(player1, player2)
      field.setPlayingField()

      verify(dataManager).initializeFields()
    }

    "reset all internal state and notify observers" in {
      val player1 = mock[IPlayer]
      val player2 = mock[IPlayer]
      val dataManager = mock[IDataManager]
      val actionManager = mock[IActionManager]
      val roles = mock[IRolesManager]
      val scores = mock[IPlayerScores]

      when(mockFactory.createDataManager(any(), any(), any())).thenReturn(dataManager)
      when(mockFactory.createActionManager(any())).thenReturn(actionManager)
      when(mockFactory.createRolesManager(any(), any(), any())).thenReturn(roles)
      when(mockFactory.createScoresManager(any(), any(), any())).thenReturn(scores)

      val field = new PlayingField(player1, player2)
      field.reset()

      verify(dataManager).clearAll()
      verify(actionManager).reset()
      verify(roles).reset()
      verify(scores).reset()
    }
  }
}
