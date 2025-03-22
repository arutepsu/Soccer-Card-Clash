package de.htwg.se.soccercardclash.model.playingFieldComponent.manager

import de.htwg.se.soccercardclash.model.playerComponent.IPlayer
import de.htwg.se.soccercardclash.model.playerComponent.base.Player
import de.htwg.se.soccercardclash.model.playingFiledComponent.IPlayingField
import de.htwg.se.soccercardclash.model.playingFiledComponent.manager.{IActionManager, IDataManager, IRolesManager, RolesManager}
import de.htwg.se.soccercardclash.model.playingFiledComponent.strategy.scoringStrategy.IPlayerScores
import de.htwg.se.soccercardclash.util.Observable
import org.mockito.Mockito.*
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.mockito.MockitoSugar

class ObservablePlayingField extends Observable with IPlayingField {
  override def getAttacker: IPlayer = ???
  override def getDefender: IPlayer = ???
  override def getDataManager: IDataManager = ???
  override def getActionManager: IActionManager = ???
  override def getRoles: IRolesManager = ???
  override def getScores: IPlayerScores = ???
  override def setPlayingField(): Unit = {}
  override def reset(): Unit = {}
}

class RolesManagerSpec extends AnyWordSpec with Matchers with MockitoSugar {

  "A RolesManager" should {

    "initialize with attacker and defender" in {
      val mockField = mock[IPlayingField]
      val attacker = mock[IPlayer]
      val defender = mock[IPlayer]

      val manager = new RolesManager(mockField, attacker, defender)

      manager.attacker shouldBe attacker
      manager.defender shouldBe defender
    }

    "set new roles" in {
      val mockField = mock[IPlayingField]
      val attacker = mock[IPlayer]
      val defender = mock[IPlayer]
      val newAttacker = mock[IPlayer]
      val newDefender = mock[IPlayer]

      val manager = new RolesManager(mockField, attacker, defender)

      manager.setRoles(newAttacker, newDefender)

      manager.attacker shouldBe newAttacker
      manager.defender shouldBe newDefender
    }

    "reset roles to default players" in {
      val mockField = mock[IPlayingField]
      val attacker = mock[IPlayer]
      val defender = mock[IPlayer]

      val manager = new RolesManager(mockField, attacker, defender)
      manager.reset()

      manager.attacker shouldBe Player("NewAttacker", List())
      manager.defender shouldBe Player("NewDefender", List())
    }
    "switch roles and notify observers" in {
      val mockField = spy(new ObservablePlayingField)
      val attacker = mock[IPlayer]
      val defender = mock[IPlayer]

      val manager = new RolesManager(mockField, attacker, defender)

      manager.switchRoles()

      manager.attacker shouldBe defender
      manager.defender shouldBe attacker

      verify(mockField).notifyObservers()
    }

  }
}
