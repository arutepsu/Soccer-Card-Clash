package de.htwg.se.soccercardclash.model.playingFieldComponent.manager

import de.htwg.se.soccercardclash.model.playerComponent.IPlayer
import de.htwg.se.soccercardclash.model.playerComponent.base.Player
import de.htwg.se.soccercardclash.model.gameComponent.state.IGameState
import de.htwg.se.soccercardclash.model.gameComponent.state.components.{IDataManager, IRoles, Roles}
import de.htwg.se.soccercardclash.model.gameComponent.state.manager.IActionManager
import de.htwg.se.soccercardclash.model.gameComponent.state.strategy.scoringStrategy.IPlayerScores
import de.htwg.se.soccercardclash.util.Observable
import org.mockito.Mockito.*
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.mockito.MockitoSugar

class ObservableGameState extends Observable with IGameState {

  private val dummyDataManager = mock(classOf[IDataManager])
  private val dummyActionManager = mock(classOf[IActionManager])
  private val dummyRolesManager = mock(classOf[IRoles])
  private val dummyPlayerScores = mock(classOf[IPlayerScores])

  override def getDataManager: IDataManager = dummyDataManager
  override def getActionManager: IActionManager = dummyActionManager
  override def getRoles: IRoles = dummyRolesManager
  override def getScores: IPlayerScores = dummyPlayerScores

  override def setPlayingField(): Unit = {}
  override def reset(): Unit = {}
}


class RolesSpec extends AnyWordSpec with Matchers with MockitoSugar {

  "A RolesManager" should {

    "initialize with attacker and defender" in {
      val mockField = mock[IGameState]
      val attacker = mock[IPlayer]
      val defender = mock[IPlayer]

      val manager = new Roles(mockField, attacker, defender)

      manager.attacker shouldBe attacker
      manager.defender shouldBe defender
    }

    "set new roles" in {
      val mockField = mock[IGameState]
      val attacker = mock[IPlayer]
      val defender = mock[IPlayer]
      val newAttacker = mock[IPlayer]
      val newDefender = mock[IPlayer]

      val manager = new Roles(mockField, attacker, defender)

      manager.setRoles(newAttacker, newDefender)

      manager.attacker shouldBe newAttacker
      manager.defender shouldBe newDefender
    }

    "reset roles to default players" in {
      val mockField = mock[IGameState]
      val attacker = mock[IPlayer]
      val defender = mock[IPlayer]

      val manager = new Roles(mockField, attacker, defender)
      manager.reset()

      manager.attacker shouldBe Player("NewAttacker")
      manager.defender shouldBe Player("NewDefender")
    }
    "switch roles and notify observers" in {
      val mockField = spy(new ObservableGameState)
      val attacker = mock[IPlayer]
      val defender = mock[IPlayer]

      val manager = new Roles(mockField, attacker, defender)

      manager.switchRoles()

      manager.attacker shouldBe defender
      manager.defender shouldBe attacker

      verify(mockField).notifyObservers()
    }

  }
}
